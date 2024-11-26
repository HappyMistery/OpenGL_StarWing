package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class CharacterPicture implements Drawable {
    BGImage character = new BGImage();
    private float x, y;
    private int fileNameID;

    public CharacterPicture(GL10 gl, Context context, float x, float y, int fileNameID) {
        character.loadTexture(gl, context, fileNameID);
        this.fileNameID = fileNameID;
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(GL10 gl) {
        float width = 1.4f;
        float height = 1.3f;
        float borderHeight = 0.05f;
        float borderWidth = 0.05f;
        gl.glPushMatrix();
        gl.glScalef(0.65f, 0.65f, 0.0f); // Scale the image
        gl.glTranslatef(x-0.65f, y-1f, 0);
        character.draw(gl);
        gl.glPopMatrix();
        gl.glDisable(GL10.GL_LIGHTING);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White color
        drawRectangle(gl, x-borderHeight*6, y+height, width, borderHeight);    // Top border
        drawRectangle(gl, x-borderHeight*6, y-borderHeight, width, borderHeight);  // Bottom border
        drawRectangle(gl, x-borderHeight*6, y, borderWidth, height);   // Right border
        drawRectangle(gl, x+width-borderWidth*7, y, borderWidth, height); // Left border
        gl.glEnable(GL10.GL_LIGHTING);
    }

    private void drawRectangle(GL10 gl, float x, float y, float width, float height) {
        gl.glPushMatrix(); // Save current matrix
        // Move to the position of the character's picture
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

    @Override
    public float getZ() {
        return 0;
    }

    public int getFileNameID() {
        return fileNameID;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
