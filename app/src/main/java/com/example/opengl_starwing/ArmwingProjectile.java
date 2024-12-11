package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class ArmwingProjectile implements SceneDrawable {
    private final Object3D projectile;
    private final Light light;
    private float x, y, z, sceneZ;
    private float rotation = 0f;
    private final float VELOCITY_Z = -6f;


    public ArmwingProjectile(GL10 gl, Context context, float x, float y, float z) {
        // Load projectile model
        projectile = new Object3D(context, R.raw.projectile);
        projectile.loadTexture(gl, context, R.drawable.paleta);

        this.x = x;
        this.y = y;
        this.z = z;

        // Initialize and configure the light
        light = new Light(gl, GL10.GL_LIGHT2);
        light.setDiffuseColor(new float[]{0.2f, 0.2f, 0.6f, 1.0f}); // Blue-ish light
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

        gl.glPushMatrix();
        gl.glTranslatef(x - 2f, y, z);  // Little offset on the x axis to make it the left side projectile
        gl.glScalef(40f, 40f, 40f); // Make that BIG BOY
        gl.glRotatef(rotation % 360, 0, 0, 1);  // Projectile spins
        projectile.draw(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(x + 2.5f, y, z);    // Little offset on the x axis to make it the right side projectile
        gl.glScalef(40f, 40f, 40f); // Make that BIG BOY
        gl.glRotatef(-rotation % 360, 0, 0, 1); // Projectile spins contrary to left side projectile
        projectile.draw(gl);
        gl.glPopMatrix();

        light.setPosition(new float[]{x, y, z, 1.0f});  // Update the light position to follow the projectile
        light.enable();
    }

    // Map the Armwing's X position to the scene's X position in order to place the projectile in the scene
    private float mapArmwingXToSceneX(float x) {
        float armwingXMin = -4f;
        float armwingXMax = 4f;
        float sceneXMin = 0f;
        float sceneXMax = 149f;

        float sceneXRange = sceneXMax - sceneXMin;  // Calculate the scene's coords range (eg. from 0 to 149)
        float armwingXRange = armwingXMax - armwingXMin;    // Calculate the Armwing's coords range (eg. from -4 to 4)

        return ((sceneXRange / armwingXRange) * (x - armwingXMin)) + sceneXMin; // Map the coordinates
    }

    // Map the Armwing's Y position to the scene's Y position in order to place the projectile in the scene
    private float mapArmwingYTosceneY(float y) {
        float armwingYMin = -1f;
        float armwingYMax = 1.3f;
        float sceneYMin = -9f;
        float sceneYMax = 34f;

        float sceneYRange = sceneYMax - sceneYMin;  // Calculate the scene's coords range (eg. from -9 to 34)
        float armwingYRange = armwingYMax - armwingYMin;    // Calculate the scene's coords range (eg. from -1 to 1.3)

        return ((sceneYRange / armwingYRange) * (y - armwingYMin)) + sceneYMin; // Map the coordinates
    }

    // Place the projectile in the scene with the scene coordinate system
    public void setPosition(float projX, float projY, float projZ) {
        x = mapArmwingXToSceneX(projX) + 325;
        y = mapArmwingYTosceneY(projY);
        z = projZ;
    }

    @Override
    public void updateScenePos(float z) {
        sceneZ = z + VELOCITY_Z;
    }

    public void powerOffLight() {
        light.disable();
    }
}
