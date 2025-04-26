package org.ravry.core;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.ravry.graphics.Renderer;
import org.ravry.graphics.Window;
import org.ravry.utilities.Logger;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.ravry.utilities.Logger.LOG_STATE.DEFAULT_LOG;

public class Input {
    /**
     * ██╗  ██╗███████╗██╗   ██╗███████╗    ██╗███╗   ██╗██████╗ ██╗   ██╗████████╗
     * ██║ ██╔╝██╔════╝╚██╗ ██╔╝██╔════╝    ██║████╗  ██║██╔══██╗██║   ██║╚══██╔══╝
     * █████╔╝ █████╗   ╚████╔╝ ███████╗    ██║██╔██╗ ██║██████╔╝██║   ██║   ██║
     * ██╔═██╗ ██╔══╝    ╚██╔╝  ╚════██║    ██║██║╚██╗██║██╔═══╝ ██║   ██║   ██║
     * ██║  ██╗███████╗   ██║   ███████║    ██║██║ ╚████║██║     ╚██████╔╝   ██║
     * ╚═╝  ╚═╝╚══════╝   ╚═╝   ╚══════╝    ╚═╝╚═╝  ╚═══╝╚═╝      ╚═════╝    ╚═╝
     */
    private static boolean[] keyDown = new boolean[GLFW_KEY_LAST + 1];
    public static boolean cursorEnabled = true;

    public static GLFWKeyCallbackI inputCallback() {
        return (window, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                keyDown[key] = true;

                switch (key)
                {
                    case GLFW_KEY_ESCAPE -> {
                        glfwSetWindowShouldClose(window, true);
                    }
                    case GLFW_KEY_G -> {
                        Renderer.g_Active = (Renderer.g_Active + 1) % Renderer.g_Buffers.length;
                    }
                }
            } else if (action == GLFW.GLFW_RELEASE) {
                keyDown[key] = false;
            } else if (action == GLFW.GLFW_REPEAT) {
            }
        };
    }

    public static void setCursorVisibility(boolean value) {
        cursorEnabled = value;

        glfwSetInputMode(Window.getHandle(), GLFW_CURSOR, cursorEnabled ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);

        DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(Window.getHandle(), xpos, ypos);
        lastMouseX = xpos.get();
        lastMouseY = ypos.get();
    }

    public static boolean isKeyDown(int key) {
        return keyDown[key];
    }




    /**
     * ███╗   ███╗ ██████╗ ██╗   ██╗███████╗███████╗    ███╗   ███╗ ██████╗ ██╗   ██╗███████╗███╗   ███╗███████╗███╗   ██╗████████╗
     * ████╗ ████║██╔═══██╗██║   ██║██╔════╝██╔════╝    ████╗ ████║██╔═══██╗██║   ██║██╔════╝████╗ ████║██╔════╝████╗  ██║╚══██╔══╝
     * ██╔████╔██║██║   ██║██║   ██║███████╗█████╗      ██╔████╔██║██║   ██║██║   ██║█████╗  ██╔████╔██║█████╗  ██╔██╗ ██║   ██║
     * ██║╚██╔╝██║██║   ██║██║   ██║╚════██║██╔══╝      ██║╚██╔╝██║██║   ██║╚██╗ ██╔╝██╔══╝  ██║╚██╔╝██║██╔══╝  ██║╚██╗██║   ██║
     * ██║ ╚═╝ ██║╚██████╔╝╚██████╔╝███████║███████╗    ██║ ╚═╝ ██║╚██████╔╝ ╚████╔╝ ███████╗██║ ╚═╝ ██║███████╗██║ ╚████║   ██║
     * ╚═╝     ╚═╝ ╚═════╝  ╚═════╝ ╚══════╝╚══════╝    ╚═╝     ╚═╝ ╚═════╝   ╚═══╝  ╚══════╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝
     */
    private static double lastMouseX, lastMouseY;
    private static float offsetX, offsetY;
    public static float scrollOffsetX, scrollOffsetY;
    public static float sensitivity = 4.f;
    public static float currentMouseX, currentMouseY;

    public static void update() {
        offsetX = 0;
        offsetY = 0;
        scrollOffsetX = 0;
        scrollOffsetY = 0;
    }

    public static Vector2f getMouseOffset() {
        return new Vector2f(offsetX, offsetY);
    }

    public static GLFWMouseButtonCallbackI mouseButtonCallback() {
        return (window, button, action, mods) -> {

        };
    }

    public static GLFWCursorPosCallbackI mousePosCallback() {
        return (long window, double xpos, double ypos) -> {
            currentMouseX = (float)xpos;
            currentMouseY = (float)ypos;

            offsetX += currentMouseX - (float)lastMouseX;
            offsetY += currentMouseY - (float)lastMouseY;

            lastMouseX = xpos;
            lastMouseY = ypos;
        };
    }

    /**
     * ███████╗ ██████╗██████╗  ██████╗ ██╗     ██╗     ██╗███╗   ██╗ ██████╗
     * ██╔════╝██╔════╝██╔══██╗██╔═══██╗██║     ██║     ██║████╗  ██║██╔════╝
     * ███████╗██║     ██████╔╝██║   ██║██║     ██║     ██║██╔██╗ ██║██║  ███╗
     * ╚════██║██║     ██╔══██╗██║   ██║██║     ██║     ██║██║╚██╗██║██║   ██║
     * ███████║╚██████╗██║  ██║╚██████╔╝███████╗███████╗██║██║ ╚████║╚██████╔╝
     * ╚══════╝ ╚═════╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚══════╝╚═╝╚═╝  ╚═══╝ ╚═════╝
     */
    public static GLFWScrollCallbackI scrollCallback() {
        return (window, xoffset, yoffset) -> {
            float scrollX = (float)xoffset;
            float scrollY = (float)yoffset;
            scrollOffsetX += scrollX;
            scrollOffsetY += scrollY;
        };
    }
}