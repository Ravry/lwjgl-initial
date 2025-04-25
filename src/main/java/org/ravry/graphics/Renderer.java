package org.ravry.graphics;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.ravry.core.Camera;
import org.ravry.core.Input;
import org.ravry.core.Time;
import org.ravry.core.VisualObject;
import org.ravry.graphics.buffers.FBO;
import org.ravry.gui.Canvas;
import org.ravry.gui.Text;
import org.ravry.utilities.Utils;

import java.awt.*;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class Renderer {
    public static HashMap<String, Shader> shaderHashMap = new HashMap<>();
    public static HashMap<String, Texture> textureHashMap = new HashMap<>();
    public static HashMap<String, VisualObject> visualObjectHashMap = new HashMap<>();

    private final Camera camera;
    private final FBO framebuffer;

    private Canvas canvas;

    public Renderer(float width, float height) {
        camera = new Camera(width, height, Camera.CameraMode.Orbit);

        framebuffer = new FBO((int)width, (int)height);

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
        visualObjectHashMap.get("grid").matrix.rotateX((float)Math.toRadians(90.0f)).scale(20.f);

        visualObjectHashMap.put("object", new VisualObject(VisualObject.Primitive.Cube));
        visualObjectHashMap.get("object").matrix.scale(1);

        String arial = Utils.getSystemFontPath("minecraft_font");
        Text.init(arial);

        canvas = new Canvas(width, height);
        canvas.addChildren(new Text(50, 50, "", arial, Color.DARK_GRAY));
    }

    public void render() {
        camera.update();
        ((Text)(canvas.children.get(0))).literal = "fps: " + (int)(1.0f/ Time.deltaTime) + "\nAbonniert den Kanal";

        framebuffer.bind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

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

        shaderHashMap.get("grid")
                .use()
                .setUniformMat4("model", visualObjectHashMap.get("grid").matrix)
                .setUniformMat4("view", camera.getViewMatrix())
                .setUniformMat4("projection", camera.projection)
                .setUniformVec3("cameraPos", new Vector3f(camera.front.x, 0, camera.front.z));
        visualObjectHashMap.get("grid").render();
        shaderHashMap.get("grid").unuse();

        shaderHashMap.get("default")
                .use()
                .setUniformMat4("model", visualObjectHashMap.get("object").matrix)
                .setUniformMat4("view", camera.getViewMatrix())
                .setUniformMat4("projection", camera.projection);
        textureHashMap.get("checkered").bind();
        visualObjectHashMap.get("object").render();
        textureHashMap.get("checkered").unbind();
        shaderHashMap.get("default").unuse();

        framebuffer.unbind();

        glClear(GL_COLOR_BUFFER_BIT);

        glDisable(GL_DEPTH_TEST);

        shaderHashMap.get("screen").use();
        textureHashMap.get("fbo").bind();
        visualObjectHashMap.get("grid").render();
        textureHashMap.get("fbo").unbind();
        shaderHashMap.get("screen").unuse();

        canvas.render(null);

        glEnable(GL_DEPTH_TEST);
    }

    public void terminate() {
        canvas.delete();

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
