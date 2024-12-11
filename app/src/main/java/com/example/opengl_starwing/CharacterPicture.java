package com.example.opengl_starwing;

import android.content.Context;
import javax.microedition.khronos.opengles.GL10;
import java.nio.FloatBuffer;

public class CharacterPicture implements HUDDrawable {
    private final BGImage character = new BGImage();
    private final FloatBuffer topBorderBuffer, bottomBorderBuffer, leftBorderBuffer, rightBorderBuffer;
    private float x, y;
    private final float width = 1.4f;
    private final float height = 1.3f;
    private final float borderHeight = 0.05f;
    private final float borderWidth = 0.05f;
    private final int fileNameID;

    public CharacterPicture(GL10 gl, Context context, float x, float y, int fileNameID) {
        character.loadTexture(gl, context, fileNameID);
        this.fileNameID = fileNameID;
        this.x = x;
        this.y = y;

        // Precompute static border buffers optimization)
        topBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x - borderHeight * 6, y + height, 0,
                x - borderHeight * 6 + width, y + height, 0,
                x - borderHeight * 6, y + height + borderHeight, 0,
                x - borderHeight * 6 + width, y + height + borderHeight, 0
        });

        bottomBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x - borderHeight * 6, y - borderHeight, 0,
                x - borderHeight * 6 + width, y - borderHeight, 0,
                x - borderHeight * 6, y, 0,
                x - borderHeight * 6 + width, y, 0
        });

        leftBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x - borderHeight * 6, y, 0,
                x - borderHeight * 6 + borderWidth, y, 0,
                x - borderHeight * 6, y + height, 0,
                x - borderHeight * 6 + borderWidth, y + height, 0
        });

        rightBorderBuffer = GLUtils.createFloatBuffer(new float[]{
                x + width - borderWidth * 7, y, 0,
                x + width - borderWidth * 7 + borderWidth, y, 0,
                x + width - borderWidth * 7, y + height, 0,
                x + width - borderWidth * 7 + borderWidth, y + height, 0
        });
    }

    @Override
    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(0.65f, 0.65f, 0.0f); // Scale the image
        gl.glTranslatef(x - 0.65f, y - 1f, 0);
        character.draw(gl);
        gl.glPopMatrix();

        // Draw borders
        gl.glDisable(GL10.GL_LIGHTING);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White color
        GLUtils.drawRectangle(gl, topBorderBuffer);
        GLUtils.drawRectangle(gl, bottomBorderBuffer);
        GLUtils.drawRectangle(gl, leftBorderBuffer);
        GLUtils.drawRectangle(gl, rightBorderBuffer);

        gl.glEnable(GL10.GL_LIGHTING);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;

        // Update buffers with new position
        GLUtils.updateBuffer(topBorderBuffer, new float[]{
                x - borderHeight * 6, y + height, 0,
                x - borderHeight * 6 + width, y + height, 0,
                x - borderHeight * 6, y + height + borderHeight, 0,
                x - borderHeight * 6 + width, y + height + borderHeight, 0
        });

        GLUtils.updateBuffer(bottomBorderBuffer, new float[]{
                x - borderHeight * 6, y - borderHeight, 0,
                x - borderHeight * 6 + width, y - borderHeight, 0,
                x - borderHeight * 6, y, 0,
                x - borderHeight * 6 + width, y, 0
        });

        GLUtils.updateBuffer(leftBorderBuffer, new float[]{
                x - borderHeight * 6, y, 0,
                x - borderHeight * 6 + borderWidth, y, 0,
                x - borderHeight * 6, y + height, 0,
                x - borderHeight * 6 + borderWidth, y + height, 0
        });

        GLUtils.updateBuffer(rightBorderBuffer, new float[]{
                x + width - borderWidth * 7, y, 0,
                x + width - borderWidth * 7 + borderWidth, y, 0,
                x + width - borderWidth * 7, y + height, 0,
                x + width - borderWidth * 7 + borderWidth, y + height, 0
        });
    }
}
