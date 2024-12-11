package com.example.opengl_starwing;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private static final float MOVEMENT_SPEED = 0.05f;
    private static final float DIAGONAL_TOLERANCE = 100f; // Tolerance (in pixels) for diagonal movement
    private static final float SWIPE_THRESHOLD_DISTANCE = 200f; // Minimum distance for a swipe
    private static final long SWIPE_THRESHOLD_TIME = 200; // Maximum time (ms) to be considered a fast swipe
    private float initialX, initialY;
    private long initialTime;
    private MyOpenGLRenderer myGLRenderer;

    private Handler movementHandler;
    private boolean isMoving = false;
    private Runnable moveRunnable;
    private float moveX = 0;
    private float moveY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setImmersiveMode();
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(myGLRenderer = new MyOpenGLRenderer(this));
        setContentView(view);

        // Initialize Handler and Runnable for continuous movement
        movementHandler = new Handler();
        moveRunnable = new Runnable() {
            @Override
            public void run() {
                if (isMoving) {
                    myGLRenderer.moveArmwing(moveX, moveY);
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
                if (x > (float) myGLRenderer.width / 8 * 7 && y < (float) myGLRenderer.height / 12) {
                    myGLRenderer.switchCameraView();
                    return true;
                }
                // Check if the touch is in the bottom-right part of the screen for boosting
                if (x > (float) myGLRenderer.width / 4 * 3 && y > (float) myGLRenderer.height / 8 * 7) {
                    myGLRenderer.boost();
                    return true;
                }
                // If the touch doesn't fall under specific regions, it might be a tap to shoot
                initialX = x;
                initialY = y;
                initialTime = System.currentTimeMillis();
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = x - initialX;
                float dy = y - initialY;

                // Ignore minor movements below a threshold
                if (Math.abs(dx) < 10 && Math.abs(dy) < 10) {
                    return true; // Ignore small unintentional moves
                }

                // Determine movement direction
                if (Math.abs(dx) > DIAGONAL_TOLERANCE && Math.abs(dy) > DIAGONAL_TOLERANCE) {
                    moveX = (dx > 0) ? MOVEMENT_SPEED : -MOVEMENT_SPEED;
                    moveY = (dy > 0) ? -MOVEMENT_SPEED : MOVEMENT_SPEED;
                } else if (Math.abs(dx) > Math.abs(dy)) {
                    moveX = (dx > 0) ? MOVEMENT_SPEED : -MOVEMENT_SPEED;
                    moveY = 0;
                } else {
                    moveY = (dy > 0) ? -MOVEMENT_SPEED : MOVEMENT_SPEED;
                    moveX = 0;
                }

                if (!isMoving) {
                    isMoving = true;
                    movementHandler.post(moveRunnable); // Start continuous movement
                }

                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isMoving = false; // Stop movement
                myGLRenderer.stopArmwingAngle();

                long elapsedTime = System.currentTimeMillis() - initialTime;
                float distanceX = x - initialX;
                float distanceY = y - initialY;

                // Detect swipe
                if (elapsedTime < SWIPE_THRESHOLD_TIME && Math.abs(distanceX) > SWIPE_THRESHOLD_DISTANCE) {
                    if (distanceX > 0) {
                        myGLRenderer.rotateArmwing(-90); // Right swipe
                    } else {
                        myGLRenderer.rotateArmwing(90); // Left swipe
                    }
                } else if (Math.abs(distanceX) < 10 && Math.abs(distanceY) < 10) {
                    // Treat as a tap if no significant movement occurred
                    myGLRenderer.shootProjectile();
                }

                return true;
        }
        return false;
    }


    private void setImmersiveMode(){
        int newUiOptions = getWindow().getDecorView().getSystemUiVisibility();

        //Set the Flags for maximum Screen utilization
        newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }
}
