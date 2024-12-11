package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class EnemyProjectile implements SceneDrawable {
    private final Object3D projectile;
    private Light light;
    private float x, y, z, sceneZ;
    private float rotation = 0f;
    private float VELOCITY_Z = 5;
    private boolean isBoss = false;
    private final GL10 gl;

    public EnemyProjectile(GL10 gl, Context context, float x, float y, float z) {
        projectile = new Object3D(context, R.raw.projectile);
        projectile.loadTexture(gl, context, R.drawable.paleta1);
        this.gl = gl;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Place the projectile in the scene with the scene coordinate system
    public void setPosition(float projX, float projY, float projZ) {
        x = projX;
        y = projY;
        z = projZ;

        // Initialize and configure the light
        light = new Light(gl, GL10.GL_LIGHT3);
        light.setDiffuseColor(new float[]{0.6f, 0.2f, 0.2f, 1.0f}); // Red-ish light
        light.setSpecularColor(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
        light.setAttenuation(1.0f, 0.1f, 0.02f);    // Make the light decay faster
    }

    @Override
    public float getScenePos() {
        return sceneZ;
    }

    // Map the projectile's x to a value between 0 and 100 to make collision detection in Scene easier
    public float getX() {
        return (x - (326f)) / (475 - (326)) * 100;
    }

    // Map the projectile's y to a value between 0 and 100 to make collision detection in Scene easier
    public float getY() {
        return (y - (-11.2f)) / (31.7f - (-11.2f)) * 100;
    }

    @Override
    public void draw(GL10 gl) {
        float ROTATION_SPEED = 3f;
        rotation += ROTATION_SPEED;
        z += VELOCITY_Z;    // Make the projectile move in the scene (faster than the scene)

        if (isBoss) {   // If it's a boss' projectile
            gl.glPushMatrix();
            gl.glTranslatef(x, y, z);
            gl.glScalef(40f, 40f, 40f); // Make that BIG BOY
            gl.glRotatef(180, 0, 1, 0); // Projectile faces Armwing
            gl.glRotatef(rotation % 360, 0, 0, 1);  // Projectile spins
            projectile.draw(gl);    // Draw a center projectile
            gl.glTranslatef(0.1f, 0, 0);
            gl.glRotatef(rotation*2 % 360, 0, 0, 1);    // Exterior projectile spins double as fast
            projectile.draw(gl);    // Draw one of two exterior projectiles
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glTranslatef(x, y, z);
            gl.glScalef(40f, 40f, 40f); // Make that BIG BOY
            gl.glRotatef(180, 0, 1, 0); // Projectile faces Armwing
            gl.glRotatef(rotation % 360, 0, 0, 1);  // Projectile spins
            gl.glTranslatef(-0.1f, 0, 0);
            gl.glRotatef(rotation*2 % 360, 0, 0, 1);    // Exterior projectile spins double as fast
            projectile.draw(gl);    // Draw one of two exterior projectiles
            gl.glPopMatrix();
        } else {    // If it's a normal enemy projectile
            gl.glPushMatrix();
            gl.glTranslatef(x, y, z);
            gl.glScalef(40f, 40f, 40f); // Make that BIG BOY
            gl.glRotatef(180, 0, 1, 0); // Projectile faces Armwing
            gl.glRotatef(rotation % 360, 0, 0, 1);  // Projectile spins
            projectile.draw(gl);    // Draw one single projectile
            gl.glPopMatrix();
        }

        // Update the light position to follow the projectile
        light.setPosition(new float[]{x, y, z, 1.0f});
        light.enable();
    }

    @Override
    public void updateScenePos(float z) {
        sceneZ = z + VELOCITY_Z;
    }

    public void powerOffLight() {
        light.disable();
    }

    public void markAsBoss() {
        isBoss = true;
        VELOCITY_Z = 2;
    }
}
