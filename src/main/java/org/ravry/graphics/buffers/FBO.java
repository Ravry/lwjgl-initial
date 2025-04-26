package org.ravry.graphics.buffers;

import org.ravry.graphics.Texture;
import org.ravry.graphics.Window;

import static org.lwjgl.opengl.GL30.*;

public class FBO extends BufferObject {
    public Texture g_Albedo;
    public Texture g_Depth;

    public FBO(int width, int height, int target) {
        id = glGenFramebuffers();
        bind();

        g_Albedo = new Texture(width, height, GL_RGBA, target, GL_UNSIGNED_BYTE);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, target, g_Albedo.getID(), 0);

        g_Depth = new Texture(width, height, GL_DEPTH_COMPONENT, target, GL_FLOAT);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, target, g_Depth.getID(), 0);

        glDrawBuffers(new int[]{
            GL_COLOR_ATTACHMENT0
        });

        unbind();

        Window.resizeListeners.add((_width, _height) -> {
            bind();
            g_Albedo.delete();
            g_Albedo = new Texture((int)_width, (int)_height, GL_RGBA, target, GL_UNSIGNED_BYTE);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, target, g_Albedo.getID(), 0);

            g_Depth.delete();
            g_Depth = new Texture((int)_width, (int)_height, GL_DEPTH_COMPONENT, target, GL_FLOAT);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, target, g_Depth.getID(), 0);

            unbind();
        });
    }

    private void attachRenderbuffer(int attachment, RBO rbo) {
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, rbo.getID());
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    public void blit(int attachment, int mask) {
        if (mask == GL_COLOR_BUFFER_BIT)
        {
            glReadBuffer(attachment);
            glDrawBuffer(attachment);
        }
        glBlitFramebuffer(0, 0, Window.width, Window.height, 0, 0, Window.width, Window.height, mask, GL_NEAREST);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void delete() {
        g_Depth.delete();
        g_Albedo.delete();
        glDeleteFramebuffers(id);
    }
}