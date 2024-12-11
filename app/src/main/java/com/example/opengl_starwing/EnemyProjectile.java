package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class EnemyProjectile implements SceneDrawable {
    private Object3D projectile = null;
    private Light light;
    private float x, y, z, sceneZ;
    private float rotation = 0f;
    private final float ROTATION_SPEED = 3f;
    private float VELOCITY_Z = 5;
    private boolean isBoss = false;

    private final Context context;
    private final GL10 gl;

    public EnemyProjectile(GL10 gl, Context context, float x, float y, float z) {
        projectile = new Object3D(context, R.raw.projectile);
        projectile.loadTexture(gl, context, R.drawable.paleta1);
        this.gl = gl;
        this.context = context;
        this.x = x;
        this.y = y;
        this.z = z;

        light = new Light(gl, GL10.GL_LIGHT3);
        light.setDiffuseColor(new float[]{0.6f, 0.2f, 0.2f, 1.0f});
        light.setSpecularColor(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
        light.setAttenuation(1.0f, 0.1f, 0.02f);
    }

    public void setPosition(float projX, float projY, float projZ) {
        x = projX;
        y = projY;
        z = projZ;
    }

    @Override
    public void draw(GL10 gl) {
        rotation += ROTATION_SPEED;
        z += VELOCITY_Z;

        if (isBoss) {
            gl.glPushMatrix();
            gl.glTranslatef(x, y, z);
            gl.glScalef(40f, 40f, 40f);
            gl.glRotatef(180, 0, 1, 0);
            gl.glRotatef(rotation % 360, 0, 0, 1);
            projectile.draw(gl);
            gl.glTranslatef(0.1f, 0, 0);
            gl.glRotatef(rotation*2 % 360, 0, 0, 1);
            projectile.draw(gl);
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glTranslatef(x, y, z);
            gl.glScalef(40f, 40f, 40f);
            gl.glRotatef(180, 0, 1, 0);
            gl.glRotatef(rotation % 360, 0, 0, 1);
            gl.glTranslatef(-0.1f, 0, 0);
            gl.glRotatef(rotation*2 % 360, 0, 0, 1);
            projectile.draw(gl);
            gl.glPopMatrix();
        } else {
            gl.glPushMatrix();
            gl.glTranslatef(x, y, z);
            gl.glScalef(40f, 40f, 40f);
            gl.glRotatef(180, 0, 1, 0);
            gl.glRotatef(rotation % 360, 0, 0, 1);
            projectile.draw(gl);
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

    @Override
    public float getScenePos() {
        return sceneZ;
    }

    public float getX() {
        return (x - (326f)) / (475 - (326)) * 100;
    }

    public float getY() {
        return (y - (-11.2f)) / (31.7f - (-11.2f)) * 100;
    }

    public void powerOffLight() {
        light.disable();
    }

    public void markAsBoss() {
        isBoss = true;
        VELOCITY_Z = 2;
    }
}
