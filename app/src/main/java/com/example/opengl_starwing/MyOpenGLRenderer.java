package com.example.opengl_starwing;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class MyOpenGLRenderer implements Renderer {
	private TextureCube textureCube;  // 3D object representing a textured cube
	private Square squareText;  // Object representing a 2D square

	// zCam is the Z-axis camera position
	private float zCam = 0;

	// Angle is used for rotating the cube
	private int angle = 0;

	// Context allows access to Android resources like textures
	private Context context;

	// Width and height of the rendering area (screen)
	private int width;
	private int height;

	// Constructor that initializes the context
	public MyOpenGLRenderer(Context context){
		this.context = context;
	}

	// Getters and setters for zCam, width, and height
	public float getzCam() {
		return zCam;
	}

	public void setzCam(float zCam) {
		this.zCam = zCam;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// Called when the surface is created, this initializes the background and objects
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set background color (black with slight transparency)
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		// Initialize the 3D objects (textureCube and square)
		textureCube = new TextureCube(); // 3D cube object
		squareText = new Square(); // 2D square object

		// Load the texture for the cube using OpenGL
		textureCube.loadTexture(gl, context);
	}

	// Called each frame, this draws both the 3D scene and HUD
	@Override
	public void onDrawFrame(GL10 gl) {
		// Draw the 3D scene
		setPerspectiveProjection(gl); // Switch to perspective view

		// Clear the screen (color and depth buffer)
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Set camera position using gluLookAt (placing the camera at zCam + 5 units away)
		GLU.gluLookAt(gl, 0, 0, 5 + zCam, 0f, 0f, 0f, 0f, 1f, 0f);

		// Draw the cube in the scene
		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(2f, 2f, 2f); // Scale the cube by a factor of 2
		gl.glRotatef((angle) % 360, 1, 1, 0); // Rotate the cube around the X and Y axes
		textureCube.draw(gl); // Render the cube
		gl.glPopMatrix(); // Restore the transformation matrix

		// Switch to 2D mode (HUD)
		setOrthographicProjection(gl);

		/*
		// This is commented out; it would draw a 2D square on the HUD
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0);
		gl.glScalef(2f, 2f, 2f);
		squareText.draw(gl); // Draw the 2D square on the HUD
		gl.glPopMatrix();
		*/
	}

	// Set up perspective projection for 3D rendering
	private void setPerspectiveProjection(GL10 gl) {
		gl.glClearDepthf(1.0f); // Set the farthest depth for clearing
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enable depth testing to handle occlusion
		gl.glDepthFunc(GL10.GL_LEQUAL); // Set the depth testing function
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // Optimize perspective view
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable smooth color shading
		gl.glDisable(GL10.GL_DITHER); // Disable dithering for better performance
		gl.glDepthMask(true); // Enable writing to the depth buffer

		// Switch to projection matrix mode
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity(); // Reset the projection matrix

		// Apply perspective projection (like a 3D camera lens)
		GLU.gluPerspective(gl, 60, (float) width / height, 0.1f, 100.f);

		// Switch back to the model-view matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity(); // Reset model-view matrix
	}

	// Set up orthographic projection for 2D rendering (HUD)
	private void setOrthographicProjection(GL10 gl) {
		// Switch to projection matrix mode
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity(); // Reset projection matrix

		// Apply orthographic projection (no perspective, for HUD display)
		gl.glOrthof(-5, 5, -4, 4, -5, 5); // Define the clipping volume for 2D drawing
		gl.glDepthMask(false); // Disable writing to the depth buffer
		gl.glDisable(GL10.GL_DEPTH_TEST); // Disable depth testing for HUD

		// Switch back to the model-view matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity(); // Reset model-view matrix
	}

	// Called when the surface dimensions change, e.g., when the screen is resized
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.width = width; // Store the new width
		this.height = height; // Store the new height

		// Set the OpenGL viewport to match the new window dimensions
		gl.glViewport(0, 0, width, height);
	}
}
