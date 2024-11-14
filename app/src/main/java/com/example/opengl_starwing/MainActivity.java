package com.example.opengl_starwing;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

public class MainActivity extends Activity {
    private static final float TOUCH_SCALE_FACTOR = 1440;
    /** Called when the activity is first created. */
    float previousX, previousY;
    MyOpenGLRenderer myGLRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(myGLRenderer=new MyOpenGLRenderer(this));
        setContentView(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_W:
                myGLRenderer.moveArwing(0, 0.05f); // Move up
                break;
            case KeyEvent.KEYCODE_S:
                myGLRenderer.moveArwing(0, -0.05f); // Move down
                break;
            case KeyEvent.KEYCODE_A:
                myGLRenderer.moveArwing(-0.05f, 0); // Move left
                break;
            case KeyEvent.KEYCODE_D:
                myGLRenderer.moveArwing(0.05f, 0); // Move right
                break;
            case KeyEvent.KEYCODE_Q:
                myGLRenderer.rotateArwing(90); // Rotate right
                break;
            case KeyEvent.KEYCODE_E:
                myGLRenderer.rotateArwing(-90); // Rotate left
                break;
            default:
                // If none of the keys match, call the superclass method
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

}