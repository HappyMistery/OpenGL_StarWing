package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Enemy implements SceneDrawable{
    // Variables for the armwing and its movement
    private final Object3D enemy;
    private final Object3D enemyShadow;
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

    public float getEnemyX() {
        return x;
    }

    public float getEnemyY() {
        return y;
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
        z += VELOCITY_Z;

        // Move towards the target x
        if (Math.abs(x - targetX) < MOVEMENT_VELOCITY) {
            x = targetX; // Snap to target to avoid overshooting
            setNewTargetX(); // Set a new target once the current one is reached
            rotationProgress = 0;
        } else {
            x += (x < targetX ? MOVEMENT_VELOCITY : -MOVEMENT_VELOCITY); // Move towards the target
        }

        // Move towards the target y
        if (Math.abs(y - targetY) < MOVEMENT_VELOCITY) {
            y = targetY; // Snap to target to avoid overshooting
            setNewTargetY(); // Set a new target once the current one is reached
            rotationProgress = 0;
        } else {
            y += (y < targetY ? MOVEMENT_VELOCITY : -MOVEMENT_VELOCITY); // Move towards the target
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
        float scaleFactor = 30f;
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
        scene.shootProjectile(x, y, z);
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
