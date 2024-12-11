package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Boss implements SceneDrawable {
    private final Object3D boss, bossShadow;
    private boolean isActivated = false;
    private final float scaleFactor = 30f;
    private float x, y, z, sceneZ, startZ, startY;

    private float targetZ, targetY, targetX; // Target for horizontal random movement
    private float transitionProgress = 0f; // Progress of the transition (0 to 1)
    private float loweringProgress = 0f; // Progress of the lowering (0 to 1)
    private static final float TRANSITION_SPEED = 0.01f; // Speed of the transition
    private boolean isSlidingIn = false; // Whether the boss is sliding into position
    private boolean isLowering = false; // Whether the boss is lowering its y position

    private float rotationAngle = 180f; // Initial rotation angle (facing away)

    private Scene scene = null;
    private HUD hud = null;

    public Boss(GL10 gl, Context context) {
        boss = new Object3D(context, R.raw.winton); // WINTON
        bossShadow = new Object3D(context, R.raw.winton);   // WINTON
        boss.loadTexture(gl, context, R.drawable.paleta1);
        setNewTargetX(); // Initialize random target X position
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setHUD(HUD hud) {
        this.hud = hud;
    }

    private void setNewTargetX() {
        Random random = new Random();
        targetX = (random.nextFloat() * 4f) -2; // Generate a new random target X between -2 and 2
    }

    // Map the boss' x to a value between 0 and 100 to make collision detection in Scene easier
    public float getX() {
        return (x - (-2)) / (2 - (-2)) * 100;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getShieldPercentage() {
        return hud.getBossShieldPercentage();
    }

    public void setShieldPercentage(float f) {
        hud.setBossShieldPercentage(f);
    }

    @Override
    public float getScenePos() {
        return sceneZ;
    }

    public void draw(GL10 gl) {
        if(!isActivated) {  // While not activated, the boss doesn't move but updates is Scene position (z)
            sceneZ += (scene.getSpeed() * 2);
            return; // Don't draw anything else if not activated
        }

        sceneZ += scene.getSpeed();
        doInitialTransition();  // Try to do the boss' initial transition (appearing animation)

        if (!isSlidingIn && !isLowering) {  // Once the boss is in position, it starts moving and shooting
            updateHorizontalMovement();
            shootProjectile(scene);
        }

        // Draw the Boss
        gl.glPushMatrix();
        gl.glTranslatef(x * scaleFactor, y * scaleFactor, z);
        gl.glRotatef(rotationAngle, 0, 1, 0); // Apply rotation smoothly
        gl.glScalef(scaleFactor, scaleFactor * 0.75f, scaleFactor);
        gl.glEnable(GL10.GL_LIGHTING);  // Boss is affected by Light
        boss.draw(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(x * scaleFactor, -50f, z);  // Shadow always on the ground (constant y)
        gl.glRotatef(rotationAngle, 0, 1, 0); // Apply rotation smoothly
        gl.glRotatef(180, 0, 0, 1); // Shadows are upside down
        gl.glScalef(scaleFactor * 0.9f, scaleFactor * 0.9f * 0.75f, scaleFactor * 0.9f); // Shadow is a little smaller
        gl.glDisable(GL10.GL_LIGHTING); // Boss' shadow is not affected by Light
        bossShadow.setAlpha(0.5f);  // Shadow is semi-transparent
        bossShadow.setRGB(0.1f, 0.1f, 0.1f); // Shadow has light-grey color
        bossShadow.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);  // ENable Lighting back
        gl.glPopMatrix();
    }

    public void initialize(float x, float y, float z) {
        this.x = x;
        this.targetX = x; // Start at the initialized X position
        this.targetY = y; // Store the initialized Y position
        this.y = y + 1f; // Start 1f higher
        startY = this.y;
        this.z = z + 300; // Start far away (behind the player and the camera)
        startZ = this.z;
        this.targetZ = z; // Target Z position
        sceneZ = z;
        transitionProgress = 0f;
        loweringProgress = 0f;
        isSlidingIn = true; // Set flag to true so that it starts its initial transition
        isLowering = false;
        rotationAngle = 180f; // Start rotated 180 degrees (facing away from the player)
    }

    private void doInitialTransition() {
        if (isSlidingIn) {  // Smoothly transition the boss' z position
            transitionProgress = Math.min(1f, transitionProgress + TRANSITION_SPEED);
            float easedProgress = (float) Math.sin(transitionProgress * Math.PI / 2);
            z = startZ + (targetZ - startZ) * easedProgress;

            if (transitionProgress >= 1f) { // Once the boss' z is its final z, start descending transition
                isSlidingIn = false;
                isLowering = true;
            }
        }

        if (isLowering) { // Smoothly transition the boss' y position and rotate it
            loweringProgress = Math.min(1f, loweringProgress + TRANSITION_SPEED / 2.5f);
            float easedProgress = (float) Math.sin(loweringProgress * Math.PI / 2);
            y = startY + (targetY - startY) * easedProgress;
            rotationAngle = 180f * (1f - loweringProgress);

            if (loweringProgress >= 1f) {   // Once the boss' y is its final y, stop lowering
                isLowering = false;
            }
        }
    }

    private void updateHorizontalMovement() {
        float MOVEMENT_VELOCITY = 0.01f;    // Speed of horizontal movement
        if (Math.abs(x - targetX) < MOVEMENT_VELOCITY) {
            x = targetX; // Snap to target when close
            setNewTargetX(); // Generate a new target X
        } else {
            x += (x < targetX ? MOVEMENT_VELOCITY : -MOVEMENT_VELOCITY); // Move towards target X
        }
    }

    public void shootProjectile(Scene scene) {
        Random random = new Random();
        // Shoot projectiles from the mouth
        if (random.nextInt(50) == 1) {
            scene.shootEnemyProjectile(x*scaleFactor+400, y*scaleFactor-9, sceneZ, true);
        }
        // Shoot projectiles from the nose
        if (random.nextInt(50) == 1) {
            scene.shootEnemyProjectile(x*scaleFactor+400, y*scaleFactor+5, sceneZ, true);
        }
        // Shoot projectiles from the eyes
        if (random.nextInt(50) == 1) {
            scene.shootEnemyProjectile(x*scaleFactor+390, y*scaleFactor+15, sceneZ, true);
            scene.shootEnemyProjectile(x*scaleFactor+410, y*scaleFactor+15, sceneZ, true);
        }
    }

    @Override
    public void updateScenePos(float z) {
        sceneZ = -(790 - z);
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void activate() {
        isActivated = true;
    }

    public boolean isDefeated() {
        return hud.getBossShieldPercentage() <= 0;
    }
}
