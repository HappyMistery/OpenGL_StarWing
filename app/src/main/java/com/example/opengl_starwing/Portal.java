package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class Portal implements SceneDrawable{
    private final Object3D portal, portalSpiral, insidePortal;
    private Armwing armwing = null;
    private float x, y, z, sceneZ;
    private boolean collided = false;
    private float spiralAngle = 0f;

    public Portal(GL10 gl, Context context, float x, float y, float z) {
        portal = new Object3D(context, R.raw.portal);
        portal.loadTexture(gl, context, R.drawable.portalpalette);
        insidePortal = new Object3D(context, R.raw.insideportal);
        insidePortal.setAlpha(0.5f);
        insidePortal.setRGB(0.0f, 0.1f, 0.1f);
        portalSpiral = new Object3D(context, R.raw.insideportal);
        portalSpiral.loadTexture(gl, context, R.drawable.insideportal);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        collided = false;   // Reset collision flag when portal is moved (when portal is replaced again in the scene)
    }

    public void setArmwing(Armwing armwing) {
        this.armwing = armwing;
    }

    public float getScenePos() {
        return sceneZ;
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(15f, 15f, 12f);
        gl.glTranslatef(x, y, z);
        gl.glRotatef(90, 0f, 1f, 0f);
        portal.draw(gl);
        checkArmwingColision(); // Check if the Armwing passed through a portal
        gl.glPopMatrix();
    }

    public void drawInnerPortal(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(15f, 15f, 12f);
        gl.glTranslatef(x, y, z);
        gl.glRotatef(90, 0f, 1f, 0f);
        gl.glScalef(0.9f, 0.9f, 0.9f);
        gl.glDisable(GL10.GL_LIGHTING);
        insidePortal.draw(gl);
        gl.glEnable(GL10.GL_LIGHTING);
        spiralAngle += 2f;
        gl.glScalef(1.3f, 1.3f, 1.3f);
        gl.glRotatef(spiralAngle % 360, 1f, 0f, 0f);
        portalSpiral.draw(gl);
        gl.glPopMatrix();
    }

    public void updateScenePos(float z) {
        sceneZ = z;
    }

    // Map the Portal's X position to the Armwing's X position in order to check collisions easily
    private float mapPortalXToArmwingX() {
        float portalXMin = 22f;
        float portalXMax = 30f;
        float armwingXMin = -4f;
        float armwingXMax = 4f;

        float armwingXRange = armwingXMax - armwingXMin;
        float portalXRange = portalXMax - portalXMin;

        return ((armwingXRange / portalXRange) * ((x-0.5f) - portalXMin)) + armwingXMin;
    }

    // Map the Portal's Y position to the Armwing's Y position in order to check collisions easily
    private float mapPortalYToArmwingY() {
        float portalYMin = 0.2f;
        float portalYMax = 2.2f;
        float armwingYMin = -1f;
        float armwingYMax = 1.3f;

        float armwingYRange = armwingYMax - armwingYMin;
        float portalYRange = portalYMax - portalYMin;

        return ((armwingYRange / portalYRange) * (y - portalYMin)) + armwingYMin;
    }

    private void checkArmwingColision() {
        float armwingX = armwing.getArmwingX();
        float armwingY = armwing.getArmwingY();
        float mappedPortalX = mapPortalXToArmwingX();
        float mappedPortalY = mapPortalYToArmwingY();

        if (!collided) {    // If the Armwing hasn't collided already with this portal, check collision
            float COLLISION_THRESHOLD = 1f;
            if ((Math.abs(armwingX - mappedPortalX) < COLLISION_THRESHOLD) &&
                    (Math.abs(armwingY - mappedPortalY) < COLLISION_THRESHOLD) &&
                    (sceneZ >= 450f) && (sceneZ <= 455f)) { // If all axes coincide, the Armwing passed through the portal
                armwing.setBoostPercentage(armwing.getBoostPercentage() + 0.5f);
                collided = true;    // Flag that the Armwing has passed through this building so that it doesn't pass through again
            }
        }
    }
}
