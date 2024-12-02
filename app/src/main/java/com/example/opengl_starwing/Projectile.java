package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class Projectile implements SceneDrawable{
    private Object3D projectile = null;
    private Light light;
    private float x, y, z, sceneZ;
    private float rotation = 0f;
    private final float ROTATION_SPEED = 3f;
    private final float VELOCITY_Z = -5f; // Speed of movement along the z-axis

    public Projectile(GL10 gl, Context context, float x, float y, float z) {
        projectile = new Object3D(context, R.raw.projectile);
        projectile.loadTexture(gl, context, R.drawable.paleta);
        this.x = x;
        this.y = y;
        this.z = z;

        // Initialize the light for the projectile
        light = new Light(gl, GL10.GL_LIGHT2);
    }

    private float mapArmwingXToSceneX(float x) {
        float armwingXMin = -4f;
        float armwingXMax = 4f;
        float sceneXMin = 0f;
        float sceneXMax = 149f;

        // Map Armwing x to scene x range
        float sceneXRange = sceneXMax - sceneXMin;
        float armwingXRange = armwingXMax - armwingXMin;

        return ((sceneXRange / armwingXRange) * (x - armwingXMin)) + sceneXMin;
    }

    private float mapArmwingYTosceneY(float y) {
        float armwingYMin = -1f;
        float armwingYMax = 1.3f;
        float sceneYMin = -9f;
        float sceneYMax = 34f;

        // Map armwing y to scene y range
        float sceneYRange = sceneYMax - sceneYMin;
        float armwingYRange = armwingYMax - armwingYMin;

        return ((sceneYRange / armwingYRange) * (y - armwingYMin)) + sceneYMin;
    }

    public void setPosition(float armwingX, float armwingY, float z) {
        x = mapArmwingXToSceneX(armwingX) + 325;
        y = mapArmwingYTosceneY(armwingY);
        this.z = z;
    }

    @Override
    public void draw(GL10 gl) {
        rotation += ROTATION_SPEED;
        z += VELOCITY_Z;

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glScalef(30f, 30f, 30f);
        gl.glRotatef(rotation % 360, 0, 0, 1);
        projectile.draw(gl);
        gl.glPopMatrix();

        // Update the light position to follow the projectile
        light.setPosition(new float[]{x, y, z, 1.0f});
        light.setDiffuseColor(new float[]{0.2f, 0.2f, 0.6f, 1.0f});
        light.setSpecularColor(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
        light.setAttenuation(1.0f, 0.1f, 0.02f);
        light.enable();
    }

    @Override
    public void updateScenePos(float z) {
        sceneZ = z+VELOCITY_Z;
    }

    @Override
    public float getScenePos() {
        return sceneZ;
    }

    public void powerOffLight() {
        light.disable();
    }
}
