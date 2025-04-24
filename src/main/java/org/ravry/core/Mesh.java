package org.ravry.core;

public class Mesh {
    public float[] vertices;
    public int[] indices;

    public Mesh(float[] vertices, int[]indices) {
        this.vertices = vertices;
        this.indices = indices;
    }
}
