package com.example.opengl_starwing;

import javax.microedition.khronos.opengles.GL10;

public class Building implements Drawable{
    private final Cube building;
    private float x, y, z;

    public Building(float x, float y, float z) {
        building = new Cube(x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(1.5f, 3f, 1.5f);
        gl.glTranslatef(x, y, z);
        building.draw(gl);
        gl.glPopMatrix();
    }
}
