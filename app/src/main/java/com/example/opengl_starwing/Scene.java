package com.example.opengl_starwing;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Scene {
    private List<Drawable> dyObjs;
    private List<Drawable> stObjs;

    public Scene() {
        dyObjs = new ArrayList<Drawable>(32);
        stObjs = new ArrayList<Drawable>(32);
        GroundPoints gp = new GroundPoints(21, 21, 32f, 8f);
        dyObjs.add(gp);
    }

    public void addDyLmn(Drawable lmn) {
        dyObjs.add(lmn);
    }

    public void addStLmn(Drawable lmn) {
        stObjs.add(lmn);
    }

    public void draw(GL10 gl) {
        for (Drawable lmn : dyObjs) {
            lmn.draw(gl);  // Calls the draw method of each element
        }
    }
}
