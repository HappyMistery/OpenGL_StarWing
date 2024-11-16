package com.example.opengl_starwing;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

public class MainActivity extends Activity {
    private static final float MOVEMENT_SPEED = 0.025f;
    private static final float DIAGONAL_TOLERANCE = 100f; // Tolerance (in pixels) for diagonal movement
    private float initialX, initialY;
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
                isMoving = false; // Stop any previous movement
                initialX = event.getX();
                initialY = event.getY();
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = x - initialX;
                float dy = y - initialY;

                // Determine if movement should be treated as diagonal
                if (Math.abs(dx - dy) <= DIAGONAL_TOLERANCE) {
                    // Move diagonally
                    moveX = (dx > 0) ? MOVEMENT_SPEED : -MOVEMENT_SPEED;
                    moveY = (dy > 0) ? -MOVEMENT_SPEED : MOVEMENT_SPEED;
                } else if (Math.abs(dx) > Math.abs(dy)) {
                    // Prioritize horizontal movement
                    moveX = (dx > 0) ? MOVEMENT_SPEED : -MOVEMENT_SPEED;
                    moveY = 0; // No vertical movement
                } else {
                    // Prioritize vertical movement
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
                return true;
        }
        return false;
    }
}
