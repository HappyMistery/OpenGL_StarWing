package com.example.opengl_starwing;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class Stairs  implements SceneDrawable{
    private final Object3D stairs;

    public Stairs(GL10 gl,Context context) {
        stairs = new Object3D(context, R.raw.stairs);

        // Initialize the lights
        Light light1 = new Light(gl, GL10.GL_LIGHT4);
        Light light2 = new Light(gl, GL10.GL_LIGHT5);

        // Configure light1
        light1.setPosition(new float[]{50f, 20f, 200f, 1f});
        light1.setDiffuseColor(new float[]{0.5f, 0f, 0f, 1f});
        light1.setSpecularColor(new float[]{0.5f, 0f, 0f, 1f});
        light1.setAttenuation(1f, 0.1f, 0.02f);
        light1.enable();

        // Configure light2
        light2.setPosition(new float[]{-50f, 20f, 200f, 1f});
        light2.setDiffuseColor(new float[]{0f, 0f, 0.5f, 1f});
        light2.setSpecularColor(new float[]{0f, 0f, 0.5f, 1f});
        light2.setAttenuation(1f, 0.1f, 0.02f);
        light2.enable();
    }

    @Override
    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(25f, 25f, 25f);
        gl.glTranslatef(0, 1.9f, 4);
        stairs.draw(gl);
        gl.glPopMatrix();
    }

    @Override
    public void updateScenePos(float z) {

    }

    @Override
    public float getScenePos() {
        return 0;
    }
}
