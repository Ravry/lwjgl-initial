package org.ravry.graphics.buffers;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class VAO extends BufferObject {
    public VAO() {
        id = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void attrib(int index, int size, int type, boolean normalized, int stride, long pointer) {
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    @Override
    public void delete() {
        glDeleteVertexArrays(id);
    }
}
