package org.ravry.graphics;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.ravry.core.Input;
import org.ravry.core.Time;

import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static long handle;
    private final Renderer renderer;
    public static List<ResizeListener> resizeListeners = new ArrayList<>();

    public Window(String title, int width, int height) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("unable to initialize GLFW");

        glfwWindowHint(GLFW_SAMPLES, 4);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);

        if (handle == NULL)
            throw new RuntimeException("failed to create glfw window");

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);

        GL.createCapabilities();

        glViewport(0, 0, width, height);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_MULTISAMPLE);

        renderer = new Renderer((float)width, (float)height);

        glfwSetKeyCallback(handle, Input.inputCallback());
        glfwSetScrollCallback(handle, Input.scrollCallback());
        glfwSetFramebufferSizeCallback(handle, Window.resizeWindow());
    }


    public interface ResizeListener {
        void onResize(float width, float height);
    }

    private static GLFWFramebufferSizeCallbackI resizeWindow() {
        return (window, width, height) -> {
            glViewport(0, 0, width, height);
            for (var resizeListener : resizeListeners)
                resizeListener.onResize(width, height);
        };
    }

    private void update() {
        glfwPollEvents();
        Input.update();
    }

    public void run() {
        glClearColor(.2f, .2f, .2f, 1.f);
        double lastTime = 0.0f;
        double fpsTimer = 0.0f;

        while(!glfwWindowShouldClose(handle)) {
            double currentTime = glfwGetTime();
            Time.deltaTime = (float)currentTime - (float)lastTime;
            lastTime = currentTime;
            fpsTimer += Time.deltaTime;

            if (fpsTimer >= 2.f)
            {
                glfwSetWindowTitle(handle, "FPS: " + (int)(1.0/Time.deltaTime));
                fpsTimer = 0.f;
            }

            update();

            renderer.render();

            glfwSwapBuffers(handle);
        }

        terminate();
    }

    private void terminate() {
        renderer.terminate();
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static long getHandle() {
        return handle;
    }
}
