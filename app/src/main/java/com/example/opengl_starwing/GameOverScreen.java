package com.example.opengl_starwing;

import javax.microedition.khronos.opengles.GL10;

public class GameOverScreen implements HUDDrawable {
    private boolean active; // Flag to track if the Game Over screen is active
    private final float x, y, halfHeight, halfWidth;
    private float alpha = 0;

    public GameOverScreen(float x, float y, float halfHeight, float halfWidth) {
        this.x = x;
        this.y = y;
        this.halfHeight = halfHeight;
        this.halfWidth = halfWidth;
        this.active = false; // Starts inactive
    }

    // Activate the Game Over screen
    public void activate() {
        active = true;
    }

    // Check if the Game Over screen is active
    public boolean isActive() {
        return active;
    }

    // Draw the Game Over screen
    @Override
    public void draw(GL10 gl) {
        if (!active) return; // Skip drawing if not active

        gl.glEnable(GL10.GL_BLEND); // Enable transparency
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        alpha += 0.025f;
        float width = halfWidth * 2f;
        float height = halfHeight * 1.975f;
        float borderHeight = 0.05f;
        float borderWidth = 0.05f;
        // Draw the black screen
        gl.glColor4f(0.0f, 0.0f, 0.0f, alpha); // Black color
        drawRectangle(gl, x, y, width, height);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White color
        drawRectangle(gl, x, y+height, width, borderHeight);    // Top border
        drawRectangle(gl, x, y-borderHeight, width, borderHeight);  // Bottom border
        drawRectangle(gl, x, y, borderWidth, height);   // Right border
        drawRectangle(gl, x+width-borderWidth, y, borderWidth, height); // Left border
        gl.glDisable(GL10.GL_BLEND); // Disable transparency
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
}
