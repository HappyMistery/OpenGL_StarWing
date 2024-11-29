package com.example.opengl_starwing;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class HUD {
    private final List<HUDDrawable> GUI_lmns;
    private boolean boostActive = false;
    private FontRenderer fontRenderer;
    private float charSize = 0.3f;
    private float textY = 2.8f;
    private float textX = -1.3f;
    private float pictureX = -2.4f;
    private float pictureY = -3.75f;
    private boolean drawClaptrap = false;

    public HUD(GL10 gl, Context context) {
        fontRenderer = new FontRenderer(gl, context);
        GUI_lmns = new ArrayList<>();
        ShieldBar shieldBar = new ShieldBar(gl, context, -4.5f, -3.5f);
        addLmn(shieldBar);
        BoostBar boostBar = new BoostBar(2.8f, -3.5f);
        addLmn(boostBar);
        SwitchCamViewButton camViewButton = new SwitchCamViewButton(3.7f, 3.5f, 1.4f, 0.5f);
        addLmn(camViewButton);
        CharacterPicture claptrap = new CharacterPicture(gl, context, -2.4f, -3.75f, R.drawable.claptrap);
        addLmn(claptrap);
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
            useBoost();
        } else {
            stopBoost();
        }

        if (boostActive && getBoostPercentage() <= 0.01) {
            stopBoost();
            boost(arwing, scene);
        }
        gl.glEnable(GL10.GL_LIGHTING);
    }

    private void drawTexts(GL10 gl) {
        for (HUDDrawable lmn : GUI_lmns) {
            if (lmn instanceof CharacterPicture) {
                if (((CharacterPicture) lmn).getFileNameID() == R.drawable.claptrap) {
                    if(drawClaptrap) {
                        ((CharacterPicture) lmn).setPosition(pictureX, pictureY);
                        lmn.draw(gl);
                        fontRenderer.drawText(gl, "NOOOOO! DAMN YOU, STAIRS!", textX, textY, charSize);
                        fontRenderer.drawText(gl, "Dammit, Jack", textX, textY+charSize, charSize);
                        fontRenderer.drawText(gl, "how did you know stairs", textX, textY+charSize*2, charSize);
                        fontRenderer.drawText(gl, "were my ONLY weakness?!", textX, textY+charSize*3, charSize);
                    } else {
                        ((CharacterPicture) lmn).setPosition(pictureX-50, pictureY-50);
                        lmn.draw(gl);
                    }

                }
            }
        }
        fontRenderer.drawText(gl, "Shield", -4.5f, 3.1f, charSize);
        fontRenderer.drawText(gl, "Boost", 3.7f, 3.1f, charSize);
        fontRenderer.drawText(gl, "Cam View", 3.7f, -3.55f, charSize);
    }

    public void useBoost() {
        for (HUDDrawable lmn : GUI_lmns) {
            if (lmn instanceof BoostBar) {
                ((BoostBar) lmn).useBoost();
            }
        }
    }

    public void stopBoost() {
        for (HUDDrawable lmn : GUI_lmns) {
            if (lmn instanceof BoostBar) {
                ((BoostBar) lmn).restoreBoost();
            }
        }
    }

    public float getBoostPercentage() {
        for (HUDDrawable lmn : GUI_lmns) {
            if (lmn instanceof BoostBar) {
                return ((BoostBar) lmn).getBoostPercentage();
            }
        }
        return 0.0f;
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
        for (HUDDrawable lmn : GUI_lmns) {
            if (lmn instanceof BoostBar) {
                ((BoostBar) lmn).setBoostPercentage(f);
            }
        }
    }
}