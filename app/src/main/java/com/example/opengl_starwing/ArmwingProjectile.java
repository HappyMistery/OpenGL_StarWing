package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class ArmwingProjectile implements SceneDrawable {
    private Object3D projectile = null;
    private Light light;
    private float x, y, z, sceneZ;
    private float rotation = 0f;
    private final float ROTATION_SPEED = 3f;
    private float VELOCITY_Z = -6f;

    private final Context context;
    private final GL10 gl;

    public ArmwingProjectile(GL10 gl, Context context, float x, float y, float z) {
        projectile = new Object3D(context, R.raw.projectile);
        projectile.loadTexture(gl, context, R.drawable.paleta);
        this.gl = gl;
        this.context = context;
        this.x = x;
        this.y = y;
        this.z = z;

        light = new Light(gl, GL10.GL_LIGHT2);
        light.setDiffuseColor(new float[]{0.2f, 0.2f, 0.6f, 1.0f});
        light.setSpecularColor(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
        light.setAttenuation(1.0f, 0.1f, 0.02f);
    }

    private float mapArmwingXToSceneX(float x) {
        float armwingXMin = -4f;
        float armwingXMax = 4f;
        float sceneXMin = 0f;
        float sceneXMax = 149f;

        float sceneXRange = sceneXMax - sceneXMin;
        float armwingXRange = armwingXMax - armwingXMin;

        return ((sceneXRange / armwingXRange) * (x - armwingXMin)) + sceneXMin;
    }

    private float mapArmwingYTosceneY(float y) {
        float armwingYMin = -1f;
        float armwingYMax = 1.3f;
        float sceneYMin = -9f;
        float sceneYMax = 34f;

        float sceneYRange = sceneYMax - sceneYMin;
        float armwingYRange = armwingYMax - armwingYMin;

        return ((sceneYRange / armwingYRange) * (y - armwingYMin)) + sceneYMin;
    }

    public void setPosition(float projX, float projY, float projZ) {
        x = mapArmwingXToSceneX(projX) + 325;
        y = mapArmwingYTosceneY(projY);
        z = projZ;
    }

    @Override
    public void draw(GL10 gl) {
        rotation += ROTATION_SPEED;
        z += VELOCITY_Z;

        gl.glPushMatrix();
        gl.glTranslatef(x - 2f, y, z);
        gl.glScalef(40f, 40f, 40f);
        gl.glRotatef(rotation % 360, 0, 0, 1);
        projectile.draw(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(x + 2.5f, y, z);
        gl.glScalef(40f, 40f, 40f);
        gl.glRotatef(-rotation % 360, 0, 0, 1);
        projectile.draw(gl);
        gl.glPopMatrix();

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
}
