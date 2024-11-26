package com.example.opengl_starwing;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Scene {
    private float speed = 1f;
    private List<SceneDrawable> dyObjs;
    private GroundPoints gp;
    private final float x, y, initialZ;
    private float z, prevZ;
    private final Random random = new Random();
    private final float resetThreshold; // Set a threshold for when to reset the Ground Points
    private final float despawnThreshold = 650f;
    private final Context context;

    public Scene(float x, float y, float z, Context context) {
        dyObjs = new ArrayList<SceneDrawable>(64);
        this.x = -x;
        this.y = y;
        this.z = -z;
        initialZ = -z;
        prevZ = -z;
        resetThreshold = z-1;
        this.context = context;

        gp = new GroundPoints(84, 20, 42, 12, prevZ);
        gp.setPosition(0, y, -z/3);
    }

    public void addDyLmn(SceneDrawable lmn) {
        dyObjs.add(lmn);
    }

    public void draw(GL10 gl) {
        spawnBuilding(gl);
        z+=speed;

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        gp.checkAndResetPosition(z, resetThreshold, speed, initialZ);
        gp.draw(gl);

        despawnObjects(gl);

        for (SceneDrawable lmn : dyObjs) {
            lmn.updateScenePos(lmn.getScenePos()+speed);
            lmn.draw(gl);
        }
        gl.glPopMatrix();
    }

    private void spawnBuilding(GL10 gl) {
        // Spawn some building every once in a while (randomly)
        int randomNumber = random.nextInt((int) (30/speed)) + 1;
        int spawningNum = 1;	// Number to spawn building
        if(randomNumber == spawningNum) {
            // Randomize the position of the cube
            float randomX = random.nextFloat() * 53; // Range: 0 to 53
            float newZ = z/(initialZ/35) - 5;
            Building newBuilding = new Building(gl, context, randomX, 0.0f, newZ);
            addDyLmn(newBuilding);
        }
    }

    private void despawnObjects(GL10 gl) {
        // Draw other dynamic objects, skipping those past the threshold
        List<SceneDrawable> toRemove = new ArrayList<>();
        for (SceneDrawable lmn : dyObjs) {
            if (lmn.getScenePos() > despawnThreshold) {
                System.out.println("Deleted object at z = " + lmn.getScenePos());
                toRemove.add(lmn); // Add objects to remove to the list
            }
        }
        dyObjs.removeAll(toRemove); // Remove objects past the threshold
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
