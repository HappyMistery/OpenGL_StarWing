package com.example.opengl_starwing;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(new MyOpenGLRenderer(this));
        setContentView(view);
    }
}