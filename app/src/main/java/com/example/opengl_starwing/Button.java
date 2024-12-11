package com.example.opengl_starwing;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Button implements HUDDrawable {
    private final FloatBuffer mainRectangleBuffer;
    private final FloatBuffer bottomBorderBuffer;
    private final FloatBuffer leftBorderBuffer;

    public Button(float x, float y, float width, float height) {
        float borderWidth = 0.05f;
        float borderHeight = 0.05f;

        // Precompute vertex buffers (optimization)
        this.mainRectangleBuffer = GLUtils.createFloatBuffer(new float[]{
                x, y, 0, x + width, y, 0, x, y + height, 0, x + width, y + height, 0
        });

        this.bottomBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x, y - borderHeight, 0, x + width, y - borderHeight, 0, x, y, 0, x + width, y, 0
        });

        this.leftBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x, y, 0, x + borderWidth, y, 0, x, y + height, 0, x + borderWidth, y + height, 0
        });
    }

    @Override
    public void draw(GL10 gl) {
        gl.glColor4f(0, 0, 0, 1.0f); // Black color
        GLUtils.drawRectangle(gl, mainRectangleBuffer);

        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White color
        GLUtils.drawRectangle(gl, bottomBorderBuffer);
        GLUtils.drawRectangle(gl, leftBorderBuffer);
    }
}

