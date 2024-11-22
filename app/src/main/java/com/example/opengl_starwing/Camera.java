package com.example.opengl_starwing;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;

public class Camera {
    // Variables for the camera movement when Arwing moves
    private float camX = 0.0f;
    private float camY = 0.0f;
    private final float camZ = 20.0f;
    private static final float CAMERA_SMOOTHNESS = 0.1f;
    private int cameraView = 0;
    private static final int CAMERA_ANGLES = 3;

    public Camera() {

    }

    public float getCamZ() {
        return camZ;
    }

    public void setCameraView(GL10 gl, float arwingX, float arwingY, float halfWidth, float halfHeight) {
        // Smoothly move the camera towards the Arwing's position, all within a certain range
        camX += ((arwingX - camX) * CAMERA_SMOOTHNESS)/2;
        camY += ((arwingY - camY) * CAMERA_SMOOTHNESS)/2;
        camX = Math.max(-halfWidth/2, Math.min(camX, halfWidth/2));
        camY = Math.max(-halfHeight/2, Math.min(camY, halfHeight/2));
        if (cameraView == 0) {	// Normal camera view
            // Set camera position using gluLookAt (placing the camera at 5 units away)
            GLU.gluLookAt(gl, camX, camY, camZ , camX, camY, 0f, 0f, 1f, 0f);
        } else if (cameraView == 1) {	// Top camera view
            GLU.gluLookAt(gl, 0, 9f, camZ+5, 0, 0, camZ-10, 0f, 0f, -1f);
        } else {	// Side camera view
            //GLU.gluLookAt(gl, 18f, 5f, 10f, 0f, 0, 10f, 0f, 1f, 0f);
            GLU.gluLookAt(gl, 35.5f, 4f, 0.01f, 0f, 0, 2f, 0f, 1f, 0f);
        }
    }

    public void switchPOV() {
            cameraView = (cameraView+1)%CAMERA_ANGLES;
    }
}
