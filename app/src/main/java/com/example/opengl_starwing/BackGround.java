package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class BackGround {
    private final BGImage bg, bgLightning;  // Background images (normal and with lightning)
    private int lightTime = 0;  // Time the light is on
    private int lightDuration = 5;  // Time limit to display lightning
    private boolean lightOn = false;    // Flag to indicate if light is on
    private final Random random = new Random();


    public BackGround(GL10 gl, Context context) {
        // Create the Background images and load their textures for the lightning animation
        bg = new BGImage();
        bgLightning = new BGImage();
        bg.loadTexture(gl, context, R.drawable.venom1);
        bgLightning.loadTexture(gl, context, R.drawable.venom1lightning);
    }

    public void draw(GL10 gl, Light light) {
        gl.glPushMatrix();
        gl.glScalef(41f, 35f, 0.0f); // Scale the Back ground images
        gl.glTranslatef(0f, 0.4f, 0.0f);	// Set the Back ground image to the Back ground of the scene

        // Display some lightning every once in a while (randomly)
        int randomNumber = random.nextInt(100) + 1;
        int lightningNum = 10;	// Number to display lightning
        if(randomNumber != lightningNum && !lightOn) {  // If it's not the lightning number and there is no lightning going on
            bg.draw(gl);
        } else {    // If there is lightning going on or it's the lightning number
            bgLightning.draw(gl);
            lightOn = true;

            light.setPosition(new float[]{0.0f, 1f, 0, 0.0f});
            light.setAmbientColor(new float[]{0.4f, 0.4f, 0.6f});
        }
        gl.glPopMatrix(); // Restore the transformation matrix
    }

    // Restore Background after lightning
    public void restoreBG(Light light) {
        if(lightOn) {
            lightTime++;
            if(lightTime == lightDuration) {    // If lightning reached its time limit
                lightTime = 0;  // Reset the light time
                lightDuration = random.nextInt(14) + 3; // Make next lightning be on for a different amount of time
                lightOn = false;

                light.setPosition(new float[]{0.0f, 0f, 1, 0.0f});
                light.setAmbientColor(new float[]{0.6f, 0.6f, 0.6f});
            }
        }
    }
}
