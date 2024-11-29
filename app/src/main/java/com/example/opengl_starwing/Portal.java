package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Portal implements SceneDrawable{
    private Object3D portal = null;
    private Object3D insidePortal = null;
    private Object3D portalShadow = null; // Shadow object
    private float x, y, z;
    private float sceneZ;
    private float alpha = 0.0f; // Initial alpha value
    private final float TRANSPARENCY_THRESHOLD = 20f;
    private final float FADE_IN_DISTANCE = 50f; // Range over which the building fades in
    private final float COLLISION_THRESHOLD = 1f;
    private boolean collided = false;

    private Arwing arwing = null;

    public Portal(GL10 gl, Context context, float x, float y, float z) {
        portal = new Object3D(context, R.raw.portal);
        portalShadow = new Object3D(context, R.raw.portal); // Initialize shadow
        portal.loadTexture(gl, context, R.drawable.portalpalette);
        insidePortal = new Object3D(context, R.raw.insideportal);
        //insidePortal.loadTexture(gl, context, R.drawable.insideportal);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        collided = false;
    }

    public void setArwing(Arwing arwing) {
        this.arwing = arwing;
    }

    public void updateScenePos(float z) {
        sceneZ = z;
    }

    public void updateAlpha(float sceneZ) {
        if (sceneZ > TRANSPARENCY_THRESHOLD) {
            float fadeStart = TRANSPARENCY_THRESHOLD;
            float fadeEnd = TRANSPARENCY_THRESHOLD + FADE_IN_DISTANCE;
            alpha = Math.min(1f, Math.max(0f, (sceneZ - fadeStart) / (fadeEnd - fadeStart)));
        } else {
            alpha = 0f; // Fully transparent
        }
        portal.setAlpha(alpha);
    }

    public float getScenePos() {
        return sceneZ;
    }

    private float mapPortalXToArwingX(float portalX) {
        // Define the ranges
        float portalXMin = 22f;
        float portalXMax = 30f;
        float arwingXMin = -4f;
        float arwingXMax = 4f;

        // Map portal x to Arwing x range
        float arwingXRange = arwingXMax - arwingXMin;
        float portalXRange = portalXMax - portalXMin;

        return ((arwingXRange / portalXRange) * (portalX - portalXMin)) + arwingXMin;
    }

    private void checkArwingColision() {
        float arwingX = arwing.getArwingX();
        float mappedPortalX = mapPortalXToArwingX(x);

        if (sceneZ >= 420f) {
            System.out.println("arwingX = " + arwingX + ", mappedPortalX = " + mappedPortalX);
        }

        if (!collided) {
            if ((Math.abs(arwingX - mappedPortalX) < COLLISION_THRESHOLD) && (sceneZ >= 500f)) {
                System.out.println("Collision!");
                arwing.setBoostPercentage(arwing.getBoostPercentage() + 0.5f);
                collided = true;
            }
        }
    }


    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(15f, 15f, 12f);
        gl.glTranslatef(x, y, z);
        gl.glRotatef(90, 0f, 1f, 0f);
        if(alpha < 1f) {
            portal.setAlpha(alpha);
            portal.setRGB(1f, 1f, 1f);
            updateAlpha(sceneZ);
        }
        portal.draw(gl);
        insidePortal.setAlpha(0.5f);
        insidePortal.setRGB(0.2f, 0.2f, 0.4f);
        gl.glDepthMask(false);  // Disable depth writes
        gl.glDisable(GL10.GL_LIGHTING);
        insidePortal.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glDepthMask(true);  // Re-enable depth writes
        checkArwingColision();

        // Draw the shadow
        /*
        gl.glScalef(0.8f, 0.8f, 0.8f);
        gl.glRotatef(180, 1f, 0f, 0f);
        gl.glDisable(GL10.GL_LIGHTING);
        portalShadow.setAlpha(1f);
        portalShadow.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
         */
        gl.glPopMatrix();
    }
}
