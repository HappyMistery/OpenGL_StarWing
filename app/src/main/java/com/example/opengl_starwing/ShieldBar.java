package com.example.opengl_starwing;

import android.content.Context;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class ShieldBar implements HUDDrawable {
    private final float x, y;
    private final FloatBuffer topBorderBuffer;
    private final FloatBuffer bottomBorderBuffer;
    private final FloatBuffer leftBorderBuffer;
    private final FloatBuffer rightBorderBuffer;
    private float shieldPercentage;    // Health percentage (0 to 1)

    public ShieldBar(float x, float y) {
        this.x = x;
        this.y = y;
        this.shieldPercentage = 1.0f;

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

    // Method to set the shield percentage (0 to 1)
    public void setShieldPercentage(float shieldPercentage) {
        if(this.shieldPercentage > 0) {
            this.shieldPercentage = Math.min(1f, shieldPercentage);
        }
    }
    // Method to get the shield percentage (0 to 1)
    public float getShieldPercentage() {
        return this.shieldPercentage;
    }

    // Method to draw the shield bar
    @Override
    public void draw(GL10 gl) {
        float width = 1.75f, height = 0.3f;

        // Draw the shield portion (dynamic width)
        float[] shieldVertices = {
                x, y, 0, x + width * shieldPercentage, y, 0, x, y + height, 0, x + width * shieldPercentage, y + height, 0
        };
        FloatBuffer shieldBuffer = GLUtils.createFloatBuffer(shieldVertices);
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f); // Red color
        GLUtils.drawRectangle(gl, shieldBuffer);

        // Draw borders
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GLUtils.drawRectangle(gl, topBorderBuffer);
        GLUtils.drawRectangle(gl, bottomBorderBuffer);
        GLUtils.drawRectangle(gl, leftBorderBuffer);
        GLUtils.drawRectangle(gl, rightBorderBuffer);
    }

    public void regainShield() {
        shieldPercentage = Math.min(shieldPercentage + 0.0002f, 1.0f);
    }
}
