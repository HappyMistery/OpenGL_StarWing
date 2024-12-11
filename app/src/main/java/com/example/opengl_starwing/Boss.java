package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Boss implements SceneDrawable {
    private Object3D boss;
    private Object3D bossShadow;
    private boolean isActivated = false;
    private final float scaleFactor = 30f;
    private float x, y, z, sceneZ, startZ, startY;

    private float targetZ, targetY, targetX; // Target for horizontal random movement
    private float transitionProgress = 0f; // Progress of the transition (0 to 1)
    private float loweringProgress = 0f; // Progress of the lowering (0 to 1)
    private static final float TRANSITION_SPEED = 0.01f; // Speed of the transition
    private static float MOVEMENT_VELOCITY = 0.01f; // Speed of horizontal movement
    private boolean isSlidingIn = false; // Whether the boss is sliding into position
    private boolean isLowering = false; // Whether the boss is lowering its y position

    private float rotationAngle = 180f; // Initial rotation angle (facing away)

    private Scene scene = null;
    private HUD hud = null;

    public Boss(GL10 gl, Context context) {
        // Load boss' model
        boss = new Object3D(context, R.raw.winton);
        bossShadow = new Object3D(context, R.raw.winton);
        boss.loadTexture(gl, context, R.drawable.paleta1);
        setNewTargetX(); // Initialize random target X position
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setHUD(HUD hud) {
        this.hud = hud;
    }

    public void initialize(float x, float y, float z) {
        this.x = x;
        this.targetX = x; // Start at the initialized X position
        this.targetY = y; // Store the initialized Y position
        this.y = y + 1f; // Start 1f higher
        startY = this.y;
        this.z = z + 300; // Start far away
        startZ = this.z;
        this.targetZ = z; // Target Z position
        sceneZ = z;
        transitionProgress = 0f;
        loweringProgress = 0f;
        isSlidingIn = true;
        isLowering = false;
        rotationAngle = 180f; // Start rotated 180 degrees
    }

    public void draw(GL10 gl) {
        if(!isActivated) {
            sceneZ += -(scene.getSpeed() * 2);
            System.out.println("SceneZ = " + sceneZ);
            return;
        }

        sceneZ += -scene.getSpeed();
        doInitialTransition();

        if (!isSlidingIn && !isLowering) {
            updateHorizontalMovement();
            shootProjectile(scene);
        }

        // Draw the Boss
        gl.glPushMatrix();
        gl.glTranslatef(x * scaleFactor, y * scaleFactor, z);
        gl.glRotatef(rotationAngle, 0, 1, 0); // Apply rotation smoothly
        gl.glScalef(scaleFactor, scaleFactor * 0.75f, scaleFactor);
        gl.glEnable(GL10.GL_LIGHTING);
        boss.draw(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(x * scaleFactor, -50f, z);
        gl.glRotatef(rotationAngle, 0, 1, 0); // Apply rotation smoothly
        gl.glRotatef(180, 0, 0, 1);
        gl.glScalef(scaleFactor * 0.9f, scaleFactor * 0.9f * 0.75f, scaleFactor * 0.9f);
        gl.glDisable(GL10.GL_LIGHTING);
        bossShadow.setAlpha(0.5f);
        bossShadow.setRGB(0.1f, 0.1f, 0.1f);
        bossShadow.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glPopMatrix();
    }

    private void doInitialTransition() {
        if (isSlidingIn) {
            transitionProgress = Math.min(1f, transitionProgress + TRANSITION_SPEED);
            float easedProgress = (float) Math.sin(transitionProgress * Math.PI / 2);
            z = startZ + (targetZ - startZ) * easedProgress;

            if (transitionProgress >= 1f) {
                isSlidingIn = false;
                isLowering = true;
            }
        }

        if (isLowering) {
            loweringProgress = Math.min(1f, loweringProgress + TRANSITION_SPEED / 2.5f);
            float easedProgress = (float) Math.sin(loweringProgress * Math.PI / 2);
            y = startY + (targetY - startY) * easedProgress;
            rotationAngle = 180f * (1f - loweringProgress);

            if (loweringProgress >= 1f) {
                isLowering = false;
            }
        }
    }

    private void setNewTargetX() {
        Random random = new Random();
        targetX = (random.nextFloat() * 4f) -2; // Generate a new random target X
    }

    private void updateHorizontalMovement() {
        if (Math.abs(x - targetX) < MOVEMENT_VELOCITY) {
            x = targetX; // Snap to target when close
            setNewTargetX(); // Generate a new target X
        } else {
            x += (x < targetX ? MOVEMENT_VELOCITY : -MOVEMENT_VELOCITY);
        }
    }

    public void shootProjectile(Scene scene) {
        Random random = new Random();
        float offset = 200;
        if (random.nextInt(50) == 1) {
            scene.shootEnemyProjectile(x*scaleFactor+400, y*scaleFactor-9, sceneZ, true);
        }
        if (random.nextInt(50) == 1) {
            scene.shootEnemyProjectile(x*scaleFactor+400, y*scaleFactor+5, sceneZ, true);
        }
        if (random.nextInt(50) == 1) {
            scene.shootEnemyProjectile(x*scaleFactor+390, y*scaleFactor+15, sceneZ, true);
            scene.shootEnemyProjectile(x*scaleFactor+410, y*scaleFactor+15, sceneZ, true);
        }
    }

    public float getX() {
        return (x - (-2)) / (2 - (-2)) * 100;
    }

    @Override
    public void updateScenePos(float z) {
        sceneZ = -(790 - z);
    }

    @Override
    public float getScenePos() {
        return sceneZ;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void activate() {
        isActivated = true;
    }

    public float getShieldPercentage() {
        return hud.getBossShieldPercentage();
    }

    public void setShieldPercentage(float f) {
        hud.setBossShieldPercentage(f);
    }

    public boolean isDefeated() {
        return hud.getBossShieldPercentage() <= 0;
    }
}
