package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class Arwing {
    // Variables for the Arwing and its movement
    private final Object3D arwing;
    private final Object3D arwingShadow;
    private float arwingX = 0f;
    private float arwingY = 0f;
    private float arwingZ = 0f;
    private float arwingYaw = 0f;  // Rotation angle around the Z-axis
    private float arwingRoll = 0f; // Rotation angle around the Y-axis
    private float arwingPitch = 0f; // Rotation angle around the X-axis
    private float targetArwingYaw = 0f;
    private float targetArwingRoll = 0f;
    private float targetArwingPitch = 0f;
    private float targetArwingZ = 0f; // Target Z position for the Arwing
    private static final float Z_TRANSITION_SPEED = 0.1f; // Speed of Z transition
    private static final float ROTATION_SPEED = 0.1f;

    public Arwing(GL10 gl, Context context, float camZ) {
        // Load Arwing's model
        arwing = new Object3D(context, R.raw.nau);
        arwingShadow = new Object3D(context, R.raw.nau);
        arwing.loadTexture(gl, context, R.drawable.paleta);

        arwingZ = camZ - 2.35f;
        targetArwingZ = camZ - 2.35f;
    }

    public float getArwingX() {
        return arwingX;
    }

    public float getArwingY() {
        return arwingY;
    }

    public void setTargetArwingZ(float targetZ) {
        targetArwingZ += targetZ;
    }

    public void draw(GL10 gl, float halfHeight) {
        // Smoothly transition the Arwing's z position
        if (Math.abs(targetArwingZ - arwingZ) > 0.01f) {
            arwingZ += (targetArwingZ - arwingZ) * Z_TRANSITION_SPEED;
        }
        // Draw the Arwing
        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glScalef(1.0f, 1.0f, 1.0f); // Scale the Arwing
        gl.glTranslatef(arwingX, arwingY, arwingZ);
        gl.glRotatef((arwingYaw) % 360, 0, 0, 1); // Tilt the arwing
        gl.glRotatef(arwingRoll, 0, 1, 0); // Roll the arwing
        gl.glRotatef(arwingPitch, 1, 0, 0); // Pitch the arwing
        // Gradually rotate the Arwing towards the target angle
        if (Math.abs(targetArwingYaw - arwingYaw) > 0.01f) {
            arwingYaw += (targetArwingYaw - arwingYaw) * ROTATION_SPEED;
        }
        if (Math.abs(targetArwingRoll - arwingRoll) > 0.01f) {
            arwingRoll += (targetArwingRoll - arwingRoll) * ROTATION_SPEED;
        }
        if (Math.abs(targetArwingPitch - arwingPitch) > 0.01f) {
            arwingPitch += (targetArwingPitch - arwingPitch) * ROTATION_SPEED;
        }
        arwing.draw(gl);
        gl.glPopMatrix(); // Restore the transformation matrix

        float normalizedY = (arwingY - (-halfHeight)) / (halfHeight - (-halfHeight));
        normalizedY = Math.min(1.0f, Math.max(0.0f, normalizedY)); // Ensure clamping between 0 and 1

        // Use the normalizedY to calculate shadowScale
        float shadowScale = Math.min(0.9f, Math.max(0.3f, 1.0f - normalizedY * 0.9f));

        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glTranslatef(arwingX, -1.25f, arwingZ+0.1f);
        gl.glScalef(shadowScale, shadowScale, shadowScale); // Scale the Arwing shadow
        gl.glRotatef(-arwingYaw, 0, 0, 1); // Tilt the arwing shadow
        gl.glRotatef(180+arwingRoll, 0, 1, 0); // Roll the arwing shadow
        gl.glRotatef(195-Math.min(arwingPitch, 3), 1, 0, 0); // Pitch the arwing shadow
        gl.glEnable(GL10.GL_BLEND); // Enable transparency
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL10.GL_LIGHTING);
        arwingShadow.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glDisable(GL10.GL_BLEND); // Disable transparency
        gl.glPopMatrix(); // Restore the transformation matrix
    }

    public void move(float deltaX, float deltaY, float halfWidth, float halfHeight) {
        // Clamp the Arwing's position to keep it within the viewport
        arwingX = Math.max(-halfWidth, Math.min(arwingX+=deltaX, halfWidth));
        arwingY = Math.max(-halfHeight, Math.min(arwingY+=deltaY, halfHeight+0.3f));
        float rotationAngle = 15;
        if (deltaX > 0) {
            targetArwingYaw = -rotationAngle; // Tilt right when moving right
            targetArwingRoll = -rotationAngle*2; // Rotate right on Y-axis
        } else if (deltaX < 0) {
            targetArwingYaw = rotationAngle; // Tilt left when moving left
            targetArwingRoll = rotationAngle*2; // Rotate left on Y-axis
        } else {
            targetArwingYaw = 0; // No tilt when not moving horizontally
            targetArwingRoll = 0; // No rotation on Y-axis when not moving horizontally
        }

        if (deltaY > 0) {
            targetArwingPitch = rotationAngle*3;
        } else if (deltaY < 0) {
            targetArwingPitch = -rotationAngle/2;
        } else {
            targetArwingPitch = 0;
        }
    }

    public void rotate(int angle) {
        if (targetArwingYaw == 0) {
            targetArwingYaw = (angle > 0) ? 90 : -90;
        } else {
            // If already rotated, reset to 0
            targetArwingYaw = 0;
        }
    }

    public void resetAngle() {
        targetArwingYaw = 0;
        targetArwingRoll = 0;
        targetArwingPitch = 0;
    }
}
