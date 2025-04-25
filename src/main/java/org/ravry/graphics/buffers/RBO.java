package org.ravry.graphics.buffers;

import static org.lwjgl.opengl.GL11.GL_RENDER;
import static org.lwjgl.opengl.GL30.*;

public class RBO extends BufferObject {
    public RBO(int width, int height, boolean multisample) {
        id = glGenRenderbuffers();
        bind();
        if (multisample) {
            glRenderbufferStorageMultisample(GL_RENDERBUFFER, 4, GL_DEPTH24_STENCIL8, width, height);
        } else {
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        }
        unbind();
    }

    void bind() {
        glBindRenderbuffer(GL_RENDERBUFFER, id);
    }

    void unbind() {
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    @Override
    public void delete() {
        glDeleteRenderbuffers(id);
    }
}