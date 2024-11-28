package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Building implements SceneDrawable{
    private Object3D building = null;
    private float x, y, z;
    private final Random random = new Random();
    private float sceneZ;
    private float rotationY; // Random Y-axis rotation
    private float alpha = 0.0f; // Initial alpha value
    private final float TRANSPARENCY_THRESHOLD = 20f;
    private final float FADE_IN_DISTANCE = 50f; // Range over which the building fades in

    public Building(GL10 gl, Context context, float x, float y, float z) {
        this.rotationY = 0;
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
                this.rotationY = random.nextBoolean() ? 0f : 90f; // Randomly select 0 or 90 degrees
                break;
        }
        building.loadTexture(gl, context, R.drawable.black);
        this.x = x;
        this.z = z;
    }

    public void setPosition(float x, float z) {
        this.x = x;
        this.z = z;
    }

    public void updateScenePos(float z) {
        sceneZ = z;
    }

    public void updateAlpha(float sceneZ) {
        if (sceneZ > TRANSPARENCY_THRESHOLD) {
            float fadeStart = TRANSPARENCY_THRESHOLD;
            float fadeEnd = TRANSPARENCY_THRESHOLD + FADE_IN_DISTANCE;
            alpha = Math.min(1f, Math.max(0f, (sceneZ - fadeStart) / (fadeEnd - fadeStart)));
        } else {
            alpha = 0f; // Fully transparent
        }
        building.setAlpha(alpha);
    }

    public float getScenePos() {
        return sceneZ;
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(12f, 15f, 12f);
        gl.glTranslatef(x, y, z);
        gl.glRotatef(rotationY, 0f, 1f, 0f); // Apply random rotation on Y-axis
        if(alpha < 1f) {
            gl.glColor4f(1f, 1f, 1f, alpha);
            updateAlpha(sceneZ);
        }
        building.draw(gl);
        gl.glPopMatrix();
    }
}
