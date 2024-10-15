package com.example.opengl_starwing;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import org.w3c.dom.Text;

public class MyOpenGLRenderer implements Renderer {

	private Square square;
	private int angle = 0;

	private Context context;

	public MyOpenGLRenderer(Context context){
		this.context = context;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Image Background color
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		//Create the objects
		square = new Square();
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gl.glLoadIdentity();	

		gl.glTranslatef(0.0f, 0.0f, -5.0f);

		// Green Square
		gl.glPushMatrix();
		int midAngle = angle % 200;
		if (midAngle > 100)
			midAngle = 200 - midAngle;
		gl.glTranslatef(0.0f, 0.0f, midAngle * -0.1f);
		gl.glColor4f(0,1,0,0);
		square.draw(gl);
		gl.glPopMatrix();

		gl.glRotatef(angle, angle, angle, 1.0f);

		// Red Square
		gl.glPushMatrix();
		gl.glRotatef(1, angle, angle, angle);
		gl.glTranslatef(-1.0f, 0.0f, 0.0f);
		gl.glColor4f(1,0,0,0);
		square.draw(gl);
		gl.glPopMatrix();

		// Blue Square
		gl.glPushMatrix();
		gl.glRotatef(1, angle, angle, angle);
		gl.glTranslatef( 1.0f, 0.0f, 0.0f);
		gl.glColor4f(0,0,1,0);
		square.draw(gl);
		gl.glPopMatrix();

		angle += 5f;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Define the Viewport
		gl.glViewport(0, 0, width, height);
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 60.0f, (float) width / (float) height, 0.1f, 1000.0f);
		
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}

}
