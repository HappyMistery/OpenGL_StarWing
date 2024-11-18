package com.example.opengl_starwing;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

public class MainActivity extends Activity {
    private static final float MOVEMENT_SPEED = 0.025f;
    private static final float DIAGONAL_TOLERANCE = 100f; // Tolerance (in pixels) for diagonal movement
    private static final float SWIPE_THRESHOLD_DISTANCE = 200f; // Minimum distance for a swipe
    private static final long SWIPE_THRESHOLD_TIME = 200; // Maximum time (ms) to be considered a fast swipe
    private float initialX, initialY;
    private long initialTime;
    private MyOpenGLRenderer myGLRenderer;

    private Handler movementHandler;
    private boolean isMoving = false;
    private Runnable moveRunnable;
    private float moveX = 0, moveY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(myGLRenderer = new MyOpenGLRenderer(this));
        setContentView(view);

        // Initialize Handler and Runnable for continuous movement
        movementHandler = new Handler();
        moveRunnable = new Runnable() {
            @Override
            public void run() {
                if (isMoving) {
                    myGLRenderer.moveArwing(moveX, moveY);
                    movementHandler.postDelayed(this, 16); // Repeat roughly every 16ms (~60 FPS)
                }
            }
        };

        // Set an OnTouchListener to handle touch events
        view.setOnTouchListener((v, event) -> handleTouch(event));
    }

    private boolean handleTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Check if user touches to switch camera view
                if (x > myGLRenderer.width/8*7 && y < myGLRenderer.height/12) {
                    myGLRenderer.switchCameraView();
                }

                // Additional region checks can be added here
                // For example, you can create quadrants:
                // if (x < screenWidth / 2 && y < screenHeight / 2) { ... }
                // if (x >= screenWidth / 2 && y < screenHeight / 2) { ... }

                isMoving = false; // Stop any previous movement
                initialX = event.getX();
                initialY = event.getY();
                initialTime = System.currentTimeMillis();
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = x - initialX;
                float dy = y - initialY;

                // Determine whether movement should be treated as diagonal or straight
                if (Math.abs(dx) > DIAGONAL_TOLERANCE && Math.abs(dy) > DIAGONAL_TOLERANCE) {
                    // Diagonal movement detection
                    moveX = (dx > 0) ? MOVEMENT_SPEED : -MOVEMENT_SPEED;
                    moveY = (dy > 0) ? -MOVEMENT_SPEED : MOVEMENT_SPEED;
                } else if (Math.abs(dx) > Math.abs(dy)) {
                    // Prioritize horizontal movement if it is more significant than vertical
                    moveX = (dx > 0) ? MOVEMENT_SPEED : -MOVEMENT_SPEED;
                    moveY = 0; // No vertical movement
                } else if (Math.abs(dy) > Math.abs(dx)) {
                    // Prioritize vertical movement if it is more significant than horizontal
                    moveY = (dy > 0) ? -MOVEMENT_SPEED : MOVEMENT_SPEED;
                    moveX = 0; // No horizontal movement
                }

                if (!isMoving) {
                    isMoving = true;
                    movementHandler.post(moveRunnable); // Start continuous movement
                }

                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isMoving = false; // Stop movement when touch is released
                myGLRenderer.stopArwingAngle();

                // Detect if the touch was a quick swipe
                long elapsedTime = System.currentTimeMillis() - initialTime;
                float distanceX = x - initialX;

                if (elapsedTime < SWIPE_THRESHOLD_TIME && Math.abs(distanceX) > SWIPE_THRESHOLD_DISTANCE) {
                    // Determine the direction of the swipe
                    if (distanceX > 0) {
                        myGLRenderer.rotateArwing(-90); // Right swipe
                    } else {
                        myGLRenderer.rotateArwing(90); // Left swipe
                    }
                }

                return true;
        }
        return false;
    }
}
