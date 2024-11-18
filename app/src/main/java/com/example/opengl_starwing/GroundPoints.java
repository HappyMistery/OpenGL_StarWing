package com.example.opengl_starwing;

import android.opengl.GLES20;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class GroundPoints implements Drawable{
    private List<Cube> cubeList;
    private float x;
    private float y;
    private float z;
    private int rows;
    private int cols;
    private float cubeXSpacing;
    private float cubeYSpacing;

    // Constructor
    public GroundPoints(int rows, int cols, float cubeXSpacing, float cubeYSpacing) {
        this.rows = rows;
        this.cols = cols;
        this.cubeXSpacing = cubeXSpacing;
        this.cubeYSpacing = cubeYSpacing;
        cubeList = new ArrayList<>();
        createCubeMatrix();
    }

    public void setPosition(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
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
        gl.glTranslatef(0, y, z);
        for (Cube cube : cubeList) {
            cube.draw(gl);
        }
        gl.glEnable(GL10.GL_LIGHTING);
    }


}

