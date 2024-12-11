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
    private final CopyOnWriteArrayList<ArmwingProjectile> armwingProjectiles; // Separate list for Armwing projectiles
    private final CopyOnWriteArrayList<EnemyProjectile> enemyProjectiles;   // Separate list for Enemy projectiles
    private final GroundPoints gp;
    private Stairs stairs;
    private Boss boss = null;
    private final float x, y, initialZ;
    private float z, newZ, armwingZ;
    private final Random random = new Random();
    private final int spawningNum = 1;    // Number to spawn building
    private final float resetThreshold; // Set a threshold for when to reset the Ground Points
    float despawnThreshold = 650f;
    private final float projectileDespawnThreshold = -790f;
    private int maxEnemies, enemiesSpawned, enemiesDefeated;
    private boolean waveCompleted = false;
    private float wavesCompleted = 0;
    private long modeStartTime = 0;  // Time when the spawn mode started
    private boolean gameEnded = false; // Flag to indicate end of game

    private final ObjectPool<Building> buildingPool;
    private final ObjectPool<Portal> portalPool;
    private final ObjectPool<Enemy> enemyPool;
    private final ObjectPool<ArmwingProjectile> armwingProjectilePool;
    private final ObjectPool<EnemyProjectile> enemyProjectilePool;

    private final Armwing armwing;

    private enum SpawnMode {
        BUILDINGS_AND_PORTALS,
        ENEMIES
    }

    private SpawnMode currentSpawnMode = SpawnMode.BUILDINGS_AND_PORTALS;
    private long lastModeSwitchTime = System.currentTimeMillis(); // Track time for mode switching

    private GL10 gl;
    private Context context;

    public Scene(GL10 gl, Context context, float x, float y, float z, Armwing armwing) {
        this.gl = gl;
        this.context = context;
        dyObjs = new ArrayList<>(64);
        stObjs = new ArrayList<>(16);
        armwingProjectiles = new CopyOnWriteArrayList<ArmwingProjectile>();
        enemyProjectiles = new CopyOnWriteArrayList<EnemyProjectile>();
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
        armwingProjectilePool = new ObjectPool<>(gl, context, ArmwingProjectile::new, 128, 128);
        enemyProjectilePool = new ObjectPool<>(gl, context, EnemyProjectile::new, 128, 128);
        enemyPool = new ObjectPool<Enemy>(gl, context, Enemy::new, 16, 16); // Max 16 enemies in pool

        this.armwing = armwing;

        boss = new Boss(gl, context);
        boss.setScene(this);
        boss.setHUD(armwing.getHUD());
        boss.initialize(0, 0.75f, 150);
    }

    public void addDyLmn(SceneDrawable lmn) {
        dyObjs.add(lmn);
    }

    public void addStLmn(Enemy lmn) {
        stObjs.add(lmn);
    }

    public void draw(GL10 gl) {
        if (gameEnded) {
            stairs.draw(gl);    // Only draw the stairs
            gl.glPushMatrix();
            gl.glTranslatef(x, y, z);
            gp.draw(gl);
            gl.glPopMatrix();
            return; // Skip the rest of the drawing logic
        }

        if (boss.isActivated()) {
            boss.draw(gl);
        }

        switchSpawnMode();

        checkArmwingProjectilesCollisions();
        checkEnemyProjectilesCollisions();

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

        for (ArmwingProjectile projectile : armwingProjectiles) {
            projectile.updateScenePos(projectile.getScenePos());
            projectile.draw(gl);
        }

        for (EnemyProjectile projectile : enemyProjectiles) {
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
        if (wavesCompleted >= 3) {
            if (!boss.isActivated()) {
                boss.activate();
                armwing.getHUD().bossPhase();
            } else if (boss.isDefeated()) {
                gameEnded = true;
                stairs = new Stairs(gl, context);
                armwing.getCam().setEndGameCamView();
                armwing.setTargetArmwingZ(-5f);
                speed = 0; // Stop scene movement
            }
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Calculate elapsed time in seconds
        long elapsedTime = (currentTime - lastModeSwitchTime) / 1000;

        // Print the elapsed time for the current spawn mode

        // Switch spawn modes based on time
        if (currentTime - lastModeSwitchTime > 20000) { // Mode duration (20 seconds)
            if (currentSpawnMode == SpawnMode.BUILDINGS_AND_PORTALS) {
                startEnemySpawnMode();
                lastModeSwitchTime = currentTime;
            }
        }

        // Check if all enemies are defeated
        waveCompleted = enemiesDefeated == maxEnemies;
        if (currentSpawnMode == SpawnMode.ENEMIES && waveCompleted) {
            wavesCompleted++;
            currentSpawnMode = SpawnMode.BUILDINGS_AND_PORTALS;
            lastModeSwitchTime = currentTime;
        }

        // Check if the time limit for killing enemies has passed
        int spawnTimeLimit = 30000; // 30 seconds
        boolean enemyTimeLimitReached = currentTime - modeStartTime >= spawnTimeLimit;

        // Transition all enemies out 2 seconds before the time limit
        if (currentSpawnMode == SpawnMode.ENEMIES && (spawnTimeLimit - (currentTime - modeStartTime)) <= 2000) {
            for (Enemy enemy : stObjs) {
                if (enemy.getTransitionMode() != 2) enemy.startTransition(2);    // Trigger transition to exit
            }
        }

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
                wavesCompleted++;
                currentSpawnMode = SpawnMode.BUILDINGS_AND_PORTALS; // Switch back to buildings and portals mode
                lastModeSwitchTime = currentTime;  // Reset mode switch time
            }
        }
    }

    private void startEnemySpawnMode() {
        currentSpawnMode = SpawnMode.ENEMIES;
        maxEnemies = random.nextInt(8) + 3; // Spawn between 3 and 10 enemies each round
        enemiesSpawned = 0;  // Reset number of enemies spawned
        enemiesDefeated = 0; // Reset number of enemies defeated
        waveCompleted = false;
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
                newBuilding.updateScenePos(0f);
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
                newPortal.updateScenePos(0f);
                newPortal.setPosition(randomX, randomY, -newZ + 30);
                newPortal.setArmwing(armwing);
                addDyLmn(newPortal);
            }
        }
    }

    private void despawnObjects() {
        List<SceneDrawable> toRemove = new ArrayList<>();
        for (SceneDrawable lmn : dyObjs) {
            if (lmn.getScenePos() > despawnThreshold) {
                toRemove.add(lmn); // Add objects to remove to the list
                // Return objects to their pools
                if (lmn instanceof Building) {
                    buildingPool.returnObject((Building) lmn);
                } else if (lmn instanceof Portal) {
                    portalPool.returnObject((Portal) lmn);
                }
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
            float enemyZ = 250 * Math.min(speed, 1.15f);
            Enemy newEnemy = enemyPool.getObject();
            if (newEnemy != null) {
                newEnemy.updateScenePos(0f); // Reset the position for reuse
                newEnemy.initialize(randomX, randomY, -this.armwingZ + -projectileDespawnThreshold -enemyZ, -enemyZ);
                newEnemy.setHalfHeight(armwing.getHalfHeight());
                newEnemy.setScene(this);
                addStLmn(newEnemy);
                enemiesSpawned++;
            }
        }
    }

    private void despawnEnemies() {
        for (Enemy enemy : stObjs) {
            enemy.defeat(); // Mark the enemy as defeated
            enemyPool.returnObject(enemy); // Return the enemy to the object pool
        }
        stObjs.clear(); // Clear the list of enemies
        waveCompleted = true;
    }


    public void shootArmwingProjectile(float armwingX, float armwingY) {
        ArmwingProjectile projectile = armwingProjectilePool.getObject();
        if (projectile != null) {
            projectile.updateScenePos(0);
            projectile.setPosition(armwingX, armwingY, -this.armwingZ + -projectileDespawnThreshold);
            armwingProjectiles.add(projectile);
        }
    }

    public void shootEnemyProjectile(float enemyX, float enemyY, float enemyZ, boolean isBoss) {
        EnemyProjectile projectile = enemyProjectilePool.getObject();
        if (isBoss) enemyZ = -this.armwingZ + -projectileDespawnThreshold - 150;
        if (projectile != null) {
            projectile.updateScenePos(0);
            projectile.setPosition(enemyX, enemyY, enemyZ);
            if (isBoss) projectile.markAsBoss();
            enemyProjectiles.add(projectile);
        }
    }

    public void deleteProjectiles() {
        List<ArmwingProjectile> toRemove1 = new ArrayList<>();
        for (ArmwingProjectile lmn : armwingProjectiles) {
            if (lmn.getScenePos() < projectileDespawnThreshold) {
                toRemove1.add(lmn); // Add objects to remove to the list
                // Return objects to their pools
                armwingProjectilePool.returnObject(lmn);
                lmn.powerOffLight();
            }
        }
        armwingProjectiles.removeAll(toRemove1); // Remove objects past the threshold
        List<EnemyProjectile> toRemove2 = new ArrayList<>();
        for (EnemyProjectile lmn : enemyProjectiles) {
            if (lmn.getScenePos() > despawnThreshold/4) {
                toRemove2.add(lmn); // Add objects to remove to the list
                // Return objects to their pools
                enemyProjectilePool.returnObject(lmn);
                lmn.powerOffLight();
            }
        }
        enemyProjectiles.removeAll(toRemove2); // Remove objects past the threshold
    }

    private void checkArmwingProjectilesCollisions() {
        List<ArmwingProjectile> toRemoveProjectiles = new ArrayList<>();
        List<Enemy> toRemoveEnemies = new ArrayList<>();

        for (ArmwingProjectile projectile : armwingProjectiles) {
            // Get the position of the projectile
            float projX = projectile.getX();
            float projY = projectile.getY();
            float projZ = projectile.getScenePos();

            if (boss.isActivated()) {
                float bossX = boss.getX();
                float bossZ = -200f;

                float collisionThreshold = 30f;

                System.out.println("BOSS: " + bossX + " " + bossZ);
                System.out.println("PROJECTILE: " + projX + " " + projZ);

                // If all three axes collide
                if (Math.abs(projX - bossX) < collisionThreshold &&
                        Math.abs(projZ - bossZ) < collisionThreshold) {
                    toRemoveProjectiles.add(projectile);
                    // Return objects to their pools
                    armwingProjectilePool.returnObject(projectile);
                    boss.setShieldPercentage(boss.getShieldPercentage() - 0.05f);
                    break;
                }
                return;
            }

            float collisionThreshold = 10f;

            for (Enemy enemy : stObjs) {
                // Get the position of the enemy
                float enemyX = enemy.getX();
                float enemyY = enemy.getY();
                float enemyZ = enemy.getScenePos();

                // If all three axes collide
                if (Math.abs(projX - enemyX) < collisionThreshold &&
                        Math.abs(projY - enemyY) < collisionThreshold &&
                        Math.abs(projZ - enemyZ) < collisionThreshold) {
                    // Handle collision: Remove the projectile and the enemy
                    toRemoveProjectiles.add(projectile);
                    toRemoveEnemies.add(enemy);
                    enemy.defeat(); // Mark the enemy as defeated
                    enemiesDefeated++;
                    // Exit the loop to avoid multiple collisions with the same projectile
                    break;
                }
            }
        }

        // Remove the collided projectiles and enemies
        armwingProjectiles.removeAll(toRemoveProjectiles);
        stObjs.removeAll(toRemoveEnemies);

        // Return objects to their respective pools
        for (ArmwingProjectile p : toRemoveProjectiles) {
            armwingProjectilePool.returnObject(p);
            p.powerOffLight();
        }
        for (Enemy e : toRemoveEnemies) {
            enemyPool.returnObject(e);
        }
    }

    private void checkEnemyProjectilesCollisions() {
        List<EnemyProjectile> toRemoveProjectiles = new ArrayList<>();

        // Map Armwing coordinates to the range [0, 100]
        float armwingXMapped = ((armwing.getArmwingX() + 4) / 8) * 100;
        float armwingYMapped = ((armwing.getArmwingY() + 1) / 2.3f) * 100;
        float armwingZMapped = ((armwing.getArmwingZ() - 16.66f)) * 100;

        for (EnemyProjectile projectile : enemyProjectiles) {
            // Get the position of the projectile
            float projX = projectile.getX();
            float projY = projectile.getY();
            float projZ = projectile.getScenePos();

            // Set a threshold distance for collision
            float collisionThreshold = 10.0f;

            // If all three axes collide
            if (Math.abs(projX - armwingXMapped) < collisionThreshold &&
                    Math.abs(projY - armwingYMapped) < collisionThreshold &&
                    Math.abs(projZ - armwingZMapped) < collisionThreshold) {
                // Handle collision: Remove the projectile and lower armwing's shield
                toRemoveProjectiles.add(projectile);
                armwing.setShieldPercentage(armwing.getShieldPercentage() - 0.10f);
                armwing.getCam().startShake(0.1f, 0.1f);
                // Exit the loop to avoid multiple collisions with the same projectile
                break;
            }
        }

        // Remove collided projectiles
        enemyProjectiles.removeAll(toRemoveProjectiles);

        // Return objects to their respective pool
        for (EnemyProjectile p : toRemoveProjectiles) {
            enemyProjectilePool.returnObject(p);
            p.powerOffLight();
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }
}