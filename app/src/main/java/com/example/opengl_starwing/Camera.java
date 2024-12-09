package com.example.opengl_starwing;

import android.opengl.GLU;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Camera {
    // Variables for the camera movement when Armwing moves
    private float camX = 0.0f;
    private float camY = 0.0f;
    private final float camZ = 20.0f;
    private float cameraRotationY = 0.0f; // Rotation of the camera on the Y-axis
    private float cameraRotationX = 0.0f; // Rotation of the camera on the X-axis
    private static final float CAMERA_SMOOTHNESS = 0.1f;
    private static final float ROTATION_SENSITIVITY = 2.0f; // How much the camera rotates based on Armwing X-movement
    private int cameraView = 0;
    private static final int CAMERA_ANGLES = 3;

    // Shake parameters
    private float shakeIntensity = 0.0f;
    private float shakeDuration = 0.0f;
    private final Random random = new Random();

    private boolean isTransitioningToEndGame = false;
    private static final float TRANSITION_SPEED = 0.005f; // Adjust for desired smoothness

    private final GL10 gl;

    public Camera(GL10 gl) {
        this.gl = gl;
    }

    public float getCamZ() {
        return camZ;
    }

    public void setCameraView(GL10 gl, Armwing armwing, float halfWidth, float halfHeight) {
        if (isTransitioningToEndGame) {
            float targetCamX = 0.0f;
            float targetCamY = 5.0f;
            float targetCamZ = 100.0f;

            // Smoothly interpolate camera position to the target position
            camX += (targetCamX - camX) * TRANSITION_SPEED;
            camY += (targetCamY - camY) * TRANSITION_SPEED;
            // camZ is constant in normal view, so interpolate it for the end-game view
            float currentCamZ = camZ;
            currentCamZ += (targetCamZ - currentCamZ) * TRANSITION_SPEED;

            // Check if the camera has reached its target
            if (Math.abs(targetCamX - camX) < 0.01f &&
                    Math.abs(targetCamY - camY) < 0.01f &&
                    Math.abs(targetCamZ - currentCamZ) < 0.01f) {
                // Snap to final position and stop transitioning
                camX = targetCamX;
                camY = targetCamY;
            }

            GLU.gluLookAt(gl, camX, camY, currentCamZ,
                    camX, 0, 0f,
                    0f, 1f, 0f);
        } else {
            // Smoothly move the camera towards the Armwing's position, within a range
            camX += ((armwing.getArmwingX() - camX) * CAMERA_SMOOTHNESS) / 2;
            camY += ((armwing.getArmwingY() - camY) * CAMERA_SMOOTHNESS) / 2;
            camX = Math.max(-halfWidth / 2, Math.min(camX, halfWidth / 2));
            camY = Math.max(-halfHeight / 2, Math.min(camY, halfHeight / 2));

            // Smoothly adjust the Y-axis rotation based on Armwing's roll
            float maxRotationAngle = 3.0f; // Maximum tilt angle in degrees
            if (armwing.isRotating()) {
                // Adjust the camera rotation based on the Armwing's roll for Y-axis
                float targetRotationY = -armwing.getArmwingRoll() * (1 / maxRotationAngle);
                cameraRotationY += (targetRotationY - cameraRotationY) * CAMERA_SMOOTHNESS;

                // Adjust the camera rotation based on the Armwing's yaw for X-axis
                float targetRotationX = Math.abs(armwing.getArmwingYaw()) * (1 / maxRotationAngle);
                cameraRotationX += (targetRotationX - cameraRotationX) * CAMERA_SMOOTHNESS;
                // (Pitch affects the X-axis of the camera)
                float targetRotationPitch = -armwing.getArmwingPitch() * (1 / maxRotationAngle);
                cameraRotationX += (targetRotationPitch - cameraRotationX) * CAMERA_SMOOTHNESS;
            } else {
                // Smoothly reset both rotations to neutral
                cameraRotationY += (0.0f - cameraRotationY) * CAMERA_SMOOTHNESS;
                cameraRotationX += (0.0f - cameraRotationX) * CAMERA_SMOOTHNESS;
            }

            if (cameraView == 0) { // Normal camera view
                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadIdentity();

                gl.glRotatef(cameraRotationY, 0f, 1f, 0f); // Rotate the camera on the Y-axis
                gl.glRotatef(cameraRotationX, 1f, 0f, 0f); // Rotate the camera on the X-axis (for yaw)

                applyShake();

                // Set camera position using gluLookAt
                GLU.gluLookAt(gl, camX, camY, camZ,
                        camX, camY, 0f,
                        0f, 1f, 0f);
            } else if (cameraView == 1) { // Top camera view
                GLU.gluLookAt(gl, 0, 9f, camZ + 5,
                        0, 0, camZ - 10,
                        0f, 0f, -1f);
            } else { // Side camera view
                GLU.gluLookAt(gl, 35.5f, 4f, 0.01f,
                        0f, 0, 2f,
                        0f, 1f, 0f);
            }
        }
    }

    public void setEndGameCamView() {
        isTransitioningToEndGame = true;
    }

    public void switchPOV() {
        cameraView = (cameraView + 1) % CAMERA_ANGLES;
    }

    public void startShake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
    }

    private void applyShake() {
        if (shakeDuration > 0) {
            camX += (random.nextFloat() - 0.5f) * shakeIntensity;
            camY += (random.nextFloat() - 0.5f) * shakeIntensity;
            // Rate at which shake reduces
            float shakeDecay = 0.9f;
            shakeDuration *= shakeDecay; // Reduce shake over time
            if (shakeDuration < 0.01f) {
                shakeDuration = 0;
                shakeIntensity = 0;
            }
        }
    }
}
