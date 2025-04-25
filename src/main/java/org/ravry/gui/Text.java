package org.ravry.gui;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.ravry.graphics.Renderer;
import org.ravry.graphics.Shader;
import org.ravry.graphics.Texture;
import org.ravry.graphics.buffers.VAO;
import org.ravry.graphics.buffers.VBO;
import org.ravry.utilities.Logger;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.ravry.utilities.Logger.LOG_STATE.ERROR_LOG;
import static org.ravry.utilities.Utils.readFileToByteBuffer;

public class Text extends GUIElement {
    public static class FontData {
        public STBTTBakedChar.Buffer charData;
        public Texture texture;

        public FontData(STBTTBakedChar.Buffer charData, Texture texture) {
            this.charData = charData;
            this.texture = texture;
        }
    };

    public static boolean initialized = false;
    public static HashMap<String, FontData> fonts = new HashMap<>();
    public static final int FONT_SIZE = 16;
    public static final int BITMAP_SIZE = 256;

    public static void init(String ... fontPaths) {
        Renderer.shaderHashMap.put("font", new Shader("resources/shader/font/vertex.glsl", "resources/shader/font/fragment.glsl"));

        for (String fontPath : fontPaths) {
            try {
                ByteBuffer fontData = readFileToByteBuffer(fontPath);
                STBTTFontinfo fontInfo = STBTTFontinfo.create();

                if (!stbtt_InitFont(fontInfo, fontData)) {
                    Logger.LOG(ERROR_LOG, "failed to init font");
                }

                STBTTBakedChar.Buffer chardata = STBTTBakedChar.malloc(96);
                ByteBuffer bitmapBuffer = BufferUtils.createByteBuffer(BITMAP_SIZE * BITMAP_SIZE);
                stbtt_BakeFontBitmap(fontData, FONT_SIZE, bitmapBuffer, BITMAP_SIZE, BITMAP_SIZE, 32, chardata);
                fonts.put(fontPath, new FontData(chardata, new Texture(BITMAP_SIZE, BITMAP_SIZE, GL_RED, bitmapBuffer)));
            }
            catch (IOException ex) {
                Logger.LOG(ERROR_LOG, "failed to load font");
            }
        }
        initialized = true;
    }

    public String literal;
    public String font;
    public Color color;
    public float x;
    public float y;

    private VAO vao;
    private VBO vbo;

    public Text(float x, float y, String literal, String font, Color color) {
        if (!initialized)
            throw new RuntimeException("text renderer has not been initialized");

        this.x = x;
        this.y = y;
        this.literal = literal;
        this.font = font;
        this.color = color;

        vao = new VAO();
        vbo = new VBO();

        vao.bind();
        vbo.bind();

        float[] vertices = new float[4 * 4];
        vbo.data(vertices, GL_DYNAMIC_DRAW);
        vao.attrib(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        vao.attrib(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
    }

    @Override
    public void render(@Nullable Canvas parent) {
        FontData fontData = fonts.get(font);
        if (fontData == null) {
            return;
        }

        float scale = 1.0f;

        Renderer.shaderHashMap.get("font")
                .use()
                .setUniformMat4("projection", parent.getProjection())
                .setUniformVec4("fontColor", new Vector4f(color.getRed(), color.getGreen(), color.getBlue(), 1));

        fontData.texture.bind();
        vao.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            STBTTAlignedQuad quad = STBTTAlignedQuad.malloc(stack);
            FloatBuffer xPos = stack.floats(x);
            FloatBuffer yPos = stack.floats(y);

            for (int i = 0; i < literal.length(); i++) {
                char c = literal.charAt(i);

                if (c == '\n') {
                    yPos.put(0, yPos.get(0) + FONT_SIZE * scale);
                    xPos.put(0, x);
                    continue;
                }

                if (c < 32 || c > 127) continue;

                stbtt_GetBakedQuad(fontData.charData, BITMAP_SIZE, BITMAP_SIZE, c - 32, xPos, yPos, quad, true);

                float quadX0 = quad.x0() * scale;
                float quadY0 = quad.y0() * scale;
                float quadX1 = quad.x1() * scale;
                float quadY1 = quad.y1() * scale;

                float[] vertices = {
                        quadX0, quadY0, quad.s0(), quad.t0(),
                        quadX1, quadY0, quad.s1(), quad.t0(),
                        quadX1, quadY1, quad.s1(), quad.t1(),
                        quadX0, quadY1, quad.s0(), quad.t1()
                };

                vbo.bind();
                vbo.subData(vertices);

                glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
            }
        }

        Renderer.shaderHashMap.get("font").unuse();
    }

    @Override
    public void delete() {
        vao.delete();
        vbo.delete();
    }
}