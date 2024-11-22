package com.example.opengl_starwing;

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

    public Scene(float x, float y, float z) {
        dyObjs = new ArrayList<Drawable>(64);
        this.x = -x;
        this.y = y;
        this.z = -z;
        initialZ = -z;
        prevZ = -z;
        resetThreshold = z-1;

        gp = new GroundPoints(84, 20, 42, 12, prevZ);
        gp.setPosition(0, y, -z/3);
    }

    public void addDyLmn(Drawable lmn) {
        dyObjs.add(lmn);
    }

    public void draw(GL10 gl) {
        spawnBuilding();
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

    private void spawnBuilding() {
        // Spawn some building every once in a while (randomly)
        int randomNumber = random.nextInt((int) (35/speed)) + 1;
        int spawningNum = 10;	// Number to spawn building
        if(randomNumber == spawningNum) {
            //System.out.println("spawned a building");
            // Randomize the position of the cube
            float randomX = random.nextFloat() * 15; // Range: 0 to 15
            float newZ = z/(initialZ/10);
            //System.out.println("z: " + z);
            //System.out.println("newZ: " + newZ);
            Building newBuilding = new Building(randomX, 0.0f, newZ);
            addDyLmn(newBuilding);
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
