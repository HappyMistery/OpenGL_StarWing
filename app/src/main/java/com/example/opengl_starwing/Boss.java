package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Boss implements SceneDrawable{
    // Variables for the armwing and its movement
    private Object3D boss;
    private Object3D bossShadow;
    private final float scaleFactor = 30f;
    private float x, y, z, sceneZ, halfHeight;
    private float health = 1000;
    private float VELOCITY_Z = -1f;

    private Scene scene = null;
    private HUD hud = null;

    public Boss(GL10 gl, Context context) {
        // Load boss' model
        boss = new Object3D(context, R.raw.winton);
        bossShadow = new Object3D(context, R.raw.winton);
        boss.loadTexture(gl, context, R.drawable.paleta1);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setHUD(HUD hud) {
        this.hud = hud;
    }

    public void setHalfHeight(float halfHeight) {
        this.halfHeight = halfHeight;
    }

    public void initialize(float x, float y, float z, float offset) {
        this.x = x;
        this.y = y;
        this.z = z;
        sceneZ = offset;
        health = 1000;
    }

    public void draw(GL10 gl) {
        // Existing movement logic
        z += VELOCITY_Z;

        shootProjectile(scene);

        // Draw the Boss
        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glTranslatef(x*scaleFactor, y*scaleFactor, z);
        gl.glScalef(scaleFactor, scaleFactor, scaleFactor);
        gl.glRotatef(180, 0, 1, 0); // Boss facing armwing
        gl.glEnable(GL10.GL_LIGHTING);
        boss.draw(gl);
        gl.glPopMatrix(); // Restore the transformation matrix

        float normalizedY = (y - (-halfHeight)) / (halfHeight - (-halfHeight));
        normalizedY = Math.min(1.0f, Math.max(0.0f, normalizedY)); // Ensure clamping between 0 and 1

        // Use the normalizedY to calculate shadowScale
        float shadowScale = Math.min(0.9f, Math.max(0.3f, 1.0f - normalizedY * 0.9f));

        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glTranslatef(x*scaleFactor, -13f, z);
        gl.glScalef(scaleFactor*shadowScale, scaleFactor*shadowScale, scaleFactor*shadowScale);
        gl.glRotatef(180, 0, 1, 0); // Boss facing armwing
        gl.glDisable(GL10.GL_LIGHTING);
        bossShadow.setAlpha(0.5f);
        bossShadow.setRGB(0.1f, 0.1f, 0.1f);
        bossShadow.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glPopMatrix(); // Restore the transformation matrix
    }

    public void shootProjectile(Scene scene) {
        Random random = new Random();
        if ((int) (random.nextFloat()*50) == 28) {
            scene.shootEnemyProjectile(x*scaleFactor, y*scaleFactor, z, true);
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
    }

    public boolean isDefeated() {
        return health <= 0;
    }
}
