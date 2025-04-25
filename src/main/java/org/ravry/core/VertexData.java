package org.ravry.core;

public class VertexData {
    public class Quad {
        public static float[] vertices = new float[] {
                -1.0f, -1.0f, 0.0f,     0.0f, 0.0f,
                -1.0f,  1.0f, 0.0f,     0.0f, 1.0f,
                1.0f, -1.0f, 0.0f,     1.0f, 0.0f,
                1.0f,  1.0f, 0.0f,     1.0f, 1.0f
        };

        public static int[] indices = new int[] {
                0, 2, 1,
                1, 2, 3
        };
    }

    public class Cube {
        public static float[] vertices = new float[] {
                // Front face
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,  // 0
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,  // 1
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,  // 2
                -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,  // 3

                // Back face
                0.5f, -0.5f, -0.5f,  0.0f, 0.0f,  // 4
                -0.5f, -0.5f, -0.5f,  1.0f, 0.0f,  // 5
                -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,  // 6
                0.5f,  0.5f, -0.5f,  0.0f, 1.0f,  // 7

                // Left face
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,  // 8
                -0.5f, -0.5f,  0.5f,  1.0f, 0.0f,  // 9
                -0.5f,  0.5f,  0.5f,  1.0f, 1.0f,  // 10
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,  // 11

                // Right face
                0.5f, -0.5f,  0.5f,  0.0f, 0.0f,  // 12
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f,  // 13
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,  // 14
                0.5f,  0.5f,  0.5f,  0.0f, 1.0f,  // 15

                // Top face
                -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,  // 16
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,  // 17
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,  // 18
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,  // 19

                // Bottom face
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,  // 20
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f,  // 21
                0.5f, -0.5f,  0.5f,  1.0f, 1.0f,  // 22
                -0.5f, -0.5f,  0.5f,  0.0f, 1.0f   // 23
        };

        public static int[] indices = new int[] {
                // Front
                0, 1, 2,  2, 3, 0,

                // Back
                4, 5, 6,  6, 7, 4,

                // Left
                8, 9,10, 10,11, 8,

                // Right
                12,13,14, 14,15,12,

                // Top
                16,17,18, 18,19,16,

                // Bottom
                20,21,22, 22,23,20
        };
    }
}
