package com.example.opengl_starwing;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class HUD implements Drawable{
    private List<Drawable> GUI_lmns;

    public HUD() {
        GUI_lmns = new ArrayList<>();
        HealthBar healthBar = new HealthBar(-4.5f, -3.5f, 1.75f, 0.3f);
        addLmn(healthBar);
        SwitchCamViewButton camViewButton = new SwitchCamViewButton(3.8f, 3.5f, 1.2f, 0.5f);
        addLmn(camViewButton);
    }

    public void addLmn(Drawable lmn) {
        GUI_lmns.add(lmn);
    }

    public void draw(GL10 gl) {
        gl.glDisable(GL10.GL_LIGHTING);
        for (Drawable lmn : GUI_lmns) {
            lmn.draw(gl);  // Calls the draw method of each element
        }
        gl.glEnable(GL10.GL_LIGHTING);
    }
}