package com.example.opengl_starwing;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Scene {
    private float speed = 1f;
    private float reset;
    private List<Drawable> dyObjs;
    private List<Drawable> stObjs;
    private float x;
    private float y;
    private float z;
    private float initialZ;
    private float prevZ;

    int groundPointsYSpacing = 12;
    int groundPointsPerCol = 84;
    int groundPointsXSpacing = 42;
    int groundPointsPerRow = 20;
    private float resetThreshold; // Set a threshold for when to reset the Ground Points

    public Scene(float x, float y, float z) {
        dyObjs = new ArrayList<Drawable>(32);
        stObjs = new ArrayList<Drawable>(32);
        this.x = -x;
        this.y = y;
        this.z = -z;
        initialZ = -z;
        prevZ = -z;
        resetThreshold = z-1;

        GroundPoints gp1 = new GroundPoints(groundPointsPerCol, groundPointsPerRow, groundPointsXSpacing, groundPointsYSpacing);
        gp1.setPosition(0, y, -z/3);
        addDyLmn(gp1);
    }

    public void addDyLmn(Drawable lmn) {
        dyObjs.add(lmn);
    }

    public void draw(GL10 gl) {
        z+=speed;
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        for (Drawable lmn : dyObjs) {
            if (lmn instanceof GroundPoints) {
                GroundPoints gp = (GroundPoints) lmn;
                // Check if the z position crosses the threshold
                reset = (z < 0) ? (resetThreshold+1+(z%initialZ)) : z%initialZ;
                if (reset >= resetThreshold) {
                    prevZ = (z < 0) ? prevZ+initialZ/9 : prevZ+initialZ;
                    gp.setPosition(0, y, prevZ); // Reset to its initial z position
                }
            }
            lmn.draw(gl);  // Calls the draw method of each element
        }
        gl.glPopMatrix();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
