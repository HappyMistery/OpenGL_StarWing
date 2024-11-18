package com.example.opengl_starwing;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import java.util.Random;

public class MyOpenGLRenderer implements Renderer {
	// Variables for the camera movement when Arwing moves
	private float camX = 0.0f;
	private float camY = 0.0f;
	private static final float CAMERA_SMOOTHNESS = 0.1f;

	// Variables for the Back Ground image and its lightning animation
	private BGImage bg;
    private int lightTime = 0;
	private int lightDuration = 5;
	private boolean lightOn = false;
	private final Random random = new Random();

	// Variables for the HUD
    private HUD hud;

	// Variables for the Arwing and its movement
	private Object3D arwing;
	private float arwingX = 0.0f;
	private float arwingY = 0.0f;
	private float arwingYaw = 0;  // Rotation angle around the Z-axis
	private float arwingRoll = 0f; // Rotation angle around the Y-axis
	private float arwingPitch = 0f; // Rotation angle around the X-axis
	private float targetArwingYaw = 0;
	private float targetArwingRoll = 0;
	private float targetArwingPitch = 0;
	private static final float ROTATION_SPEED = 0.1f;

	// Variables for the light in the scene
	private Light light;

	// Variables for the 3D scene
    private Scene scene;

	private final Context context;

	// Width and height of the rendering and movement area (screen)
	private int width;
	private int height;
	float halfWidth;
	float halfHeight = 4f;

	public MyOpenGLRenderer(Context context){
		this.context = context;
	}

	// Called when the surface is created, this initializes the background and objects
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set background color (black with slight transparency)
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		// Create the Background image and load its textures for the lightning animation
		bg = new BGImage();
		bg.loadTexture(gl, context, R.drawable.venom1);
		bg.loadTexture(gl, context, R.drawable.venom1lightning);

		// Create HUD
		hud = new HUD();

		// Load Arwing's model
		arwing = new Object3D(context, R.raw.nau);
		arwing.loadTexture(gl, context, R.drawable.paleta);

		// Enable lightning in the scene
		gl.glEnable(GL10.GL_LIGHTING);
		light = new Light(gl, GL10.GL_LIGHT0);
		light.setPosition(new float[]{0.0f, 0f, 1, 0.0f});
		light.setAmbientColor(new float[]{0.6f, 0.6f, 0.6f});
		light.setDiffuseColor(new float[]{1, 1, 1});

		// Create the 3D scene with its moving ground points
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
		setPerspectiveProjection(gl); // Switch to perspective view

		// Clear the screen (color and depth buffer)
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Set the general light's position
		light.setPosition(new float[]{0, 2f, -2, 0});

		// Smoothly move the camera towards the Arwing's position, all within a certain range
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
        int angle = 0;	// Angle is used for rotating the cube
        gl.glRotatef((angle) % 360, 1, 1, 0); // Rotate the image around the X and Y axes
		gl.glTranslatef(0f, 0.39f, -15.0f);	// Set the Back ground image to the Back ground of the scene

		// Display some lighting every once in a while (randomly)
        int randomNumber = random.nextInt(100) + 1;
        int lightningNum = 10;	// Number to display lightning
        if(randomNumber != lightningNum && !lightOn) {
			bg.drawImage(gl, 0);
		} else {
			bg.drawImage(gl, 1);
			lightOn = true;

			light.setPosition(new float[]{0.0f, 1f, 0, 0.0f});
			light.setAmbientColor(new float[]{0.4f, 0.4f, 0.6f});
		}
		gl.glPopMatrix(); // Restore the transformation matrix

		// Draw the 3D Scene objects
		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(0.06f, 0.06f, 0.06f);
		scene.draw(gl);
		gl.glPopMatrix(); // Restore the transformation matrix

		// Draw the Arwing
		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(1f, 1.0f, 1.0f); // Scale the Arwing
		gl.glTranslatef(arwingX, arwingY, 3.0f);
		gl.glRotatef((arwingYaw) % 360, 0, 0, 1); // Tilt the arwing
		gl.glRotatef(arwingRoll, 0, 1, 0); // Roll the arwing
		gl.glRotatef(arwingPitch, 1, 0, 0); // Pitch the arwing
		// Gradually rotate the Arwing towards the target angle
		if (Math.abs(targetArwingYaw - arwingYaw) > 0.01f) {
			arwingYaw += (targetArwingYaw - arwingYaw) * ROTATION_SPEED;
		}
		if (Math.abs(targetArwingRoll - arwingRoll) > 0.01f) {
			arwingRoll += (targetArwingRoll - arwingRoll) * ROTATION_SPEED;
		}
		if (Math.abs(targetArwingPitch - arwingPitch) > 0.01f) {
			arwingPitch += (targetArwingPitch - arwingPitch) * ROTATION_SPEED;
		}
		arwing.draw(gl);
		gl.glPopMatrix(); // Restore the transformation matrix

		// Restore Background after lightning
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
		float rotationAngle = 15;
		if (deltaX > 0) {
			targetArwingYaw = -rotationAngle; // Tilt right when moving right
			targetArwingRoll = -rotationAngle*2; // Rotate right on Y-axis
		} else if (deltaX < 0) {
			targetArwingYaw = rotationAngle; // Tilt left when moving left
			targetArwingRoll = rotationAngle*2; // Rotate left on Y-axis
		} else {
			targetArwingYaw = 0; // No tilt when not moving horizontally
			targetArwingRoll = 0; // No rotation on Y-axis when not moving horizontally
		}

		if (deltaY > 0) {
			targetArwingPitch = rotationAngle*3;
		} else if (deltaY < 0) {
			targetArwingPitch = -rotationAngle/2;
		} else {
			targetArwingPitch = 0;
		}
	}

	public void rotateArwing(int angle) {
		targetArwingYaw = arwingYaw + angle;
	}

	public void stopArwingAngle() {
		targetArwingYaw = 0;
		targetArwingRoll = 0;
		targetArwingPitch = 0;

	}
}
