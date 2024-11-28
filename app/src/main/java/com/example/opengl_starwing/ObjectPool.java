package com.example.opengl_starwing;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class ObjectPool<T> {
    private final List<T> availableObjects;
    private final ObjectFactory<T> factory;
    private final int maxSize;
    private final GL10 gl;
    private final Context context;

    public ObjectPool(GL10 gl ,Context context, ObjectFactory<T> factory, int initialSize, int maxSize) {
        this.gl = gl;
        this.context = context;
        this.factory = factory;
        this.maxSize = maxSize;
        this.availableObjects = new ArrayList<>(initialSize);

        // Initialize the pool with initialSize objects
        for (int i = 0; i < initialSize; i++) {
            availableObjects.add(factory.create(gl, context, 0, 0, 0));
        }
    }

    public T getObject() {
        if (availableObjects.isEmpty()) {
            if (availableObjects.size() < maxSize) {
                return factory.create(gl, context, 0, 0, 0);
            }
            return null;
        }
        return availableObjects.remove(availableObjects.size() - 1);
    }

    public void returnObject(T object) {
        if (availableObjects.size() < maxSize) {
            availableObjects.add(object);
        }
    }

    public int getAvailableCount() {
        return availableObjects.size();
    }

    public interface ObjectFactory<T> {
        T create(GL10 gl, Context context, float x, float y, float z);
    }
}

