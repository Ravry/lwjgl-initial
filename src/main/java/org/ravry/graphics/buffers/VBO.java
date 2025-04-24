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

    public void data(float[] data) {
        FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(data.length);
        dataBuffer.put(data).flip();

        glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);
    }

    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void delete() {
        glDeleteBuffers(id);
    }
}
