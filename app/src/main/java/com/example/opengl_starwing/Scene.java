package com.example.opengl_starwing;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

public class Scene {
    private float speed = 1f;
    private final List<SceneDrawable> dyObjs;
    private final List<Enemy> stObjs;
    private final CopyOnWriteArrayList<Projectile> projectiles; // Array safe for threading
    private final GroundPoints gp;
    private final float x, y, initialZ;
    private float z, newZ, armwingZ;
    private final Random random = new Random();
    private final int spawningNum = 1;    // Number to spawn building
    private final float resetThreshold; // Set a threshold for when to reset the Ground Points
    private final float projectileDespawnThreshold = -790f;
    private int maxEnemies, enemiesSpawned;
    private long modeStartTime = 0;  // Time when the spawn mode started

    private final ObjectPool<Building> buildingPool;
    private final ObjectPool<Portal> portalPool;
    private final ObjectPool<Enemy> enemyPool;
    private final ObjectPool<Projectile> projectilePool;

    private final Armwing armwing;

    private enum SpawnMode {
        BUILDINGS_AND_PORTALS,
        ENEMIES
    }

    private SpawnMode currentSpawnMode = SpawnMode.BUILDINGS_AND_PORTALS;
    private long lastModeSwitchTime = System.currentTimeMillis(); // Track time for mode switching

    public Scene(GL10 gl, Context context, float x, float y, float z, Armwing armwing) {
        dyObjs = new ArrayList<>(64);
        stObjs = new ArrayList<>(16);
        projectiles = new CopyOnWriteArrayList<Projectile>();
        this.x = -x;
        this.y = y;
        this.z = -z;
        initialZ = -z;
        resetThreshold = z - 1;

        gp = new GroundPoints(84, 20, 42, 12, this.z);
        gp.setPosition(0, y, -z / 3);

        // Initialize the ObjectPools for Building and Portal
        buildingPool = new ObjectPool<Building>(gl, context, Building::new, 40, 50); // Max 50 buildings in pool
        portalPool = new ObjectPool<Portal>(gl, context, Portal::new, 10, 10); // Max 10 portals in pool
        projectilePool = new ObjectPool<Projectile>(gl, context, Projectile::new, 128, 128); // Max 128 projectiles in pool
        enemyPool = new ObjectPool<Enemy>(gl, context, Enemy::new, 16, 16); // Max 16 enemies in pool

        this.armwing = armwing;
    }

    public void addDyLmn(SceneDrawable lmn) {
        dyObjs.add(lmn);
    }

    public void addStLmn(Enemy lmn) {
        stObjs.add(lmn);
    }

    public void draw(GL10 gl) {
        checkCollisions();

        switchSpawnMode();

        z += speed;
        newZ += speed / 12;
        armwingZ += speed;

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        gp.checkAndResetPosition(z, resetThreshold, speed, initialZ);
        gp.draw(gl);

        despawnObjects();
        deleteProjectiles();

        // Draw opaque objects first
        for (SceneDrawable lmn : dyObjs) {
            lmn.updateScenePos(lmn.getScenePos() + speed);
            lmn.draw(gl);
        }

        for (Enemy enemy : stObjs) {
            enemy.draw(gl);
        }

        for (Projectile projectile : projectiles) {
            projectile.updateScenePos(projectile.getScenePos());
            projectile.draw(gl);
        }

        // Draw semi-transparent objects after
        for (SceneDrawable lmn : dyObjs) {
            if (lmn instanceof Portal) {
                ((Portal) lmn).drawInnerPortal(gl);
            }
        }
        gl.glPopMatrix();
    }

    private void switchSpawnMode() {
        long currentTime = System.currentTimeMillis();

        // Calculate elapsed time in seconds
        long elapsedTime = (currentTime - lastModeSwitchTime) / 1000;

        // Print the elapsed time for the current spawn mode
        System.out.println("Current spawn mode: " + currentSpawnMode + " | Elapsed time: " + elapsedTime + " seconds");

        // Check if all enemies are defeated
        if (currentSpawnMode == SpawnMode.ENEMIES && stObjs.isEmpty()) {
            currentSpawnMode = SpawnMode.BUILDINGS_AND_PORTALS;
            lastModeSwitchTime = currentTime;
        }

        // Switch spawn modes based on time
        if (currentTime - lastModeSwitchTime > 20000) { // Mode duration (20 seconds)
            if (currentSpawnMode == SpawnMode.BUILDINGS_AND_PORTALS) {
                startEnemySpawnMode();
                lastModeSwitchTime = currentTime;
            }
        }

        // Check if the time limit for killing enemies has passed
        int spawnTimeLimit = 30000; // 30 seconds
        boolean enemyTimeLimitReached = currentTime - modeStartTime >= spawnTimeLimit;

        if (currentSpawnMode == SpawnMode.BUILDINGS_AND_PORTALS ||
                (currentSpawnMode == SpawnMode.ENEMIES && spawnTimeLimit - (currentTime - modeStartTime) <= 4000)) {
            spawnBuilding(); // Spawn buildings even during ENEMIES mode, 4 seconds before it ends
        }

        if (currentSpawnMode == SpawnMode.BUILDINGS_AND_PORTALS) {
            spawnPortal();
        } else if (currentSpawnMode == SpawnMode.ENEMIES) {
            spawnEnemy();

            if (enemyTimeLimitReached) {
                despawnEnemies();  // Remove all enemies if the time is up
                currentSpawnMode = SpawnMode.BUILDINGS_AND_PORTALS; // Switch back to buildings and portals mode
                lastModeSwitchTime = currentTime;  // Reset mode switch time
            }
        }
    }

    private void startEnemySpawnMode() {
        currentSpawnMode = SpawnMode.ENEMIES;
        maxEnemies = random.nextInt(8) + 3; // Spawn between 3 and 10 enemies each round
        enemiesSpawned = 0;  // Reset number of enemies spawned
        modeStartTime = System.currentTimeMillis(); // Start the timer when spawning enemies
    }

    private void spawnBuilding() {
        // Spawn a building every once in a while (randomly)
        int randomNumber = random.nextInt((int) (35 / speed)) + 1;
        if (randomNumber == spawningNum) {
            // Randomize the position of the building
            float randomX = random.nextFloat() * 53; // Range: 0 to 53
            Building newBuilding = buildingPool.getObject();
            if (newBuilding != null) {
                newBuilding.setPosition(randomX, -newZ + 30);
                newBuilding.setArmwing(armwing);
                addDyLmn(newBuilding);
            }
        }
    }

    private void spawnPortal() {
        // Spawn a portal every once in a while (randomly)
        int randomNumber = random.nextInt((int) (250 / speed)) + 1;
        if (randomNumber == spawningNum) {
            // Randomize the position of the portal
            float randomX = (random.nextFloat() * 8) + 22; // Range: 22 to 30
            float randomY = (random.nextFloat() * 2) + 0.2f; // Range: 0.2 to 2.2
            Portal newPortal = portalPool.getObject();
            if (newPortal != null) {
                newPortal.setPosition(randomX, randomY, -newZ + 30);
                newPortal.setArmwing(armwing);
                addDyLmn(newPortal);
            }
        }
    }

    private void despawnObjects() {
        List<SceneDrawable> toRemove = new ArrayList<>();
        for (SceneDrawable lmn : dyObjs) {
            float despawnThreshold = 650f;
            if (lmn.getScenePos() > despawnThreshold) {
                toRemove.add(lmn); // Add objects to remove to the list
                // Return objects to their pools
                if (lmn instanceof Building) {
                    buildingPool.returnObject((Building) lmn);
                } else if (lmn instanceof Portal) {
                    portalPool.returnObject((Portal) lmn);
                }
                lmn.updateScenePos(0f);
            }
        }
        dyObjs.removeAll(toRemove); // Remove objects past the threshold
    }

    private void spawnEnemy() {
        if(enemiesSpawned >= maxEnemies) {
            return;
        }

        // Spawn an enemy every once in a while (randomly)
        int randomNumber = random.nextInt((int) (35 / speed)) + 1;
        if (randomNumber == spawningNum) {
            // Randomize the position of the enemy
            float randomX = (random.nextFloat() * 4.7f) + 11f;
            float randomY = (random.nextFloat() * 1.2f) - 0.2f;
            float randomZ = (random.nextFloat() * 100) + 100;
            Enemy newEnemy = enemyPool.getObject();
            if (newEnemy != null) {
                newEnemy.initialize(randomX, randomY, -this.armwingZ + -projectileDespawnThreshold -randomZ, -randomZ);
                newEnemy.setHalfHeight(armwing.getHalfHeight());
                newEnemy.setSpeed(speed);
                addStLmn(newEnemy);
                enemiesSpawned++;
            }
        }
    }

    private void despawnEnemies() {
        for (Enemy enemy : stObjs) {
            enemy.defeat(); // Mark the enemy as defeated
            enemyPool.returnObject(enemy); // Return the enemy to the object pool
            enemy.updateScenePos(0f); // Reset the position for reuse
        }
        stObjs.clear(); // Clear the list of enemies
    }


    public void shootProjectile(float armwingX, float armwingY, float armwingZ) {
        Projectile projectile = projectilePool.getObject();
        projectile.setPosition(armwingX, armwingY, -this.armwingZ + -projectileDespawnThreshold);
        projectiles.add(projectile);
    }

    public void deleteProjectiles() {
        List<Projectile> toRemove = new ArrayList<>();
        for (Projectile lmn : projectiles) {
            if (lmn.getScenePos() < projectileDespawnThreshold) {
                toRemove.add(lmn); // Add objects to remove to the list
                // Return objects to their pools
                projectilePool.returnObject(lmn);
                lmn.updateScenePos(0f);
                lmn.powerOffLight();
            }
        }
        projectiles.removeAll(toRemove); // Remove objects past the threshold
    }

    private void checkCollisions() {
        List<Projectile> toRemoveProjectiles = new ArrayList<>();
        List<Enemy> toRemoveEnemies = new ArrayList<>();

        for (Projectile projectile : projectiles) {
            // Get the position of the projectile
            float projX = projectile.getX();
            float projY = projectile.getY();
            float projZ = projectile.getScenePos();

            for (Enemy enemy : stObjs) {
                // Get the position of the enemy
                float enemyX = enemy.getX();
                float enemyY = enemy.getY();
                float enemyZ = enemy.getScenePos();


                // Set a threshold distance for collision
                float collisionThreshold = 10.0f;

                // If all three axes collide
                if (Math.abs(projX - enemyX) < collisionThreshold &&
                        Math.abs(projY - enemyY) < collisionThreshold &&
                        Math.abs(projZ - enemyZ) < collisionThreshold) {
                    // Handle collision: Remove the projectile and the enemy
                    toRemoveProjectiles.add(projectile);
                    toRemoveEnemies.add(enemy);
                    // Exit the loop to avoid multiple collisions with the same projectile
                    break;
                }
            }
        }

        // Remove the collided projectiles and enemies
        projectiles.removeAll(toRemoveProjectiles);
        stObjs.removeAll(toRemoveEnemies);

        // Return objects to their respective pools
        for (Projectile p : toRemoveProjectiles) {
            projectilePool.returnObject(p);
        }
        for (Enemy e : toRemoveEnemies) {
            enemyPool.returnObject(e);
        }
    }


    public void setSpeed(float speed) {
        this.speed = speed;
        for (Enemy enemy : stObjs) {
            enemy.setSpeed(speed);
        }
    }

    public float getSpeed() {
        return speed;
    }
}