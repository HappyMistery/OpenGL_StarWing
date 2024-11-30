package com.example.opengl_starwing;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class MyOpenGLRenderer implements Renderer {
	private Camera camera;
	private BackGround bg;
    private HUD hud;
	private Light light;
	private Light sceneLight;
	private Scene scene;
	private Arwing arwing;

	private final Context context;

	// Width and height of the rendering and movement area (screen)
	public int width;
	public int height;
	public float halfWidth = 5f;
	public float halfHeight = 4f;

	public MyOpenGLRenderer(Context context){
		this.context = context;
	}

	// Called when the surface is created, this initializes the background and objects
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set background color (black with slight transparency)
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		camera = new Camera();
		bg = new BackGround(gl, context);
		hud = new HUD(gl, context, halfHeight, halfWidth);
		arwing = new Arwing(gl, context, camera.getCamZ(), hud);

		// Enable lightning in the scene
		gl.glEnable(GL10.GL_LIGHTING);
		light = new Light(gl, GL10.GL_LIGHT0);
		light.setPosition(new float[]{0.0f, 1f, 1, 0.0f});
		light.setAmbientColor(new float[]{1f, 1f, 1f});
		light.setDiffuseColor(new float[]{1, 1, 1});

		// Initialize the second light for scene illumination
		sceneLight = new Light(gl, GL10.GL_LIGHT1);
		sceneLight.setPosition(new float[]{5.0f, 10.0f, 5.0f, 1.0f}); // Positioned above the buildings
		sceneLight.setAmbientColor(new float[]{0.2f, 0.2f, 0.2f, 1.0f}); // Soft ambient light
		sceneLight.setDiffuseColor(new float[]{0.2f, 0.2f, 0.15f, 1.0f}); // Brighter diffuse ligh
		gl.glEnable(GL10.GL_LIGHT1); // Enable the second light

		// Create the 3D scene with its moving ground points
		int groundPointsYSpacing = 12;
		int groundPointsPerCol = 84;
		int gpZ = (groundPointsYSpacing * groundPointsPerCol)/2;
		int groundPointsXSpacing = 42;
		int groundPointsPerRow = 19;
		int gpX = (groundPointsXSpacing * groundPointsPerRow)/2;
		scene = new Scene(gl, context, gpX,-10f, gpZ, arwing);
	}

	// Called each frame, this draws both the 3D scene and HUD
	@Override
	public void onDrawFrame(GL10 gl) {
		setPerspectiveProjection(gl); // Switch to perspective view

		// Clear the screen (color and depth buffer)
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Set the general light's position
		light.setPosition(new float[]{0, 2f, -2, 0});

        camera.setCameraView(gl, arwing, halfWidth, halfHeight);
		bg.draw(gl, light);
		drawScene(gl);
		arwing.draw(gl, halfHeight);
		bg.restoreBG(light);	// Disable Lightning after a certain time

		// Switch to 2D mode (HUD) and draw the HUD
		setOrthographicProjection(gl);
		hud.draw(gl, arwing, scene);
	}

	private void drawScene(GL10 gl) {
		// Draw the 3D Scene objects
		gl.glPushMatrix(); // Save the current transformation matrix
		gl.glScalef(0.06f, 0.06f, 0.06f);
		scene.draw(gl);
		gl.glPopMatrix(); // Restore the transformation matrix
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
		GLU.gluPerspective(gl, 60, (float) width / height, 0.1f, 200.f);

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

		halfWidth = (float) (width / height) * 2;
		halfHeight= (float) (width / height) / 2;

		// Set the OpenGL viewport to match the new window dimensions
		gl.glViewport(0, 0, width, height);
	}

	public void moveArwing(float deltaX, float deltaY) {
		arwing.move(deltaX, deltaY, halfWidth, halfHeight);
	}

	public void stopArwingAngle(){
		arwing.resetAngle();
	}

	public void rotateArwing(int angle){
		arwing.rotate(angle);
	}

	public void switchCameraView() {
		if(hud.gameOver()) {
			restartApp();
		}
		camera.switchPOV();
	}

	public void boost() {
		hud.boost(arwing, scene);
	}

	public void restartApp() {
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

		System.exit(0);	// Exit the process
	}
}
