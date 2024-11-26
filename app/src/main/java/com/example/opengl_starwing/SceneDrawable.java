package com.example.opengl_starwing;

import javax.microedition.khronos.opengles.GL10;

public interface SceneDrawable {
    void draw(GL10 gl);

    void updateScenePos(float z);

    float getScenePos();
}
