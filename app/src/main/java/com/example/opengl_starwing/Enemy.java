package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class Enemy {
    // Variables for the armwing and its movement
    private final Object3D enemy;
    private final Object3D enemyShadow;
    private float x = 0f;
    private float y = 0f;
    private float z = 0f;
    private float halfHeight;
    private float health = 1;

    public Enemy(GL10 gl, Context context, float x, float y, float z) {
        // Load enemy's model
        enemy = new Object3D(context, R.raw.enemy1);
        enemyShadow = new Object3D(context, R.raw.enemy1);
        enemy.loadTexture(gl, context, R.drawable.paleta1);

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getEnemyX() {
        return x;
    }

    public float getEnemyY() {
        return y;
    }

    public void setHalfHeight(float halfHeight) {
        this.halfHeight = halfHeight;
    }

    public void initialize(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        health = 100;
    }

    public void draw(GL10 gl) {
        // Draw the Enemy
        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glRotatef(180, 0, 1, 0); // Enemy facing armwing
        gl.glTranslatef(x, y, z);
        enemy.draw(gl);
        gl.glPopMatrix(); // Restore the transformation matrix

        float normalizedY = (y - (-halfHeight)) / (halfHeight - (-halfHeight));
        normalizedY = Math.min(1.0f, Math.max(0.0f, normalizedY)); // Ensure clamping between 0 and 1

        // Use the normalizedY to calculate shadowScale
        float shadowScale = Math.min(0.9f, Math.max(0.3f, 1.0f - normalizedY * 0.9f));

        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glTranslatef(x, -1.25f, z + 0.1f);
        gl.glScalef(shadowScale, shadowScale, shadowScale); // Scale the enemy's shadow
        gl.glDisable(GL10.GL_LIGHTING);
        enemyShadow.setAlpha(0.5f);
        enemyShadow.setRGB(0.1f, 0.1f, 0.1f);
        enemyShadow.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glPopMatrix(); // Restore the transformation matrix
    }

    public void shootProjectile(Scene scene) {
        scene.shootProjectile(x, y, z);
    }
}
