package com.example.opengl_starwing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FontRenderer {
    private int textureId;
    private int numColumns = 31;
    private int numRows = 3;
    private float charWidth;
    private float charHeight;

    private FloatBuffer vertexBuffer; // Preallocated buffer for vertices
    private FloatBuffer texCoordBuffer; // Preallocated buffer for texture coordinates

    public FontRenderer(GL10 gl, Context context) {
        // Load texture and calculate character dimensions
        loadFontTexture(gl, context);
        charWidth = 1.0f / numColumns;
        charHeight = 1.0f / numRows;

        // Allocate reusable buffers for a quad (4 vertices)
        vertexBuffer = ByteBuffer.allocateDirect(4 * 2 * Float.BYTES) // 4 vertices * 2 coordinates * 4 bytes per float
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoordBuffer = ByteBuffer.allocateDirect(4 * 2 * Float.BYTES) // Same size for texture coordinates
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    private void loadFontTexture(GL10 gl, Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.font);

        int[] textureIds = new int[1];
        gl.glGenTextures(1, textureIds, 0);
        textureId = textureIds[0];

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    public void drawText(GL10 gl, String text, float x, float y, float charSize) {
        text = text.toUpperCase();
        int textLength = text.length();

        float spacingFactor = 0.5f;
        float heightScaleFactor = 1.5f;

        // Preallocate reusable arrays for vertices and texture coordinates
        float[] vertices = new float[8]; // 4 vertices * 2 coordinates (x, y)
        float[] texCoords = new float[8]; // 4 texture coordinates (u, v)

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glPushMatrix();
        gl.glRotatef(180, 1, 0, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glDisable(GL10.GL_LIGHTING);

        for (int i = 0; i < textLength; i++) {
            char c = text.charAt(i);
            int index;
            switch (c) {
                case 45: index = 26; break;
                case 46: index = 27; break;
                case 39: index = 28; break;
                case 63: index = 29; break;
                case 33: index = 30; break;
                default: index = c - 65;
            }

            if (index < 0 || index >= numColumns * numRows) continue;

            float u = (index % numColumns) * charWidth;
            float v = (index / numColumns) * charHeight;

            float x0 = x + i * charSize * spacingFactor;
            float x1 = x0 + charSize;
            float y0 = y;
            float y1 = y - charSize * heightScaleFactor;

            // Update vertices array
            vertices[0] = x0; vertices[1] = y0; // Top-left
            vertices[2] = x0; vertices[3] = y1; // Bottom-left
            vertices[4] = x1; vertices[5] = y1; // Bottom-right
            vertices[6] = x1; vertices[7] = y0; // Top-right

            // Update texture coordinates array
            texCoords[0] = u; texCoords[1] = v + charHeight; // Top-left
            texCoords[2] = u; texCoords[3] = v;             // Bottom-left
            texCoords[4] = u + charWidth; texCoords[5] = v; // Bottom-right
            texCoords[6] = u + charWidth; texCoords[7] = v + charHeight; // Top-right

            // Update buffers
            vertexBuffer.clear();
            vertexBuffer.put(vertices).position(0);

            texCoordBuffer.clear();
            texCoordBuffer.put(texCoords).position(0);

            // Draw the quad
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);

            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }

        gl.glPopMatrix();
        gl.glDisable(GL10.GL_BLEND);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }

}

