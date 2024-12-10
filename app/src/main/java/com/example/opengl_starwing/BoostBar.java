package com.example.opengl_starwing;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class BoostBar implements HUDDrawable {
    private final float x, y;
    private final FloatBuffer topBorderBuffer;
    private final FloatBuffer bottomBorderBuffer;
    private final FloatBuffer leftBorderBuffer;
    private final FloatBuffer rightBorderBuffer;
    private float boostPercentage;    // Health percentage (0 to 1)

    public BoostBar(float x, float y) {
        this.x = x;
        this.y = y;
        this.boostPercentage = 1.0f; // Full boost by default

        // Precompute static buffers for borders
        float width = 1.75f, height = 0.3f, borderHeight = 0.05f, borderWidth = 0.05f;
        this.topBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x, y + height, 0, x + width, y + height, 0, x, y + height + borderHeight, 0, x + width, y + height + borderHeight, 0
        });

        this.bottomBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x, y - borderHeight, 0, x + width, y - borderHeight, 0, x, y, 0, x + width, y, 0
        });

        this.leftBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x, y, 0, x + borderWidth, y, 0, x, y + height, 0, x + borderWidth, y + height, 0
        });

        this.rightBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x + width - borderWidth, y, 0, x + width, y, 0, x + width - borderWidth, y + height, 0, x + width, y + height, 0
        });
    }

    // Method to set the boost percentage (0 to 1)
    public void setBoostPercentage(float boostPercentage) {
        if(this.boostPercentage > 0) {
            this.boostPercentage = Math.min(1f, boostPercentage);
        }
    }
    // Method to get the boost percentage (0 to 1)
    public float getBoostPercentage() {
        return this.boostPercentage;
    }

    // Method to draw the health bar
    @Override
    public void draw(GL10 gl) {
        float width = 1.75f, height = 0.3f;

        // Draw the boost portion (dynamic width)
        float[] boostVertices = {
                x, y, 0, x + width * boostPercentage, y, 0, x, y + height, 0, x + width * boostPercentage, y + height, 0
        };
        FloatBuffer boostBuffer = GLUtils.createFloatBuffer(boostVertices);
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f); // Blue color
        GLUtils.drawRectangle(gl, boostBuffer);

        // Draw borders
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White color
        GLUtils.drawRectangle(gl, topBorderBuffer);
        GLUtils.drawRectangle(gl, bottomBorderBuffer);
        GLUtils.drawRectangle(gl, leftBorderBuffer);
        GLUtils.drawRectangle(gl, rightBorderBuffer);
    }

    public void useBoost() {
        boostPercentage = Math.min(boostPercentage - 0.005f, 1.0f);
    }

    public void restoreBoost() {
        boostPercentage = Math.min(boostPercentage + 0.001f, 1.0f);
    }
}
