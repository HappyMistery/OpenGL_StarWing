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
    private float buildingHeight;
    private float rotationY; // Random Y-axis rotation
    private float alpha = 0.0f; // Initial alpha value
    private final float TRANSPARENCY_THRESHOLD = 20f;
    private final float FADE_IN_DISTANCE = 50f; // Range over which the building fades in
    private final float COLLISION_THRESHOLD = 1.5f;
    private boolean collided = false;

    private Arwing arwing = null;

    public Building(GL10 gl, Context context, float x, float y, float z) {
        this.rotationY = 0;
        switch (random.nextInt(4)) {
            case 0:
                building = new Object3D(context, R.raw.building1);
                buildingShadow = new Object3D(context, R.raw.building1); // Initialize shadow
                this.y = 2.5f;
                buildingHeight = this.y * 3;
                shadowY = -5f;
                break;
            case 1:
                building = new Object3D(context, R.raw.building2);
                buildingShadow = new Object3D(context, R.raw.building2); // Initialize shadow
                this.y = 2.1f;
                buildingHeight = this.y * 3;
                shadowY = -2.5f;
                break;
            case 2:
                building = new Object3D(context, R.raw.building3);
                buildingShadow = new Object3D(context, R.raw.building3); // Initialize shadow
                this.y = -0.1f;
                buildingHeight = 1.2f;
                shadowY = 0.1f;
                break;
            case 3:
                building = new Object3D(context, R.raw.building4);
                buildingShadow = new Object3D(context, R.raw.building4); // Initialize shadow
                this.y = -0.2f;
                buildingHeight = 1.1f;
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

    public void setArwing(Arwing arwing) {
        this.arwing = arwing;
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

    private float mapBuildingXToArwingX() {
        float buildingXMin = 0f;
        float buildingXMax = 53f;
        float arwingXMin = -4f;
        float arwingXMax = 4f;

        // Map portal x to Arwing x range
        float arwingXRange = arwingXMax - arwingXMin;
        float buildingXRange = buildingXMax - buildingXMin;

        return ((arwingXRange / buildingXRange) * (x - buildingXMin)) + arwingXMin;
    }


    private void checkArwingColision() {
        float arwingX = arwing.getArwingX();
        float arwingY = arwing.getArwingY();
        float mappedBuildingX = mapBuildingXToArwingX();

        if (!collided) {
            if((sceneZ >= 420f) && (sceneZ <= 425f)) {
                if ((Math.abs(arwingX - mappedBuildingX) < COLLISION_THRESHOLD) &&
                        (arwingY + 1 < buildingHeight)) {
                    arwing.setShieldPercentage(arwing.getShieldPercentage() - 0.25f);
                    collided = true;
                }
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
        checkArwingColision();

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
