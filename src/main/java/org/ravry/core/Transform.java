package org.ravry.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public Matrix4f matrix;
    protected Vector3f position;
    protected Vector3f scale;
    protected Vector3f rotation;

    public Transform() {
        matrix = new Matrix4f();
        position = new Vector3f(0);
        scale = new Vector3f(1);
        rotation = new Vector3f(0);
        updateMatrix();
    }

    private void updateMatrix() {
        matrix.identity()
                .translate(position)
                .rotateXYZ(rotation)
                .scale(scale);
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
        updateMatrix();
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
        updateMatrix();
    }

    public Vector3f getPosition() {
        return position;
    }
}
