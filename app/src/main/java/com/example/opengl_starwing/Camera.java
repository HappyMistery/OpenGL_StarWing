package com.example.opengl_starwing;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;

public class Camera {
    // Variables for the camera movement when Arwing moves
    private float camX = 0.0f;
    private float camY = 0.0f;
    private final float camZ = 20.0f;
    private float cameraRotationY = 0.0f; // Rotation of the camera on the Y-axis
    private float cameraRotationX = 0.0f; // Rotation of the camera on the X-axis
    private static final float CAMERA_SMOOTHNESS = 0.1f;
    private static final float ROTATION_SENSITIVITY = 2.0f; // How much the camera rotates based on Arwing X-movement
    private int cameraView = 0;
    private static final int CAMERA_ANGLES = 3;

    public Camera() {}

    public float getCamZ() {
        return camZ;
    }

    public void setCameraView(GL10 gl, Arwing arwing, float halfWidth, float halfHeight) {
        // Smoothly move the camera towards the Arwing's position, within a range
        camX += ((arwing.getArwingX() - camX) * CAMERA_SMOOTHNESS) / 2;
        camY += ((arwing.getArwingY() - camY) * CAMERA_SMOOTHNESS) / 2;
        camX = Math.max(-halfWidth / 2, Math.min(camX, halfWidth / 2));
        camY = Math.max(-halfHeight / 2, Math.min(camY, halfHeight / 2));

        // Smoothly adjust the Y-axis rotation based on Arwing's roll
        float maxRotationAngle = 3.0f; // Maximum tilt angle in degrees
        if (arwing.isRotating()) {
            // Adjust the camera rotation based on the Arwing's roll for Y-axis
            float targetRotationY = -arwing.getArwingRoll() * (1 / maxRotationAngle);
            cameraRotationY += (targetRotationY - cameraRotationY) * CAMERA_SMOOTHNESS;

            // Adjust the camera rotation based on the Arwing's yaw for X-axis
            float targetRotationX = Math.abs(arwing.getArwingYaw()) * (1 / maxRotationAngle);
            cameraRotationX += (targetRotationX - cameraRotationX) * CAMERA_SMOOTHNESS;
            // (Pitch affects the X-axis of the camera)
            float targetRotationPitch = -arwing.getArwingPitch() * (1 / maxRotationAngle);
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

    public void resetRotation() {
        cameraRotationY = 0.0f;
    }

    public void switchPOV() {
        cameraView = (cameraView + 1) % CAMERA_ANGLES;
    }
}
