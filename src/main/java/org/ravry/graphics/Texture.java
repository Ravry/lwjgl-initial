package org.ravry.graphics;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

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
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public class Texture {
    private int id;
    private final int target;

    static class ImageData {
        public ByteBuffer pixels;
        public int width, height;

        public ImageData(ByteBuffer pixels, int width, int height) {
            this.pixels = pixels;
            this.width = width;
            this.height = height;
        }
    }

    public static void init() {
        ByteBuffer pixels = BufferUtils.createByteBuffer(4 * 2 * 2);
        pixels.put(new byte[]{
                (byte) 200, 0, (byte) 200, (byte) 255,
                (byte) 100, 0, (byte) 100, (byte) 255,
                (byte) 100, 0, (byte) 100, (byte) 255,
                (byte) 200, 0, (byte) 200, (byte) 255
        });
        pixels.flip();
        Renderer.textureHashMap.put("fallback", new Texture(2, 2, GL_RGBA, GL_NEAREST, true, pixels));
        MemoryUtil.memFree(pixels);
    }

    ImageData loadFile(String filePath) throws RuntimeException {
        ByteBuffer pixels;
        int width, height;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            pixels = stbi_load(filePath, w, h, channels, 4);

            if (pixels == null)
                throw new RuntimeException("error loading pixel data!");

            width = w.get();
            height = h.get();
        }

        return new ImageData(pixels, width, height);
    }

    public Texture(String textureFile) {
        target = GL_TEXTURE_2D;

        try {
            ImageData imageData = loadFile(textureFile);
            id = glGenTextures();
            bind();

            glTexImage2D(target, 0, GL_RGBA, imageData.width, imageData.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData.pixels);
            glGenerateMipmap(target);

            glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            stbi_image_free(imageData.pixels);
        } catch (Exception ex) {
            id = Renderer.textureHashMap.get("fallback").getID();
        }
    }

    public Texture(int width, int height, int format, int filter, boolean generateMipmap, @Nullable ByteBuffer byteBuffer) {
        target = GL_TEXTURE_2D;
        id = glGenTextures();
        bind();
        if (byteBuffer == null)
            glTexImage2D(target, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, NULL);
        else
            glTexImage2D(target, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, byteBuffer);

        if (generateMipmap)
            glGenerateMipmap(target);

        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, filter);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, filter);
    }

    public Texture(int width, int height, int format, int target, int type) {
        this.target = target;
        id = glGenTextures();
        bind();
        if (target == GL_TEXTURE_2D_MULTISAMPLE) {
            glTexImage2DMultisample(target, 4, format, width, height, true);
        } else {
            glTexImage2D(target, 0, format, width, height, 0, format, type, NULL);
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
            ImageData imageData;
            try {
                imageData = loadFile(files[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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