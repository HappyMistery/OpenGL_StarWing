package com.example.opengl_starwing;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class ShieldBar implements HUDDrawable {
    private final float x, y;
    private float width = 1.75f;
    private float height = 0.3f;
    private float shieldPercentage;    // Health percentage (0 to 1)
    private final FloatBuffer topBorderBuffer, bottomBorderBuffer, leftBorderBuffer, rightBorderBuffer;

    public ShieldBar(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.shieldPercentage = 1.0f; // Full health by default

        // Precompute static buffers for borders (optimizations)
        if(width != 0) {
            this.width = width;
        }
        if(height != 0) {
            this.height = height;
        }
        width = this.width;
        height = this.height;
        float borderHeight = 0.05f;
        float borderWidth = 0.05f;
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

    public void setShieldPercentage(float shieldPercentage) {
        if(this.shieldPercentage > 0) {
            this.shieldPercentage = Math.min(1f, shieldPercentage);
        }
    }

    public float getShieldPercentage() {
        return this.shieldPercentage;
    }

    @Override
    public void draw(GL10 gl) {

        // Draw the shield portion (dynamic width)
        float[] shieldVertices = {
                x, y, 0, x + width * shieldPercentage, y, 0, x, y + height, 0, x + width * shieldPercentage, y + height, 0
        };
        FloatBuffer shieldBuffer = GLUtils.createFloatBuffer(shieldVertices);
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f); // Red color
        GLUtils.drawRectangle(gl, shieldBuffer);

        // Draw borders using precomputed buffers
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White color
        GLUtils.drawRectangle(gl, topBorderBuffer);
        GLUtils.drawRectangle(gl, bottomBorderBuffer);
        GLUtils.drawRectangle(gl, leftBorderBuffer);
        GLUtils.drawRectangle(gl, rightBorderBuffer);
    }

    public void regainShield() {
        shieldPercentage = Math.min(shieldPercentage + 0.0002f, 1.0f);
    }
}
