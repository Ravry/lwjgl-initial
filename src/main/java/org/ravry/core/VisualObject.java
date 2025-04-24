package org.ravry.core;

import org.ravry.graphics.buffers.EBO;
import org.ravry.graphics.buffers.VAO;
import org.ravry.graphics.buffers.VBO;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

public class VisualObject extends EngineObject {
    public enum Primitive {
        Quad,
        Cube
    };

    private Mesh mesh;
    private VAO vao;
    private VBO vbo;
    private EBO ebo;

    public VisualObject(Primitive primitive) {

        switch (primitive) {
            case Primitive.Quad -> {
                mesh = new Mesh(VertexData.Quad.vertices, VertexData.Quad.indices);
                break;
            }
            case Primitive.Cube -> {
                mesh = new Mesh(VertexData.Cube.vertices, VertexData.Cube.indices);
                break;
            }
        }

        vao = new VAO();
        vbo = new VBO();
        ebo = new EBO();

        vao.bind();
        vbo.bind();
        ebo.bind();

        vbo.data(mesh.vertices);

        vao.attrib(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        vao.attrib(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);

        ebo.data(mesh.indices);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }

    public void render() {
        vao.bind();
        glDrawElements(GL_TRIANGLES, mesh.indices.length, GL_UNSIGNED_INT, 0);
        vao.unbind();
    }

    public void delete() {
        ebo.delete();
        vbo.delete();
        vao.delete();
    }
}
