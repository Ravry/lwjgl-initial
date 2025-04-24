package org.ravry.graphics.buffers;

import org.ravry.graphics.Renderer;
import org.ravry.graphics.Texture;
import org.ravry.graphics.Window;

import static org.lwjgl.opengl.GL30.*;

public class FBO extends BufferObject {
    private int rboDepth;

    public FBO(int width, int height) {
        id = glGenFramebuffers();
        bind();
        Renderer.textureHashMap.put("fbo", new Texture(width, height));
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, Renderer.textureHashMap.get("fbo").id, 0);

        rboDepth = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboDepth);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboDepth);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("framebuffer is not complete!");

        unbind();

        Window.resizeListeners.add((_width, _height) -> {
            bind();
            Renderer.textureHashMap.remove("fbo").delete();
            Renderer.textureHashMap.put("fbo", new Texture((int)_width, (int)_height));
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, Renderer.textureHashMap.get("fbo").id, 0);

            glDeleteRenderbuffers(rboDepth);
            rboDepth = glGenRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, rboDepth);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, (int)_width, (int)_height);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboDepth);
            unbind();
        });
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void delete() {
        glDeleteRenderbuffers(rboDepth);
        glDeleteFramebuffers(id);
    }
}