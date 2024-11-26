package com.example.opengl_starwing;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Scene {
    private float speed = 1f;
    private List<Drawable> dyObjs;
    private GroundPoints gp;
    private final float x, y, initialZ;
    private float z, prevZ;
    private final Random random = new Random();
    private final float resetThreshold; // Set a threshold for when to reset the Ground Points
    private final Context context;

    public Scene(float x, float y, float z, Context context) {
        dyObjs = new ArrayList<Drawable>(64);
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

    public void addDyLmn(Drawable lmn) {
        dyObjs.add(lmn);
    }

    public void draw(GL10 gl) {
        spawnBuilding(gl);
        z+=speed;

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        gp.checkAndResetPosition(z, resetThreshold, speed, initialZ);
        gp.draw(gl);

        // Draw other dynamic objects, skipping those past the threshold
        List<Drawable> toRemove = new ArrayList<>();
        for (Drawable lmn : dyObjs) {
            lmn.draw(gl);  // Calls the draw method of each dynamic element
        }
        dyObjs.removeAll(toRemove); // Remove objects past the threshold
        gl.glPopMatrix();
    }

    private void spawnBuilding(GL10 gl) {
        // Spawn some building every once in a while (randomly)
        int randomNumber = random.nextInt((int) (100/speed)) + 1;
        int spawningNum = 1;	// Number to spawn building
        if(randomNumber == spawningNum) {
            // Randomize the position of the cube
            float randomX = random.nextFloat() * 60; // Range: 0 to 60
            float newZ = Math.abs(z)/(initialZ/35) - 5;
            Building newBuilding = new Building(gl, context, randomX, 0.0f, newZ);
            addDyLmn(newBuilding);
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
