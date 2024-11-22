package com.example.opengl_starwing;

import javax.microedition.khronos.opengles.GL10;

public class HealthBar implements Drawable {
    private final float x, y;
    private float width;
    private float height;
    private float borderHeight;
    private float borderWidth; // Position and size of the health bar
    private float healthPercentage;    // Health percentage (0 to 1)

    public HealthBar(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.borderHeight = 0.05f;
        this.borderWidth = 0.05f;
        this.healthPercentage = 1.0f; // Full health by default
    }

    // Method to set the health percentage (0 to 1)
    public void setHealthPercentage(float healthPercentage) {
        if(this.healthPercentage > 0) {
            this.healthPercentage = healthPercentage;
        }
    }
    // Method to get the health percentage (0 to 1)
    public float getHealthPercentage() {
        return this.healthPercentage;
    }

    // Method to draw the health bar
    @Override
    public void draw(GL10 gl) {
        // Draw the health portion (red) according to healthPercentage
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f); // Red color
        drawRectangle(gl, x, y, width * healthPercentage, height);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White color
        drawRectangle(gl, x, y+height, width, borderHeight);    // Top border
        drawRectangle(gl, x, y-borderHeight, width, borderHeight);  // Bottom border
        drawRectangle(gl, x, y, borderWidth, height);   // Right border
        drawRectangle(gl, x+width-borderWidth, y, borderWidth, height); // Left border
    }

    @Override
    public float getZ() {
        return 0;
    }

    // Helper method to draw a rectangle
    private void drawRectangle(GL10 gl, float x, float y, float width, float height) {
        gl.glPushMatrix(); // Save current matrix

        // Move to the position of the health bar
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
