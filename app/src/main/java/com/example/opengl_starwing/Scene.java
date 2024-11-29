package com.example.opengl_starwing;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Scene {
    private float speed = 1f;
    private final List<SceneDrawable> dyObjs;
    private final GroundPoints gp;
    private final float x, y, initialZ;
    private float z, newZ;
    private final Random random = new Random();
    private final int spawningNum = 1;    // Number to spawn building
    private final float resetThreshold; // Set a threshold for when to reset the Ground Points
    private final float despawnThreshold = 650f;

    private final ObjectPool<Building> buildingPool;
    private final ObjectPool<Portal> portalPool;

    public Scene(GL10 gl, Context context, float x, float y, float z) {
        dyObjs = new ArrayList<>(64);
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
    }

    public void addDyLmn(SceneDrawable lmn) {
        dyObjs.add(lmn);
    }

    public void draw(GL10 gl) {
        spawnPortals(gl);
        spawnBuilding(gl);

        z += speed;
        newZ += speed / 12;

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        gp.checkAndResetPosition(z, resetThreshold, speed, initialZ);
        gp.draw(gl);

        despawnObjects(gl);

        for (SceneDrawable lmn : dyObjs) {
            lmn.updateScenePos(lmn.getScenePos() + speed);
            lmn.draw(gl);
        }
        gl.glPopMatrix();
    }

    private void spawnBuilding(GL10 gl) {
        // Spawn a building every once in a while (randomly)
        int randomNumber = random.nextInt((int) (35 / speed)) + 1;
        if (randomNumber == spawningNum) {
            // Randomize the position of the building
            float randomX = random.nextFloat() * 53; // Range: 0 to 53
            Building newBuilding = buildingPool.getObject();
            if (newBuilding != null) {
                newBuilding.setPosition(randomX, -newZ + 30);
                addDyLmn(newBuilding);
            }
        }
    }

    private void spawnPortals(GL10 gl) {
        // Spawn a portal every once in a while (randomly)
        int randomNumber = random.nextInt((int) (250 / speed)) + 1;
        if (randomNumber == spawningNum) {
            // Randomize the position of the portal
            float randomX = (random.nextFloat() * 8) + 22; // Range: 22 to 30
            float randomY = (random.nextFloat() * 2) + 0.2f; // Range: 0 to 2
            Portal newPortal = portalPool.getObject();
            if (newPortal != null) {
                newPortal.setPosition(randomX, randomY, -newZ + 30);
                addDyLmn(newPortal);
            }
        }
    }

    private void despawnObjects(GL10 gl) {
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

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}