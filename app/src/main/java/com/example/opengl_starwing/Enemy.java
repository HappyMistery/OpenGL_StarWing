package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Enemy implements SceneDrawable{
    // Variables for the armwing and its movement
    private final Object3D enemy;
    private final Object3D enemyShadow;
    private final float scaleFactor = 30f;
    private float x, y, z, sceneZ;
    private float targetX, targetY;
    private float halfHeight;
    private float health = 1;
    private float VELOCITY_Z = -1f;
    private float MOVEMENT_VELOCITY = 0.05f; // Speed of horizontal movement

    private float enemyYaw = 0f;
    private float enemyRoll = 0f;
    private float enemyPitch = 0f;
    private float targetEnemyYaw = 0f;
    private float targetEnemyRoll = 0f;
    private float targetEnemyPitch = 0f;
    private float rotationProgress = 0f;
    private static final float ROTATION_SPEED = 0.05f;

    private boolean transitioning = true; // Flag to indicate if the enemy is transitioning
    private float transitionProgress = 0f; // Progress of the transition (0 to 1)
    private static final float TRANSITION_SPEED = 0.01f; // Speed of the transition
    private float startX, startY; // Starting position for the transition

    private Scene scene = null;

    public Enemy(GL10 gl, Context context, float x, float y, float z) {
        // Load enemy's model
        enemy = new Object3D(context, R.raw.enemy1);
        enemyShadow = new Object3D(context, R.raw.enemy1);
        enemy.loadTexture(gl, context, R.drawable.paleta1);

        this.x = x;
        this.y = y;
        this.z = z;
        setNewTargetX();
        setNewTargetY();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setHalfHeight(float halfHeight) {
        this.halfHeight = halfHeight;
    }

    public void initialize(float x, float y, float z, float offset) {
        this.x = x;
        this.y = y;
        this.z = z;
        sceneZ = offset;
        health = 100;

        // Set the transition start position (e.g., outside the screen boundaries)
        Random random = new Random();
        startX = random.nextBoolean() ? -20f : 40f; // Start from either side of the screen
        startY = (random.nextFloat() * 1.2f) - 0.2f; // Random Y within the range
        transitioning = true;
        transitionProgress = 0f;

        setNewTargetX();
        setNewTargetY();
    }

    // Randomize a new target x within the range
    private void setNewTargetX() {
        Random random = new Random();
        targetX =  (random.nextFloat() * 4.7f) + 11;
    }

    // Randomize a new target y within the range
    private void setNewTargetY() {
        Random random = new Random();
        targetY = (random.nextFloat() * 1.2f) - 0.2f; // Random Y target within a predefined range
    }

    // Smooth transition for rotation
    private void updateRotation() {
        // Slowly increase the progress until it reaches 1
        rotationProgress = Math.min(1f, rotationProgress + ROTATION_SPEED);
        // Smooth easing function to make the transition smoother
        float easedProgress = (float) Math.sin(rotationProgress * Math.PI / 2); // Sinusoidal easing

        // Interpolate between current and target yaw, roll, pitch using easedProgress
        enemyYaw = enemyYaw + (targetEnemyYaw - enemyYaw) * easedProgress;
        enemyRoll = enemyRoll + (targetEnemyRoll - enemyRoll) * easedProgress;
        enemyPitch = enemyPitch + (targetEnemyPitch - enemyPitch) * easedProgress;
    }

    public void draw(GL10 gl) {
        if (transitioning) {
            // Increment transition progress
            transitionProgress = Math.min(1f, transitionProgress + TRANSITION_SPEED);

            // Interpolate position from start to target using eased progress
            float easedProgress = (float) Math.sin(transitionProgress * Math.PI / 2); // Smooth easing
            x = startX + (targetX - startX) * easedProgress;
            y = startY + (targetY - startY) * easedProgress;

            // End transition when progress reaches 1
            if (transitionProgress >= 1f) {
                transitioning = false; // End the transition phase
                x = targetX; // Snap to final target position
                y = targetY;
            }
        } else {
            // Existing movement logic
            z += VELOCITY_Z;

            // Horizontal movement
            if (Math.abs(x - targetX) < MOVEMENT_VELOCITY) {
                x = targetX;
                setNewTargetX();
                rotationProgress = 0;
            } else {
                x += (x < targetX ? MOVEMENT_VELOCITY : -MOVEMENT_VELOCITY);
            }

            // Vertical movement
            if (Math.abs(y - targetY) < MOVEMENT_VELOCITY) {
                y = targetY;
                setNewTargetY();
                rotationProgress = 0;
            } else {
                y += (y < targetY ? MOVEMENT_VELOCITY : -MOVEMENT_VELOCITY);
            }

            shootProjectile(scene);
        }

        // Update rotation based on movement direction
        if (x < targetX) {
            targetEnemyYaw = 15f; // Tilt right
            targetEnemyRoll = -10f; // Roll right
        } else if (x > targetX) {
            targetEnemyYaw = -15f; // Tilt left
            targetEnemyRoll = 10f; // Roll left
        } else {
            targetEnemyYaw = 0f; // No tilt
            targetEnemyRoll = 0f; // No roll
        }

        if (y < targetY) {
            targetEnemyPitch = -10f; // Tilt up
        } else if (y > targetY) {
            targetEnemyPitch = 10f; // Tilt down
        } else {
            targetEnemyPitch = 0f; // No pitch
        }

        // Apply the rotation update
        updateRotation();

        // Draw the Enemy
        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glTranslatef(x*scaleFactor, y*scaleFactor, z);
        gl.glScalef(scaleFactor, scaleFactor, scaleFactor);
        gl.glRotatef(enemyYaw, 0, 1, 0);  // Yaw rotation (around Y axis)
        gl.glRotatef(enemyPitch, 1, 0, 0);  // Pitch rotation (around X axis)
        gl.glRotatef(enemyRoll, 0, 0, 1);  // Roll rotation (around Z axis)
        gl.glRotatef(180, 0, 1, 0); // Enemy facing armwing
        gl.glEnable(GL10.GL_LIGHTING);
        enemy.draw(gl);
        gl.glPopMatrix(); // Restore the transformation matrix

        float normalizedY = (y - (-halfHeight)) / (halfHeight - (-halfHeight));
        normalizedY = Math.min(1.0f, Math.max(0.0f, normalizedY)); // Ensure clamping between 0 and 1

        // Use the normalizedY to calculate shadowScale
        float shadowScale = Math.min(0.9f, Math.max(0.3f, 1.0f - normalizedY * 0.9f));

        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glTranslatef(x*scaleFactor, -13f, z);
        gl.glScalef(scaleFactor*shadowScale, scaleFactor*shadowScale, scaleFactor*shadowScale);
        gl.glRotatef(enemyYaw, 0, 1, 0);  // Yaw rotation (around Y axis)
        gl.glRotatef(enemyPitch, 1, 0, 0);  // Pitch rotation (around X axis)
        gl.glRotatef(enemyRoll, 0, 0, 1);  // Roll rotation (around Z axis)
        gl.glRotatef(180, 0, 1, 0); // Enemy facing armwing
        gl.glDisable(GL10.GL_LIGHTING);
        enemyShadow.setAlpha(0.5f);
        enemyShadow.setRGB(0.1f, 0.1f, 0.1f);
        enemyShadow.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glPopMatrix(); // Restore the transformation matrix
    }

    public void shootProjectile(Scene scene) {
        Random random = new Random();
        if ((int) (random.nextFloat()*100) == 28) {
            System.out.println("Enemy shooting");
            scene.shootEnemyProjectile(x*scaleFactor, y*scaleFactor, z);
        }
    }

    public float getX() {
        // Normalize x to a range between 0 and 100
        return (x - 11) / (15.7f - 11) * 100;
    }

    public float getY() {
        // Normalize y to a range between 0 and 100
        return (y - (-0.2f)) / (1 - (-0.2f)) * 100;
    }


    @Override
    public void updateScenePos(float z) {
        sceneZ = -(790-z);;
    }

    @Override
    public float getScenePos() {
        return sceneZ;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSpeed(float speed) {
        VELOCITY_Z = -speed;
        MOVEMENT_VELOCITY = speed/75; // Adjust horizontal speed proportionally
    }

    public boolean isDefeated() {
        return health <= 0;
    }

    public void defeat() {
        health = 0;
    }
}
