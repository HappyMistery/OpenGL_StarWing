package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Portal implements SceneDrawable{
    private Object3D portal = null;
    private float x, y, z;
    private final Random random = new Random();
    private float sceneZ;
    private float alpha = 0.0f; // Initial alpha value
    private final float TRANSPARENCY_THRESHOLD = 20f;
    private final float FADE_IN_DISTANCE = 50f; // Range over which the building fades in

    public Portal(GL10 gl, Context context, float x, float y, float z) {
        portal = new Object3D(context, R.raw.portal);
        portal.loadTexture(gl, context, R.drawable.portalpalette);
        this.x = x;
        this.y = y;
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

    public void updateAlpha(float sceneZ) {
        if (sceneZ > TRANSPARENCY_THRESHOLD) {
            float fadeStart = TRANSPARENCY_THRESHOLD;
            float fadeEnd = TRANSPARENCY_THRESHOLD + FADE_IN_DISTANCE;
            alpha = Math.min(1f, Math.max(0f, (sceneZ - fadeStart) / (fadeEnd - fadeStart)));
        } else {
            alpha = 0f; // Fully transparent
        }
        portal.setAlpha(alpha);
    }

    public float getScenePos() {
        return sceneZ;
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(15f, 15f, 15f);
        gl.glTranslatef(x, y, z);
        gl.glRotatef(90, 0f, 1f, 0f);
        if(alpha < 1f) {
            gl.glColor4f(1f, 1f, 1f, alpha);
            updateAlpha(sceneZ);
        }
        portal.draw(gl);
        gl.glPopMatrix();
    }
}
