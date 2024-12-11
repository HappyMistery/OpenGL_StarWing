package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class TutorialScreen implements HUDDrawable {
    private final float x, y;
    private final float halfHeight, halfWidth;
    private final BGImage arrowUp,arrowDown, arrowLeft, arrowRight, boostTouch, switchPOVTouch, shootTouch;
    private float respirationAlpha = 0.5f; // Initial alpha value
    private boolean alphaIncreasing = true; // Alpha direction

    public TutorialScreen(GL10 gl, Context context, float x, float y, float halfHeight, float halfWidth) {
        this.x = x;
        this.y = y;
        this.halfHeight = halfHeight;
        this.halfWidth = halfWidth;

        // Load the arrow images
        arrowUp = new BGImage();
        arrowDown = new BGImage();
        arrowLeft = new BGImage();
        arrowRight = new BGImage();

        arrowUp.loadTexture(gl, context, R.drawable.arrow);
        arrowDown.loadTexture(gl, context, R.drawable.arrow);
        arrowLeft.loadTexture(gl, context, R.drawable.arrow);
        arrowRight.loadTexture(gl, context, R.drawable.arrow);

        // Load the touch images
        boostTouch = new BGImage();
        switchPOVTouch = new BGImage();
        shootTouch = new BGImage();

        boostTouch.loadTexture(gl, context, R.drawable.touch);
        switchPOVTouch.loadTexture(gl, context, R.drawable.touch);
        shootTouch.loadTexture(gl, context, R.drawable.touch);
    }

    @Override
    public void draw(GL10 gl) {
        updateRespirationAlpha();
        float width = halfWidth * 2f;
        float height = halfHeight * 1.975f;

        gl.glEnable(GL10.GL_BLEND); // Enable transparency
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f); // Semi-transparent black background
        drawRectangle(gl, x, y, width, height+0.1f);

        // Draw arrows at the center
        gl.glPushMatrix();
        gl.glScalef(0.4f, 0.6f, 0);
        gl.glRotatef(0, 1, 1, 0);
        gl.glTranslatef(3,0,0);
        arrowRight.draw(gl);
        arrowRight.setAlpha(respirationAlpha);
        gl.glTranslatef(-6,0,0);
        gl.glRotatef(180, 0, 0, 1);
        arrowLeft.draw(gl);
        arrowLeft.setAlpha(respirationAlpha);
        gl.glTranslatef(-3,-2,0);
        gl.glRotatef(-90, 0, 0, 1);
        arrowUp.draw(gl);
        arrowUp.setAlpha(respirationAlpha);
        gl.glTranslatef(-4,0.1f,0);
        gl.glRotatef(180, 0, 0, 1);
        arrowDown.draw(gl);
        arrowDown.setAlpha(respirationAlpha);
        gl.glPopMatrix();

        // Draw touch instructions
        gl.glPushMatrix();
        gl.glScalef(0.4f, 0.6f, 0);
        gl.glRotatef(0, 1, 1, 0);
        gl.glRotatef(-90, 0, 0, 1);
        gl.glTranslatef(5.8f,7.1f,0);
        boostTouch.draw(gl);
        boostTouch.setAlpha(respirationAlpha);
        gl.glRotatef(90, 0, 0, 1);
        gl.glTranslatef(3f,11.5f,0);
        switchPOVTouch.draw(gl);
        switchPOVTouch.setAlpha(respirationAlpha);
        gl.glTranslatef(-19f,-5f,0);
        shootTouch.draw(gl);
        shootTouch.setAlpha(respirationAlpha);
        gl.glDisable(GL10.GL_BLEND); // Disable transparency
        gl.glPopMatrix();
        gl.glDisable(GL10.GL_LIGHTING);
    }

    // Helper method to draw a rectangle
    private void drawRectangle(GL10 gl, float x, float y, float width, float height) {
        gl.glPushMatrix();

        // Move to the position of the shield bar
        gl.glTranslatef(x, y, 0);

        // Draw a rectangle using GL_TRIANGLE_STRIP
        float[] vertices = {
                0, 0, 0,  // Bottom-left
                width, 0, 0,  // Bottom-right
                0, height, 0,  // Top-left
                width, height, 0   // Top-right
        };

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); // Enable vertex arrays
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, createFloatBuffer(vertices)); // Set vertex pointer
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4); // Draw the rectangle
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY); // Disable vertex arrays

        gl.glPopMatrix();
    }

    // Helper method to create a FloatBuffer from a float array
    private java.nio.FloatBuffer createFloatBuffer(float[] array) {
        java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocateDirect(array.length * 4);
        bb.order(java.nio.ByteOrder.nativeOrder());
        java.nio.FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    private void updateRespirationAlpha() {
        if (alphaIncreasing) {
            respirationAlpha += 0.07f; // Increase alpha
            if (respirationAlpha >= 1.0f) {
                alphaIncreasing = false; // Switch direction
            }
        } else {
            respirationAlpha -= 0.07f; // Decrease alpha
            if (respirationAlpha <= 0f) {
                alphaIncreasing = true; // Switch direction
            }
        }
    }
}
