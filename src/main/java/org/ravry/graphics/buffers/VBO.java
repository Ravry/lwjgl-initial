package org.ravry.graphics.buffers;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VBO extends BufferObject {
    public VBO() {
        id = glGenBuffers();
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }

    public void data(float[] data, int usage) {
        glBufferData(GL_ARRAY_BUFFER, data, usage);
    }

    public void subData(float[] data) {
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
    }

    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void delete() {
        glDeleteBuffers(id);
    }
}
