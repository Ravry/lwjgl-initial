package org.ravry.graphics.buffers;

import org.lwjgl.system.MemoryUtil;
import org.ravry.graphics.Renderer;
import org.ravry.graphics.Texture;
import org.ravry.graphics.Window;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public class FBO extends BufferObject {
    private RBO rboDepth;

    public FBO(int width, int height, boolean multisample) {
        id = glGenFramebuffers();
        bind();

        if (multisample) {
            Renderer.textureHashMap.put("fboMSAA", new Texture(width, height, GL_RGBA, 4));
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, Renderer.textureHashMap.get("fboMSAA").getID(), 0);
            rboDepth = new RBO(width, height, true);
            attachRenderbuffer(GL_DEPTH_STENCIL_ATTACHMENT, rboDepth);
        }
        else {
            Renderer.textureHashMap.put("fboIntermediate", new Texture(width, height, GL_RGBA, null));
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, Renderer.textureHashMap.get("fboIntermediate").getID(), 0);
        }

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("framebuffer is not complete!");

        unbind();

        Window.resizeListeners.add((_width, _height) -> {
            bind();

            if (multisample) {
                Renderer.textureHashMap.remove("fboMSAA").delete();
                Renderer.textureHashMap.put("fboMSAA", new Texture((int)_width, (int)_height, GL_RGBA, 4));
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, Renderer.textureHashMap.get("fboMSAA").getID(), 0);
                rboDepth.delete();
                rboDepth = new RBO((int)_width, (int)_height, multisample);
                attachRenderbuffer(GL_DEPTH_ATTACHMENT, rboDepth);
            }
            else {
                Renderer.textureHashMap.remove("fboIntermediate").delete();
                Renderer.textureHashMap.put("fboIntermediate", new Texture((int)_width, (int)_height, GL_RGBA, null));
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, Renderer.textureHashMap.get("fboIntermediate").getID(), 0);
            }
            unbind();
        });
    }

    private void attachRenderbuffer(int attachment, RBO rbo) {
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, rbo.getID());
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void delete() {
        rboDepth.delete();
        glDeleteFramebuffers(id);
    }
}