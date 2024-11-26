package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Building implements SceneDrawable{
    private Object3D building = null;
    private float x, y, z;
    private final Random random = new Random();
    private float sceneZ;

    public Building(GL10 gl, Context context, float x, float y, float z) {
        switch (random.nextInt(4)) {
            case 0:
                building = new Object3D(context, R.raw.building1);
                this.y = 2.5f;
                break;
            case 1:
                building = new Object3D(context, R.raw.building2);
                this.y = 2.1f;
                break;
            case 2:
                building = new Object3D(context, R.raw.building3);
                this.y = -0.1f;
                break;
            case 3:
                building = new Object3D(context, R.raw.building4);
                this.y = -0.2f;
                break;
        }
        building.loadTexture(gl, context, R.drawable.black);
        this.x = x;
        this.z = z;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void updateScenePos(float z) {
        sceneZ = z;
    }

    public float getScenePos() {
        return sceneZ;
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(15f, 15f, 15f);
        gl.glTranslatef(x, y, z);
        building.draw(gl);
        gl.glPopMatrix();
    }
}
