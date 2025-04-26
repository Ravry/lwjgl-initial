package org.ravry.graphics;

import org.joml.*;
import org.lwjgl.system.MemoryStack;
import org.ravry.core.Camera;
import org.ravry.core.Time;
import org.ravry.core.VisualObject;
import org.ravry.graphics.buffers.FBO;
import org.ravry.gui.Canvas;
import org.ravry.gui.Text;
import org.ravry.utilities.Utils;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class Renderer {
    public static HashMap<String, Shader> shaderHashMap = new HashMap<>();
    public static HashMap<String, Texture> textureHashMap = new HashMap<>();
    public static HashMap<String, VisualObject> visualObjectHashMap = new HashMap<>();

    private final Camera camera;

    private final FBO framebufferMSAA;
    private final FBO framebufferIntermediate;
    public static int g_Active = 0;
    public static final String[] g_Buffers = {
        "g_Albedo",
        "g_Depth"
    };

    private Canvas canvas;

    public Renderer(float width, float height) {
        camera = new Camera(width, height, Camera.CameraMode.Orbit);

        framebufferMSAA = new FBO((int)width, (int)height, GL_TEXTURE_2D_MULTISAMPLE);
        framebufferIntermediate = new FBO((int)width, (int)height, GL_TEXTURE_2D);

        shaderHashMap.put("grid", new Shader("resources/shader/grid/vertex.glsl", "resources/shader/grid/fragment.glsl"));
        shaderHashMap.put("default", new Shader("resources/shader/default/vertex.glsl", "resources/shader/default/fragment.glsl"));
        shaderHashMap.put("screen", new Shader("resources/shader/screen/vertex.glsl", "resources/shader/screen/fragment.glsl"));
        shaderHashMap.put("cubemap", new Shader("resources/shader/cubemap/vertex.glsl", "resources/shader/cubemap/fragment.glsl"));

        textureHashMap.put("cubemap", new Texture(new String[] {
                "resources/texture/cubemap/right.png",
                "resources/texture/cubemap/left.png",
                "resources/texture/cubemap/top.png",
                "resources/texture/cubemap/bottom.png",
                "resources/texture/cubemap/front.png",
                "resources/texture/cubemap/back.png",
        }));
        stbi_set_flip_vertically_on_load(true);
        textureHashMap.put("checkered", new Texture("resources/texture/.png"));

        visualObjectHashMap.put("grid", new VisualObject(VisualObject.Primitive.Quad));
        visualObjectHashMap.get("grid").matrix.rotateX((float)Math.toRadians(-90.0f)).scale(20.f);

        visualObjectHashMap.put("object", new VisualObject(VisualObject.Primitive.Cube));
        visualObjectHashMap.get("object").matrix.scale(1);

        String arial = Utils.getSystemFontPath("arial");
        Text.init(arial);

        canvas = new Canvas(width, height);
        canvas.addChildren(new Text(50, 50, "", arial, new Vector4f(0, 0, 0, 1)));

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        glClearColor(.2f, .2f, .2f, 1.f);
        glActiveTexture(GL_TEXTURE0);
    }

    public void render() {
        camera.update();
        ((Text)(canvas.children.get(0))).literal = "FPS: " + (int)(1.0f/ Time.deltaTime) + "\nG-Buffer: " + Renderer.g_Buffers[Renderer.g_Active];

        framebufferMSAA.bind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glDisable(GL_CULL_FACE);
        glDepthMask(false);
        shaderHashMap.get("cubemap")
                .use()
                .setUniformMat4("view", new Matrix4f(new Matrix3f(camera.getViewMatrix())))
                .setUniformMat4("projection", camera.projection)
                .setUniformInt("skybox", 0);
        textureHashMap.get("cubemap").bind();
        visualObjectHashMap.get("object").render();
        textureHashMap.get("cubemap").unbind();
        shaderHashMap.get("cubemap").unuse();
        glDepthMask(true);
        glEnable(GL_CULL_FACE);

        shaderHashMap.get("default")
                .use()
                .setUniformMat4("model", visualObjectHashMap.get("object").matrix)
                .setUniformMat4("view", camera.getViewMatrix())
                .setUniformMat4("projection", camera.projection);
        textureHashMap.get("checkered").bind();
        visualObjectHashMap.get("object").render();
        textureHashMap.get("checkered").unbind();
        shaderHashMap.get("default").unuse();

        shaderHashMap.get("grid")
                .use()
                .setUniformMat4("model", visualObjectHashMap.get("grid").matrix)
                .setUniformMat4("view", camera.getViewMatrix())
                .setUniformMat4("projection", camera.projection)
                .setUniformVec3("cameraPos", new Vector3f(camera.front.x, 0, camera.front.z));
        visualObjectHashMap.get("grid").render();
        shaderHashMap.get("grid").unuse();

        glBindFramebuffer(GL_READ_FRAMEBUFFER, framebufferMSAA.getID());
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, framebufferIntermediate.getID());

        framebufferMSAA.blit(GL_COLOR_ATTACHMENT0, GL_COLOR_BUFFER_BIT);
        framebufferMSAA.blit(0, GL_DEPTH_BUFFER_BIT);

        framebufferMSAA.unbind();

        glClear(GL_COLOR_BUFFER_BIT);

        glDisable(GL_DEPTH_TEST);

        framebufferIntermediate.g_Depth.bind(GL_TEXTURE1);
        framebufferIntermediate.g_Albedo.bind(GL_TEXTURE0);

        shaderHashMap.get("screen").use()
                .setUniformInt("_MainTex", 0)
                .setUniformInt("_DepthTex",  1)
                .setUniformInt("g_Active", g_Active);

        visualObjectHashMap.get("grid").render();

        shaderHashMap.get("screen").unuse();

        canvas.render(null);

        glEnable(GL_DEPTH_TEST);
    }

    public void terminate() {
        canvas.delete();

        framebufferIntermediate.delete();
        framebufferMSAA.delete();

        visualObjectHashMap.forEach((_, object) -> {
            object.delete();
        });

        shaderHashMap.forEach((_, shader) -> {
            shader.delete();
        });

        textureHashMap.forEach((_, texture) -> {
            texture.delete();
        });
    }
}
