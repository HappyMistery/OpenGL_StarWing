package com.example.opengl_starwing;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import java.util.Random;

public class MyOpenGLRenderer implements Renderer {
	private float camX = 0.0f;
	private float camY = 0.0f;
	private static final float CAMERA_SMOOTHNESS = 0.1f;

	private BGImage bg;
    private int lightTime = 0;
	private int lightDuration = 5;
	private boolean lightOn = false;
	private final Random random = new Random();

    private HUD hud;

	private Object3D arwing;
	private float arwingX = 0.0f;
	private float arwingY = 0.0f;
	private float arwingAng = 0;
	private float targetArwingAng = 0; // New field for the target angle
	private static final float ROTATION_SPEED = 0.1f; // Adjust the speed of rotation

	private Light light;

    private Scene scene;

    // Context allows access to Android resources like textures
	private final Context context;

	// Width and height of the rendering area (screen)
	private int width;
	private int height;
	float halfWidth;
	float halfHeight = 4f;

	// Constructor that initializes the context
	public MyOpenGLRenderer(Context context){
		this.context = context;
	}

	/*
	//Getters and setters for zCam, width, and height
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
	*/

	// Called when the surface is created, this initializes the background and objects
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set background color (black with slight transparency)
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		bg = new BGImage();
		bg.loadTexture(gl, context, R.drawable.venom1);
		bg.loadTexture(gl, context, R.drawable.venom1lightning);



		hud = new HUD();

		arwing = new Object3D(context, R.raw.nau);

		gl.glEnable(GL10.GL_LIGHTING);
		light = new Light(gl, GL10.GL_LIGHT0);
		light.setPosition(new float[]{0.0f, 0f, 1, 0.0f});
		light.setAmbientColor(new float[]{0.6f, 0.6f, 0.6f});
		light.setDiffuseColor(new float[]{1, 1, 1});

		int groundPointsYSpacing = 8;
		int groundPointsPerCol = 21;
		int gpZ = (groundPointsYSpacing * groundPointsPerCol)/2;
		int groundPointsXSpacing = 32;
		int groundPointsPerRow = 11;
		int gpX = (groundPointsXSpacing * groundPointsPerRow)/2;
		scene = new Scene(gpX,-10f, gpZ);
	}

	// Called each frame, this draws both the 3D scene and HUD
	@Override
	public void onDrawFrame(GL10 gl) {
		// Draw the 3D scene
		setPerspectiveProjection(gl); // Switch to perspective view

		// Clear the screen (color and depth buffer)
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		light.setPosition(new float[]{0, 2f, -2, 0});

		// Smoothly move the camera towards the Arwing's position
		camX += ((arwingX - camX) * CAMERA_SMOOTHNESS)/2;
		camY += ((arwingY - camY) * CAMERA_SMOOTHNESS)/2;

		camX = Math.max(-halfWidth/2, Math.min(camX, halfWidth/2));
		camY = Math.max(-halfHeight/2, Math.min(camY, halfHeight/2));

		// Set camera position using gluLookAt (placing the camera at zCam + 5 units away)
        // zCam is the Z-axis camera position
        float zCam = 0;
        GLU.gluLookAt(gl, camX, camY, 5 + zCam, camX, camY, 0f, 0f, 1f, 0f);

		// Draw the background in the scene
		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(8f, 8f, 0.0f); // Scale the image
        // Angle is used for rotating the cube
        int angle = 0;
        gl.glRotatef((angle) % 360, 1, 1, 0); // Rotate the image around the X and Y axes
		gl.glTranslatef(0f, 0.39f, -15.0f);

		// Display some lighting every once in a while
        int randomNumber = random.nextInt(100) + 1;
        int lightningNum = 10;
        if(randomNumber != lightningNum && !lightOn) {
			bg.drawImage(gl, 0);
		} else {
			bg.drawImage(gl, 1);
			lightOn = true;

			light.setPosition(new float[]{0.0f, 1f, 0, 0.0f});
			light.setAmbientColor(new float[]{0.4f, 0.4f, 0.6f});
		}
		gl.glPopMatrix(); // Restore the transformation matrix

		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(0.06f, 0.06f, 0.06f);
		//gl.glRotatef(25, 1, 0, 0);
		scene.draw(gl);
		gl.glPopMatrix(); // Restore the transformation matrix

		// Draw the Arwing
		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(1f, 1.0f, 1.0f); // Scale the Arwing
		gl.glTranslatef(arwingX, arwingY, 3.0f);
		gl.glRotatef((arwingAng) % 360, 0, 0, 1); // Rotate the arwing
		// Gradually rotate the Arwing towards the target angle
		if (Math.abs(targetArwingAng - arwingAng) > 0.01f) {
			arwingAng += (targetArwingAng - arwingAng) * ROTATION_SPEED;
		}
		arwing.draw(gl);
		gl.glPopMatrix(); // Restore the transformation matrix

		if(lightOn) {
			lightTime++;
			if(lightTime == lightDuration) {
				lightTime = 0;
				lightDuration = random.nextInt(13) + 3;
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

		halfWidth = (float) width / height;
		halfHeight= (float) (width / height) / 2;

		// Set the OpenGL viewport to match the new window dimensions
		gl.glViewport(0, 0, width, height);
	}

	public void moveArwing(float deltaX, float deltaY) {
		// Clamp the Arwing's position to keep it within the viewport
		arwingX = Math.max(-halfWidth, Math.min(arwingX+=deltaX, halfWidth));
		arwingY = Math.max(-halfHeight, Math.min(arwingY+=deltaY, halfHeight));
	}

	public void rotateArwing(int angle) {
		targetArwingAng = arwingAng + angle;
	}
}
