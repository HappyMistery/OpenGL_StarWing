package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class ShieldBar implements HUDDrawable {
    private final float x, y;
    private float shieldPercentage;    // Health percentage (0 to 1)

    public ShieldBar(GL10 gl, Context context, float x, float y) {
        this.x = x;
        this.y = y;
        this.shieldPercentage = 1.0f; // Full shield by default
    }

    // Method to set the shield percentage (0 to 1)
    public void setHealthPercentage(float shieldPercentage) {
        if(this.shieldPercentage > 0) {
            this.shieldPercentage = shieldPercentage;
        }
    }
    // Method to get the shield percentage (0 to 1)
    public float getHealthPercentage() {
        return this.shieldPercentage;
    }

    // Method to draw the shield bar
    @Override
    public void draw(GL10 gl) {
        float width = 1.75f;
        float height = 0.3f;
        float borderHeight = 0.05f;
        float borderWidth = 0.05f;
        // Draw the shield portion (red) according to shieldPercentage
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f); // Red color
        drawRectangle(gl, x, y, width * shieldPercentage, height);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White color
        drawRectangle(gl, x, y+height, width, borderHeight);    // Top border
        drawRectangle(gl, x, y-borderHeight, width, borderHeight);  // Bottom border
        drawRectangle(gl, x, y, borderWidth, height);   // Right border
        drawRectangle(gl, x+width-borderWidth, y, borderWidth, height); // Left border
    }

    // Helper method to draw a rectangle
    private void drawRectangle(GL10 gl, float x, float y, float width, float height) {
        gl.glPushMatrix(); // Save current matrix

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
