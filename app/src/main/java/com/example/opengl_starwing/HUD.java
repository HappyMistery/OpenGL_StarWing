package com.example.opengl_starwing;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class HUD {
    private final List<HUDDrawable> GUI_lmns;
    private boolean boostActive = false;
    private final FontRenderer fontRenderer;
    private boolean drawClaptrap = false;

    private final ShieldBar shieldBar;
    private final BoostBar boostBar;
    private final CharacterPicture claptrap;
    private final GameOverScreen gameOverScreen;

    public HUD(GL10 gl, Context context, float halfHeight, float halfWidth) {
        fontRenderer = new FontRenderer(gl, context);
        GUI_lmns = new ArrayList<>();
        shieldBar = new ShieldBar(gl, context, -4.5f, -3.5f);
        addLmn(shieldBar);
        boostBar = new BoostBar(2.8f, -3.5f);
        addLmn(boostBar);
        Button camViewButton = new Button(3.7f, 3.5f, 1.4f, 0.5f);
        addLmn(camViewButton);
        claptrap = new CharacterPicture(gl, context, -2.4f, -3.75f, R.drawable.claptrap);
        addLmn(claptrap);
        gameOverScreen = new GameOverScreen(-5f, -3.95f, halfHeight, halfWidth);
        addLmn(gameOverScreen);
    }

    public void addLmn(HUDDrawable lmn) {
        GUI_lmns.add(lmn);
    }

    public void draw(GL10 gl, Arwing arwing, Scene scene) {
        gl.glDisable(GL10.GL_LIGHTING);
        for (HUDDrawable lmn : GUI_lmns) {
            if (!(lmn instanceof CharacterPicture)) {
                lmn.draw(gl);  // Calls the draw method of each element
            }
        }
        drawTexts(gl);

        if (boostActive) {
            boostBar.useBoost();
        } else {
            stopBoost();
        }

        if (boostActive && getBoostPercentage() <= 0.01) {
            stopBoost();
            boost(arwing, scene);
        }

        // Check for Game Over condition
        if (getShieldPercentage() <= 0.0f) {
            gameOverScreen.activate();
        }

        shieldBar.regainShield();

        gl.glEnable(GL10.GL_LIGHTING);
    }

    private void drawTexts(GL10 gl) {
        float charSize = 0.3f;
        float pictureX = -2.4f;
        float pictureY = -3.75f;
        if(drawClaptrap) {
            claptrap.setPosition(pictureX, pictureY);
            claptrap.draw(gl);
            float textY = 2.8f;
            float textX = -1.3f;
            fontRenderer.drawText(gl, "NOOOOO! DAMN YOU, STAIRS!", textX, textY, charSize);
            fontRenderer.drawText(gl, "Dammit, Jack", textX, textY + charSize, charSize);
            fontRenderer.drawText(gl, "how did you know stairs", textX, textY + charSize *2, charSize);
            fontRenderer.drawText(gl, "were my ONLY weakness?!", textX, textY + charSize *3, charSize);
        } else {
            claptrap.setPosition(pictureX -50, pictureY -50);
            claptrap.draw(gl);
        }

        if(gameOverScreen.isActive()) {
            fontRenderer.drawText(gl, "GAME OVER", -2f, 0.5f, charSize*3);
            fontRenderer.drawText(gl, "Restart", 3.7f, -3.55f, charSize);
        } else {
            fontRenderer.drawText(gl, "Shield", -4.5f, 3.1f, charSize);
            fontRenderer.drawText(gl, "Boost", 3.7f, 3.1f, charSize);
            fontRenderer.drawText(gl, "Cam View", 3.7f, -3.55f, charSize);
        }
    }

    public void stopBoost() {
        boostBar.restoreBoost();
    }

    public float getBoostPercentage() {
        return boostBar.getBoostPercentage();
    }

    public void boost(Arwing arwing, Scene scene) {
        float speed = 1;
        if (!boostActive) {
            arwing.setTargetArwingZ(-1); // Lower target Z for boost
            boostActive = true;
            scene.setSpeed(speed*2);
        } else {
            arwing.setTargetArwingZ(1); // Lower target Z for boost
            boostActive = false;
            scene.setSpeed(speed);
        }
    }

    public void setBoostPercentage(float f) {
        boostBar.setBoostPercentage(f);
    }

    public float getShieldPercentage() {
        return shieldBar.getShieldPercentage();
    }

    public void setShieldPercentage(float f) {
        shieldBar.setShieldPercentage(f);
    }

    public boolean gameOver() {
        return gameOverScreen.isActive();
    }
}