package com.example.opengl_starwing;

import javax.microedition.khronos.opengles.GL10;

public class Button implements HUDDrawable {
    private float x, y, width, height, borderHeight, borderWidth; // Position and size of the cam view button

    public Button(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        borderWidth = 0.05f;
        borderHeight = 0.05f;
    }

    // Method to draw the cam view button
    @Override
    public void draw(GL10 gl) {
        gl.glColor4f(0, 0, 0, 1.0f); // Black color
        drawRectangle(gl, x, y, width, height);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White color
        drawRectangle(gl, x, y-borderHeight, width, borderHeight);  // Bottom border
        drawRectangle(gl, x, y, borderWidth, height); // Left border
    }

    // Helper method to draw a rectangle
    private void drawRectangle(GL10 gl, float x, float y, float width, float height) {
        gl.glPushMatrix(); // Save current matrix

        // Move to the position of the cam view button
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

        gl.glPopMatrix(); // Restore matrix
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
