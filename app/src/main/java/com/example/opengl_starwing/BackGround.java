package com.example.opengl_starwing;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class BackGround {
    // Variables for the Back Ground image and its lightning animation
    private final BGImage bg;
    private int lightTime = 0;
    private int lightDuration = 5;
    private boolean lightOn = false;
    private final Random random = new Random();


    public BackGround(GL10 gl, Context context) {
        // Create the Background image and load its textures for the lightning animation
        bg = new BGImage();
        bg.loadTexture(gl, context, R.drawable.venom1);
        bg.loadTexture(gl, context, R.drawable.venom1lightning);
    }

    public void restoreBG(Light light) {
        // Restore Background after lightning
        if(lightOn) {
            lightTime++;
            if(lightTime == lightDuration) {
                lightTime = 0;
                lightDuration = random.nextInt(14) + 3;
                lightOn = false;

                light.setPosition(new float[]{0.0f, 0f, 1, 0.0f});
                light.setAmbientColor(new float[]{0.6f, 0.6f, 0.6f});
            }
        }
    }

    public void draw(GL10 gl, Light light) {
        // Draw the background in the scene
        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glScalef(35f, 35f, 0.0f); // Scale the image
        gl.glRotatef(0, 1, 1, 0); // Rotate the image around the X and Y axes
        gl.glTranslatef(0f, 0.4f, 0.0f);	// Set the Back ground image to the Back ground of the scene

        // Display some lighting every once in a while (randomly)
        int randomNumber = random.nextInt(100) + 1;
        int lightningNum = 10;	// Number to display lightning
        if(randomNumber != lightningNum && !lightOn) {
            bg.drawImage(gl, 0);
        } else {
            bg.drawImage(gl, 1);
            lightOn = true;

            light.setPosition(new float[]{0.0f, 1f, 0, 0.0f});
            light.setAmbientColor(new float[]{0.4f, 0.4f, 0.6f});
        }
        gl.glPopMatrix(); // Restore the transformation matrix
    }
}
