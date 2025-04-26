package org.ravry.graphics;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.ravry.utilities.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.ravry.utilities.Logger.LOG_STATE.WARNING_LOG;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public class Texture {
    private int id;
    private int target;

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
                Logger.LOG(WARNING_LOG, "failed to load image - " + stbi_failure_reason());
                pixels = BufferUtils.createByteBuffer(4 * 2 * 2);
                pixels.put(new byte[] {
                    (byte)200, 0, (byte)200, (byte)255,
                    (byte)100, 0, (byte)100, (byte)255,
                    (byte)100, 0, (byte)100, (byte)255,
                    (byte)200, 0, (byte)200, (byte)255
                });
                pixels.flip();
                return new ImageData(pixels, 2, 2);
            }

            width = w.get();
            height = h.get();
        }

        return new ImageData(pixels, width, height);
    }

    public Texture(String textureFile) {
        ImageData imageData = loadFile(textureFile);

        target = GL_TEXTURE_2D;
        id = glGenTextures();
        bind();

        glTexImage2D(target, 0, GL_RGBA, imageData.width, imageData.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData.pixels);
        glGenerateMipmap(target);

        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        stbi_image_free(imageData.pixels);
    }

    public Texture(int width, int height, int format, @Nullable ByteBuffer byteBuffer) {
        target = GL_TEXTURE_2D;
        id = glGenTextures();
        bind();
        if (byteBuffer == null)
            glTexImage2D(target, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, NULL);
        else
            glTexImage2D(target, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, byteBuffer);
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    public Texture(int width, int height, int format, int target, int type) {
        this.target = target;
        id = glGenTextures();
        bind();
        if (target == GL_TEXTURE_2D_MULTISAMPLE) {
            glTexImage2DMultisample(target, 4, format, width, height, true);
        }
        else {
            glTexImage2D(target,  0, format, width, height, 0, format, type, NULL);
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
        unbind();
    }

    public Texture(String[] files) {
        target = GL_TEXTURE_CUBE_MAP;
        id = glGenTextures();
        bind();

        for (int i = 0; i < files.length; i++) {
            ImageData imageData = loadFile(files[i]);
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, imageData.width, imageData.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData.pixels);
            stbi_image_free(imageData.pixels);
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    }

    public void bind() {
        glBindTexture(target, id);
    }

    public void bind(int slot) {
        glActiveTexture(slot);
        glBindTexture(target, id);
    }

    public void unbind() {
        glBindTexture(target, 0);
    }

    public void delete() {
        glDeleteTextures(id);
    }

    public int getID() {
        return id;
    }
}
