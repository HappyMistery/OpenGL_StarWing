package com.example.opengl_starwing;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Scene {
    private static final float SPEED = 1f;
    private static final float RESET_THRESHOLD = 83f; // Set a threshold for when to reset
    private List<Drawable> dyObjs;
    private List<Drawable> stObjs;
    private float x;
    private float y;
    private float z;
    private float initialZ;
    private float prevZ;

    int groundPointsYSpacing = 8;
    int groundPointsPerCol = 21;
    int groundPointsXSpacing = 32;
    int groundPointsPerRow = 11;

    public Scene(float x, float y, float z) {
        dyObjs = new ArrayList<Drawable>(32);
        stObjs = new ArrayList<Drawable>(32);
        this.x = -x;
        this.y = y;
        this.z = -z;
        initialZ = -z;
        prevZ = -z;

        GroundPoints gp1 = new GroundPoints(groundPointsPerCol, groundPointsPerRow, groundPointsXSpacing, groundPointsYSpacing);
        gp1.setPosition(x, y, -z);
        dyObjs.add(gp1);
    }

    public void draw(GL10 gl) {
        z+=SPEED;
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        for (Drawable lmn : dyObjs) {
            if (lmn instanceof GroundPoints) {
                GroundPoints gp = (GroundPoints) lmn;
                // Check if the z position crosses the threshold
                if (z%initialZ >= RESET_THRESHOLD) {
                    prevZ += initialZ;
                    gp.setPosition(x, y, prevZ); // Reset to its initial z position (relative to the Scene)
                }
            }
            lmn.draw(gl);  // Calls the draw method of each element
        }
        gl.glPopMatrix();
    }
}
