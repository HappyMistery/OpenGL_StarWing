package com.example.opengl_starwing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;



/*
 * A cube with texture.
 * Define the vertices for only one representative face.
 * Render the cube by translating and rotating the face.
 */
public class BGImage implements Drawable{
    private FloatBuffer vertexBuffer; // Buffer for vertex-array
    private FloatBuffer texBuffer;    // Buffer for texture-coords-array (NEW)
    private int texIndex = 0;
    private int texID;

    private float[] vertices = { // Vertices for a face
            -1.0f, -1.0f, 0.0f,  // 0. left-bottom-front
            1.0f, -1.0f, 0.0f,  // 1. right-bottom-front
            -1.0f,  1.0f, 0.0f,  // 2. left-top-front
            1.0f,  1.0f, 0.0f   // 3. right-top-front
    };

    float[] texCoords = { // Texture coords for the above face (NEW)
            0.0f, 1.0f,  // A. left-bottom (NEW)
            1.0f, 1.0f,  // B. right-bottom (NEW)
            0.0f, 0.0f,  // C. left-top (NEW)
            1.0f, 0.0f   // D. right-top (NEW)
    };
    int[] textureIDs = new int[3];   // Array for 1 texture-ID (NEW)

    // Constructor - Set up the buffers
    public BGImage() {
        // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        vertexBuffer.put(vertices);         // Copy data into buffer
        vertexBuffer.position(0);           // Rewind

        // Setup texture-coords-array buffer, in float. An float has 4 bytes (NEW)
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        texBuffer = tbb.asFloatBuffer();
        texBuffer.put(texCoords);
        texBuffer.position(0);
    }

    @Override
    public float getZ() {
        return 0;
    }

    // Draw the shape
    @Override
    public void draw(GL10 gl) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[texID]);
        gl.glDisable(GL10.GL_LIGHTING);
        gl.glColor4f(1,1,1,1);

        gl.glFrontFace(GL10.GL_CCW);    // Front face in counter-clockwise orientation
        gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
        gl.glCullFace(GL10.GL_BACK);    // Cull the back face (don't display)
        gl.glEnable(GL10.GL_TEXTURE_2D);  // Enable texture

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Enable texture-coords-array
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer); // Define texture-coords buffer

        // front
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, 50.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();


        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Disable texture-coords-array (NEW)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDisable(GL10.GL_TEXTURE_2D);  // Enable texture (NEW));
        gl.glEnable(GL10.GL_LIGHTING);
    }

    // Load an image into GL texture
    public void loadTexture(GL10 gl, Context context, int fileNameID) {
        gl.glGenTextures(1, textureIDs, texIndex); // Generate texture-ID array
        texID = texIndex;

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[texID]);   // Bind to texture ID
        // Set up texture filters
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

        Bitmap bitmap;
        // Construct a bitmap for the specified file
        bitmap = BitmapFactory.decodeResource(context.getResources(), fileNameID);
        // Build Texture from loaded bitmap for the currently-bind texture ID
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        texIndex++;
    }
}