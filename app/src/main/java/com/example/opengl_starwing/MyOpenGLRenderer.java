package com.example.opengl_starwing;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyOpenGLRenderer implements Renderer {
	private BGImage bg;
	private BGImage lightBG;
	private int lightningNum = 10;
	private int lightTime = 0;
	private int lightDuration = 5;
	private boolean lightOn = false;
	private Random random = new Random();
	private int randomNumber;

	private HUD hud;

	private Object3D arwing;
	private float arwingX = 0.0f;
	private float arwingY = 0.0f;
	private int arwingAng = 0;

	private Light light;

	private int groundPointsXSpacing = 32;
	private int groundPointsYSpacing = 8;
	private int groundPointsPerRow = 21;
	private int groundPointsPerCol = 21;
	private Scene scene;

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

		bg = new BGImage();


		hud = new HUD();

		arwing = new Object3D(context, R.raw.nau);

		gl.glEnable(GL10.GL_LIGHTING);
		light = new Light(gl, GL10.GL_LIGHT0);
		light.setPosition(new float[]{0.0f, 0f, 1, 0.0f});
		light.setAmbientColor(new float[]{0.6f, 0.6f, 0.6f});
		light.setDiffuseColor(new float[]{1, 1, 1});

		scene = new Scene();
	}

	// Called each frame, this draws both the 3D scene and HUD
	@Override
	public void onDrawFrame(GL10 gl) {
		// Draw the 3D scene
		setPerspectiveProjection(gl); // Switch to perspective view

		// Clear the screen (color and depth buffer)
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		light.setPosition(new float[]{0, 2f, -2, 0});

		// Set camera position using gluLookAt (placing the camera at zCam + 5 units away)
		GLU.gluLookAt(gl, 0, 0, 5 + zCam, 0f, 0f, 0f, 0f, 1f, 0f);

		// Draw the background in the scene
		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(8f, 8f, 0.0f); // Scale the image
		gl.glRotatef((angle) % 360, 1, 1, 0); // Rotate the image around the X and Y axes
		gl.glTranslatef(0f, 0.39f, -15.0f);

		// Display some lighting every once in a while
		randomNumber = random.nextInt(100) + 1;
		if(randomNumber != lightningNum && !lightOn) {
			bg.loadTexture(gl, context, R.drawable.venom1);
			bg.draw(gl);
		} else {
			bg.loadTexture(gl, context, R.drawable.venom1lightning);
			bg.draw(gl);
			lightOn = true;

			light.setPosition(new float[]{0.0f, 1f, 0, 0.0f});
			light.setAmbientColor(new float[]{0.4f, 0.4f, 0.6f});
		}
		gl.glPopMatrix(); // Restore the transformation matrix

		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(0.06f, 0.06f, 0.06f);
		gl.glRotatef(15, 1, 0, 0);
		int gpZ = (groundPointsYSpacing * groundPointsPerCol)/2;
		int gpX = (groundPointsXSpacing * groundPointsPerRow)/2;
		gl.glTranslatef(-gpX, -10f, -gpZ);
		scene.draw(gl);
		gl.glPopMatrix(); // Restore the transformation matrix

		// Draw the Arwing
		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(1f, 1.0f, 1.0f); // Scale the Arwing
		gl.glTranslatef(arwingX, arwingY, 3.0f);
		gl.glRotatef((arwingAng) % 360, 0, 0, 1); // Rotate the arwing
		arwing.draw(gl);
		gl.glPopMatrix(); // Restore the transformation matrix

		if(lightOn) {
			lightTime++;
			if(lightTime == lightDuration) {
				lightTime = 0;
				lightDuration = random.nextInt(17) + 3;
				lightOn = false;

				light.setPosition(new float[]{0.0f, 0f, 1, 0.0f});
				light.setAmbientColor(new float[]{0.6f, 0.6f, 0.6f});
			}
		}

		// Switch to 2D mode (HUD) and draw the HUD
		setOrthographicProjection(gl);
		hud.draw(gl);
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

	public void moveArwing(float deltaX, float deltaY) {
		arwingX += deltaX;
		arwingY += deltaY;
	}

	public void rotateArwing(int angle) {
		arwingAng += angle;
	}
}
