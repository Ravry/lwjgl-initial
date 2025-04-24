package org.ravry.graphics.buffers;

import static org.lwjgl.opengl.GL15.*;

public class EBO extends BufferObject {
    public EBO() {
        id = glGenBuffers();
    }

    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    public void data(int[] indices) {
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void delete() {
        glDeleteBuffers(id);
    }
}
