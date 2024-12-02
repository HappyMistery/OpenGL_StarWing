package com.example.opengl_starwing;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Scene {
    private float speed = 1f;
    private final float projectileSpeed = -2f;
    private final List<SceneDrawable> dyObjs;
    private final List<Projectile> projectiles;
    private final GroundPoints gp;
    private final float x, y, initialZ;
    private float z, newZ, armwingZ;
    private final Random random = new Random();
    private final int spawningNum = 1;    // Number to spawn building
    private final float resetThreshold; // Set a threshold for when to reset the Ground Points
    private final float despawnThreshold = 650f;
    private final float projectileDespawnThreshold = -790f;

    private final ObjectPool<Building> buildingPool;
    private final ObjectPool<Portal> portalPool;
    private final ObjectPool<Projectile> projectilePool;

    private Armwing armwing;

    public Scene(GL10 gl, Context context, float x, float y, float z, Armwing armwing) {
        dyObjs = new ArrayList<>(64);
        projectiles = new ArrayList<>(256);
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

        this.armwing = armwing;
    }

    public void addDyLmn(SceneDrawable lmn) {
        dyObjs.add(lmn);
    }

    public void draw(GL10 gl) {
        spawnPortals();
        spawnBuilding();

        z += speed;
        newZ += speed / 12;
        armwingZ += speed;

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        gp.checkAndResetPosition(z, resetThreshold, speed, initialZ);
        gp.draw(gl);

        despawnObjects();
        deleteProjectile();

        // Draw opaque objects first
        for (SceneDrawable lmn : dyObjs) {
            lmn.updateScenePos(lmn.getScenePos() + speed);
            lmn.draw(gl);
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

    private void spawnPortals() {
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

    public void shootProjectile(float armwingX, float armwingY, float armwingZ) {
        Projectile projectile = projectilePool.getObject();
        projectile.setPosition(armwingX, armwingY, -this.armwingZ + -projectileDespawnThreshold);
        projectiles.add(projectile);
    }

    public void deleteProjectile() {
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

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}