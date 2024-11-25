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
    private int numColumns = 31; // Number of columns in the font texture
    private int numRows = 3;    // Number of rows in the font texture
    private float charWidth;    // Width of each character in texture coordinates
    private float charHeight;   // Height of each character in texture coordinates

    public FontRenderer(GL10 gl, Context context) {
        // Load texture and calculate character dimensions
        loadFontTexture(gl, context);
        charWidth = 1.0f / numColumns;
        charHeight = 1.0f / numRows;
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
        // Convert the input string to uppercase
        text = text.toUpperCase();
        float[] vertices = new float[12]; // Quad vertices (2 triangles)
        float[] texCoords = new float[8]; // Texture coordinates
        int textLength = text.length();

        // Spacing and height scale factors
        float spacingFactor = 0.5f;
        float heightScaleFactor = 1.5f;

        // Enable transparency
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
                case 45:
                    index = 26;
                    break;
                case 46:
                    index = 27;
                    break;
                case 39:
                    index = 28;
                    break;
                case 63:
                    index = 29;
                    break;
                case 33:
                    index = 30;
                    break;
                default:
                    index = c - 65; // Adjust offset to start from ASCII 'A' (65)
            }

            // If the character is outside the range of the texture, skip rendering
            if (index < 0 || index >= numColumns * numRows) {
                continue;
            }

            // Calculate texture coordinates
            float u = (index % numColumns) * charWidth;
            float v = (index / numColumns) * charHeight;

            // Quad vertices
            float x0 = x + i * charSize * spacingFactor;
            float x1 = x0 + charSize;
            float y0 = y;
            float y1 = y - charSize * heightScaleFactor;

            vertices = new float[]{
                    x0, y0,  // Top-left
                    x0, y1,  // Bottom-left
                    x1, y1,  // Bottom-right
                    x1, y0   // Top-right
            };

            // Texture coordinates
            texCoords = new float[]{
                    u, v + charHeight,   // Top-left
                    u, v,               // Bottom-left
                    u + charWidth, v,   // Bottom-right
                    u + charWidth, v + charHeight // Top-right
            };

            // Enable vertex arrays
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            // Pass vertex and texture coordinate arrays
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, createFloatBuffer(vertices));
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, createFloatBuffer(texCoords));

            // Draw the quad
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

            // Disable arrays
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }

        gl.glPopMatrix();
        gl.glDisable(GL10.GL_BLEND); // Disable transparency
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }

    // Helper method to create a FloatBuffer
    private java.nio.FloatBuffer createFloatBuffer(float[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length * 4); // 4 bytes per float
        buffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = buffer.asFloatBuffer();
        floatBuffer.put(data);
        floatBuffer.position(0);
        return floatBuffer;
    }


}
