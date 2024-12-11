package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Building implements SceneDrawable{
    private Object3D building = null;
    private Armwing armwing = null;
    private float x, y, z, sceneZ;
    private float rotationY; // Random Y-axis rotation
    private float Y_COLLISION_THRESHOLD, X_COLLISION_THRESHOLD;
    private boolean collided = false;

    public Building(GL10 gl, Context context, float x, float y, float z) {
        this.rotationY = 0; // Building is not rotated by default
        Y_COLLISION_THRESHOLD = 5f; // Can't go over the  building by default
        Random random = new Random();
        switch (random.nextInt(4)) {    // Select a building model at random out of 4 models
            case 0:
                building = new Object3D(context, R.raw.building1);  // Tall building
                this.y = 2.5f;  // Tall building
                X_COLLISION_THRESHOLD = 1.4f;
                break;
            case 1:
                building = new Object3D(context, R.raw.building2);  // Tall building
                this.y = 2.1f;  // Tall building
                X_COLLISION_THRESHOLD = 1.5f;
                break;
            case 2:
                building = new Object3D(context, R.raw.building3);  // Small building
                this.y = -0.1f; // Small building
                X_COLLISION_THRESHOLD = 1.5f;
                Y_COLLISION_THRESHOLD = 1.1f;   // Can go over the building
                break;
            case 3:
                building = new Object3D(context, R.raw.building4);  // Small building
                this.y = -0.2f; // Small building
                X_COLLISION_THRESHOLD = 0.9f;
                Y_COLLISION_THRESHOLD = 0.9f;   // Can go over the building
                this.rotationY = random.nextBoolean() ? 0f : 90f; // Randomly rotate 0 or 90 degrees
                break;
        }
        building.loadTexture(gl, context, R.drawable.black);
        this.x = x;
        this.z = z;
    }

    public void setPosition(float x, float z) {
        this.x = x;
        this.z = z;
        collided = false;   // Reset collision flag when building is moved (when building is replaced again in the scene)
    }

    public void setArmwing(Armwing armwing) {
        this.armwing = armwing;
    }

    public float getScenePos() {
        return sceneZ;
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(15f, 15f, 12f);
        gl.glTranslatef(x, y, z);
        gl.glRotatef(rotationY, 0f, 1f, 0f); // Apply random rotation on Y-axis
        building.draw(gl);
        checkArmwingColision(); // Check if the Armwing has collided with the building
        gl.glPopMatrix();
    }

    public void updateScenePos(float z) {
        sceneZ = z;
    }

    // Map the Building's X position to the Armwing's X position in order to check collisions easily
    private float mapBuildingXToArmwingX() {
        float buildingXMin = 22f;
        float buildingXMax = 30f;
        float armwingXMin = -4f;
        float armwingXMax = 4f;

        return armwingXMin + (((x-0.5f) - buildingXMin) / (buildingXMax - buildingXMin)) * (armwingXMax - armwingXMin);
    }

    // Map the Building's Y position to the Armwing's Y position in order to check collisions easily
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

        if (!collided) {    // If the Armwing hasn't collided already with this building, check collision
            if ((Math.abs(armwingX - mappedBuildingX) < X_COLLISION_THRESHOLD) &&
                    (Math.abs(armwingY - mappedBuildingY) < Y_COLLISION_THRESHOLD) &&
                    (sceneZ >= 415f) && (sceneZ <= 425f)) { // If all axes coincide, the Armwing collided with the building
                armwing.setShieldPercentage(armwing.getShieldPercentage() - 0.25f);
                collided = true;    // Flag that the Armwing has collided with this building so that it doesn't collide again
                armwing.getCam().startShake(0.5f, 1.0f);    // Make that cam SHAKE!
            }
        }
    }
}
