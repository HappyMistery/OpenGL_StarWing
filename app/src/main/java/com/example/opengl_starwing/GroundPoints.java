package com.example.opengl_starwing;

import android.opengl.GLES20;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class GroundPoints implements SceneDrawable{
    private final List<Cube> cubeList;
    private float x, y, z, newZ;
    private final int rows, cols;
    private final float cubeXSpacing, cubeYSpacing;
    private float sceneZ;

    // Constructor
    public GroundPoints(int rows, int cols, float cubeXSpacing, float cubeYSpacing, float prevZ) {
        this.rows = rows;
        this.cols = cols;
        this.cubeXSpacing = cubeXSpacing;
        this.cubeYSpacing = cubeYSpacing;
        cubeList = new ArrayList<>();
        createCubeMatrix();
        newZ = prevZ;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Method to create a matrix of cubes
    private void createCubeMatrix() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Calculate position of each cube based on row, column, and spacing
                float x = col * cubeXSpacing;
                float z = row * cubeYSpacing;
                Cube cube = new Cube(x, 0.0f, z); // Assuming y = 0 for ground level
                cubeList.add(cube);
            }
        }
    }

    // Method to draw the cubes
    @Override
    public void draw(GL10 gl) {
        gl.glDisable(GL10.GL_LIGHTING);
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        for (Cube cube : cubeList) {
            cube.draw(gl);
        }
        gl.glPopMatrix();
        gl.glEnable(GL10.GL_LIGHTING);
    }

    @Override
    public void updateScenePos(float z) {
        sceneZ = z;
    }

    @Override
    public float getScenePos() {
        return sceneZ;
    }

    // Reset logic for GroundPoints
    public void checkAndResetPosition(float currentZ, float resetThreshold, float speed, float initialZ) {
        float reset = (currentZ < 0) ? (resetThreshold + speed + (currentZ % initialZ)) : (currentZ % initialZ);
        float offset = (speed > 1 && reset % 2 == 0) ? 1 : 0;
        if (reset >= resetThreshold - offset) {
            newZ = (currentZ < 0) ? newZ + initialZ / 9 : newZ + initialZ;
            setPosition(x, y, newZ);
        }
    }


}

