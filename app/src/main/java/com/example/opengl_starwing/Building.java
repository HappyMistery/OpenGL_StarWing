package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Building implements SceneDrawable{
    private Object3D building = null;
    private Object3D buildingShadow = null; // Shadow object
    private float x, y, z, shadowY;
    private final Random random = new Random();
    private float sceneZ;
    private float rotationY; // Random Y-axis rotation
    private float alpha = 0.0f; // Initial alpha value
    private final float TRANSPARENCY_THRESHOLD = 20f;
    private final float FADE_IN_DISTANCE = 50f; // Range over which the building fades in
    private final float X_COLLISION_THRESHOLD = 1.5f;
    private float Y_COLLISION_THRESHOLD;
    private boolean collided = false;

    private Armwing armwing = null;

    public Building(GL10 gl, Context context, float x, float y, float z) {
        this.rotationY = 0;
        Y_COLLISION_THRESHOLD = 5f;
        switch (random.nextInt(4)) {
            case 0:
                building = new Object3D(context, R.raw.building1);
                buildingShadow = new Object3D(context, R.raw.building1); // Initialize shadow
                this.y = 2.5f;
                shadowY = -5f;
                break;
            case 1:
                building = new Object3D(context, R.raw.building2);
                buildingShadow = new Object3D(context, R.raw.building2); // Initialize shadow
                this.y = 2.1f;
                shadowY = -2.5f;
                break;
            case 2:
                building = new Object3D(context, R.raw.building3);
                buildingShadow = new Object3D(context, R.raw.building3); // Initialize shadow
                this.y = -0.1f;
                Y_COLLISION_THRESHOLD = 1.1f;
                shadowY = 0.1f;
                break;
            case 3:
                building = new Object3D(context, R.raw.building4);
                buildingShadow = new Object3D(context, R.raw.building4); // Initialize shadow
                this.y = -0.2f;
                Y_COLLISION_THRESHOLD = 0.9f;
                shadowY = 0.2f;
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
        collided = false;
    }

    public void setArmwing(Armwing armwing) {
        this.armwing = armwing;
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

    private float mapBuildingXToArmwingX() {
        float buildingXMin = 20f;
        float buildingXMax = 32f;
        float armwingXMin = -4f;
        float armwingXMax = 4f;

        // Map building x to Armwing x range
        float armwingXRange = armwingXMax - armwingXMin;
        float buildingXRange = buildingXMax - buildingXMin;

        return ((armwingXRange / buildingXRange) * ((x-0.75f) - buildingXMin)) + armwingXMin;
    }

    private float mapBuildingYToArmwingY() {
        float buildingYMin = -0.2f;
        float buildingYMax = 2.5f;
        float armwingYMin = -1f;
        float armwingYMax = 1.3f;

        // Map building y to Armwing y range
        float armwingYRange = armwingYMax - armwingYMin;
        float buildingYRange = buildingYMax - buildingYMin;

        return ((armwingYRange / buildingYRange) * (-buildingYMin)) + armwingYMin;
    }


    private void checkArmwingColision() {
        float armwingX = armwing.getArmwingX();
        float armwingY = armwing.getArmwingY();
        float mappedBuildingX = mapBuildingXToArmwingX();
        float mappedBuildingY = mapBuildingYToArmwingY();

        if (!collided && x >= 20 && x <= 32) {
            if ((Math.abs(armwingX - mappedBuildingX) < X_COLLISION_THRESHOLD) &&
                    (Math.abs(armwingY - mappedBuildingY) < Y_COLLISION_THRESHOLD) &&
                    (sceneZ >= 415f) && (sceneZ <= 420f)) {
                armwing.setShieldPercentage(armwing.getShieldPercentage() - 0.25f);
                collided = true;
                armwing.getCam().startShake(0.5f, 1.0f);
            }
        }
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(15f, 15f, 12f);
        gl.glTranslatef(x, y, z);
        gl.glRotatef(rotationY, 0f, 1f, 0f); // Apply random rotation on Y-axis
        if(alpha < 1f) {
            gl.glColor4f(1f, 1f, 1f, alpha);
            updateAlpha(sceneZ);
        }
        building.draw(gl);
        checkArmwingColision();

        // Draw the shadow
        /*
        gl.glScalef(0.8f, 0.8f, 0.8f);
        gl.glTranslatef(0, shadowY, 0);
        gl.glRotatef(180, 1f, 0f, 0f);
        gl.glDisable(GL10.GL_LIGHTING);
        buildingShadow.setAlpha(1f);
        buildingShadow.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
         */
        gl.glPopMatrix();
    }
}
