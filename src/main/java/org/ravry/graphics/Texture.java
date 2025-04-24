package org.ravry.graphics;

import org.lwjgl.system.MemoryStack;
import org.ravry.utilities.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.ravry.utilities.Logger.LOG_STATE.ERROR_LOG;

public class Texture {
    private final String FALLBACK_TEXTURE = "resources/texture/checkered.png";
    public int id;

    class ImageData {
        public ByteBuffer pixels;
        public int width, height;

        public ImageData(ByteBuffer pixels, int width, int height) {
            this.pixels = pixels;
            this.width = width;
            this.height = height;
        }
    };

    ImageData loadFile(String filePath) {
        ByteBuffer pixels;
        int width, height;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            pixels = stbi_load(filePath, w, h, channels, 4);

            if (pixels == null)
            {
                Logger.LOG(ERROR_LOG, "failed to load image - " + stbi_failure_reason());
                return loadFile(FALLBACK_TEXTURE);
            }

            width = w.get();
            height = h.get();
        }

        return new ImageData(pixels, width, height);
    }

    public Texture(String textureFile) {
        ImageData imageData = loadFile(textureFile);

        id = glGenTextures();
        bind();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imageData.width, imageData.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData.pixels);
        glGenerateMipmap(GL_TEXTURE_2D);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        stbi_image_free(imageData.pixels);
    }
    public Texture(int width, int height) {
        id = glGenTextures();
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void delete() {
        glDeleteTextures(id);
    }
}
