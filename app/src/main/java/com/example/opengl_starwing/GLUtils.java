package com.example.opengl_starwing;

import javax.microedition.khronos.opengles.GL10;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GLUtils {
    // Use a precomputed buffer to draw a rectangle
    public static void drawRectangle(GL10 gl, FloatBuffer buffer) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    // Helper method to create a FloatBuffer from a float array
    public static FloatBuffer createFloatBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    public static void updateBuffer(FloatBuffer buffer, float[] array) {
        buffer.clear(); // Clear the buffer for new data
        buffer.put(array); // Add the new data
        buffer.position(0); // Reset position to the start
    }
}
