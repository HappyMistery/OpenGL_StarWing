package com.example.opengl_starwing;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class HUD {
    private final List<Drawable> GUI_lmns;
    private boolean boostActive = false;

    public HUD() {
        GUI_lmns = new ArrayList<>();
        HealthBar healthBar = new HealthBar(-4.5f, -3.5f, 1.75f, 0.3f);
        addLmn(healthBar);
        BoostBar boostBar = new BoostBar(2.8f, -3.5f, 1.75f, 0.3f);
        addLmn(boostBar);
        SwitchCamViewButton camViewButton = new SwitchCamViewButton(3.8f, 3.5f, 1.2f, 0.5f);
        addLmn(camViewButton);
    }

    public void addLmn(Drawable lmn) {
        GUI_lmns.add(lmn);
    }

    public void draw(GL10 gl, Arwing arwing, Scene scene) {
        gl.glDisable(GL10.GL_LIGHTING);
        for (Drawable lmn : GUI_lmns) {
            lmn.draw(gl);  // Calls the draw method of each element
        }

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

    public void useBoost() {
        for (Drawable lmn : GUI_lmns) {
            if (lmn instanceof BoostBar) {
                ((BoostBar) lmn).useBoost();
            }
        }
    }

    public void stopBoost() {
        for (Drawable lmn : GUI_lmns) {
            if (lmn instanceof BoostBar) {
                ((BoostBar) lmn).restoreBoost();
            }
        }
    }

    public float getBoostPercentage() {
        for (Drawable lmn : GUI_lmns) {
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
}