package com.example.opengl_starwing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Square {
	private float vertices[] = {
			-0.5f, -0.5f, 0.0f,
			-0.5f,  0.5f, 0.0f,
			0.5f,  0.5f, 0.0f,
			0.5f, -0.5f, 0.0f};
	private short faces[] = { 0, 1, 2, 0, 2, 3 };

	// Our vertex buffer.
	private FloatBuffer vertexBuffer;

	// Our index buffer.
	private ShortBuffer indexBuffer;


	private boolean colorEnabled = false;

	public Square() {
		//Move the vertices list into a buffer
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		//Move the faces list into a buffer
		ByteBuffer ibb = ByteBuffer.allocateDirect(faces.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(faces);
		indexBuffer.position(0);
	}

	public void setColor(float r, float g, float b) {

	}

	public void setColor(float []colors) {
	}

	public void enableColor() { colorEnabled = true; }
	public void disableColor() { colorEnabled = false; }

	public void draw(GL10 gl) {
		//gl.glColor4f(r, g, b, 0.0f);

		// Enabled the vertices buffer for writing and to be used during rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, faces.length,
				GL10.GL_UNSIGNED_SHORT, indexBuffer);

		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
