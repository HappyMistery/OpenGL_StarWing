package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class Armwing {
    // Variables for the armwing and its movement
    private final Object3D armwing;
    private final Object3D armwingShadow;
    private float halfWidth, halfHeight;
    private float armwingX = 0f;
    private float armwingY = 0f;
    private float armwingZ = 0f;
    private float armwingYaw = 0f;  // Rotation angle around the Z-axis
    private float armwingRoll = 0f; // Rotation angle around the Y-axis
    private float armwingPitch = 0f; // Rotation angle around the X-axis
    private float targetArmwingYaw = 0f;
    private float targetArmwingRoll = 0f;
    private float targetArmwingPitch = 0f;
    private float rotationProgress = 0f; // Tracks progress of the rotation (0 to 1)
    private static final float ROTATION_SPEED = 0.05f; // Speed at which progress increases
    private float initialYaw, initialRoll, initialPitch; // Initial angles
    private boolean isRotating = false; // Flag to check if rotation is in progress
    private float targetArmwingZ = 0f; // Target Z position for the Armwing
    private static final float Z_TRANSITION_SPEED = 0.1f; // Speed of Z transition

    private final HUD hud;
    private final Camera camera;

    public Armwing(GL10 gl, Context context, float camZ, HUD hud, Camera camera, float halfWidth, float halfHeight) {
        // Load Armwing's model
        armwing = new Object3D(context, R.raw.nau);
        armwingShadow = new Object3D(context, R.raw.nau);
        armwing.loadTexture(gl, context, R.drawable.paleta);

        armwingZ = camZ - 2.35f;    // 20 - 2.35 = 17.65
        targetArmwingZ = camZ - 2.35f;

        this.hud = hud;
        this.camera = camera;

        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
    }

    public Camera getCam() {
        return camera;
    }

    public HUD getHUD() {
        return hud;
    }

    public float getBoostPercentage() {
        return hud.getBoostPercentage();
    }

    public void setBoostPercentage(float f) {
        hud.setBoostPercentage(f);
    }

    public float getShieldPercentage() {
        return hud.getShieldPercentage();
    }

    public void setShieldPercentage(float f) {
        hud.setShieldPercentage(f);
    }

    public float getArmwingX() {
        return armwingX;
    }

    public float getArmwingY() {
        return armwingY;
    }

    public float getArmwingZ() {
        return armwingZ;
    }

    public void setTargetArmwingZ(float targetZ) {
        targetArmwingZ += targetZ;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public float getArmwingRoll(){
        return armwingRoll;
    }

    public float getArmwingYaw(){
        return armwingYaw;
    }

    public float getArmwingPitch(){
        return armwingPitch;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public void draw(GL10 gl) {
        // Smoothly transition the Armwing's z position
        if (Math.abs(targetArmwingZ - armwingZ) > 0.01f) {
            armwingZ += (targetArmwingZ - armwingZ) * Z_TRANSITION_SPEED;
        }

        // Update rotation using sinusoidal easing if rotation is in progress
        if (isRotating) {
            rotationProgress = Math.min(1f, rotationProgress + ROTATION_SPEED);
            float easedProgress = (float) Math.sin(rotationProgress * Math.PI / 2); // Eases progress with sin

            armwingYaw = initialYaw + easedProgress * (targetArmwingYaw - initialYaw);
            armwingRoll = initialRoll + easedProgress * (targetArmwingRoll - initialRoll);
            armwingPitch = initialPitch + easedProgress * (targetArmwingPitch - initialPitch);

            // End rotation if the progress is complete
            if (rotationProgress >= 1f) {
                isRotating = false;
            }
        }

        // Draw the Armwing
        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glTranslatef(armwingX, armwingY, armwingZ);
        gl.glRotatef((armwingYaw) % 360, 0, 0, 1); // Tilt the Armwing
        gl.glRotatef(armwingRoll, 0, 1, 0); // Roll the Armwing
        gl.glRotatef(armwingPitch, 1, 0, 0); // Pitch the Armwing
        armwing.draw(gl);
        gl.glPopMatrix(); // Restore the transformation matrix

        float normalizedY = (armwingY - (-halfHeight)) / (halfHeight - (-halfHeight));
        normalizedY = Math.min(1.0f, Math.max(0.0f, normalizedY)); // Ensure clamping between 0 and 1

        // Use the normalizedY to calculate shadowScale
        float shadowScale = Math.min(0.9f, Math.max(0.3f, 1.0f - normalizedY * 0.9f));

        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glTranslatef(armwingX, -1.25f, armwingZ + 0.1f);
        gl.glScalef(shadowScale, shadowScale, shadowScale); // Scale the Armwing shadow
        gl.glRotatef(-armwingYaw, 0, 0, 1); // Tilt the Armwing shadow
        gl.glRotatef(180 + armwingRoll, 0, 1, 0); // Roll the Armwing shadow
        gl.glRotatef(195 - Math.min(armwingPitch, 3), 1, 0, 0); // Pitch the Armwing shadow
        gl.glDisable(GL10.GL_LIGHTING);
        armwingShadow.setAlpha(0.5f);
        armwingShadow.setRGB(0.1f, 0.1f, 0.1f);
        armwingShadow.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glPopMatrix(); // Restore the transformation matrix
    }

    public void setHalfHeight(float halfHeight) {
        this.halfHeight = halfHeight;
    }

    public void setHalfWidth(float halfWidth) {
        this.halfWidth = halfWidth;
    }

    public void move(float deltaX, float deltaY) {
        // Clamp the Armwing's position to keep it within the viewport
        armwingX = Math.max(-halfWidth, Math.min(armwingX + deltaX, halfWidth));
        armwingY = Math.max(-halfHeight, Math.min(armwingY + deltaY, halfHeight + 0.3f));

        // Start rotation
        startRotation(deltaX, deltaY);
    }

    private void startRotation(float deltaX, float deltaY) {
        float rotationAngle = 15;

        // Set initial and target values for rotation
        initialYaw = armwingYaw;
        initialRoll = armwingRoll;
        initialPitch = armwingPitch;

        if (deltaX > 0) {
            targetArmwingYaw = -rotationAngle; // Tilt right
            targetArmwingRoll = -rotationAngle * 2; // Roll right
        } else if (deltaX < 0) {
            targetArmwingYaw = rotationAngle; // Tilt left
            targetArmwingRoll = rotationAngle * 2; // Roll left
        } else {
            targetArmwingYaw = 0; // No tilt
            targetArmwingRoll = 0; // No roll
        }

        if (deltaY > 0) {
            targetArmwingPitch = rotationAngle * 3; // Tilt up
        } else if (deltaY < 0) {
            targetArmwingPitch = -rotationAngle; // Tilt down
        } else {
            targetArmwingPitch = 0; // No tilt
        }

        rotationProgress = 0f; // Reset progress
        isRotating = true; // Start rotation
    }

    public void rotate(int angle) {
        if (targetArmwingYaw == 0) {
            targetArmwingYaw = (angle > 0) ? 90 : -90;
        } else {
            // If already rotated, reset to 0
            targetArmwingYaw = 0;
        }
    }

    public void resetAngle() {
        targetArmwingYaw = 0;
        targetArmwingRoll = 0;
        targetArmwingPitch = 0;
    }

    public void shootProjectile(Scene scene) {
        scene.shootArmwingProjectile(armwingX, armwingY-0.12f, armwingZ);
    }
}
