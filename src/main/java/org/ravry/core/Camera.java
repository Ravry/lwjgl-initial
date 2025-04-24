package org.ravry.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.ravry.graphics.Window;

import static org.lwjgl.glfw.GLFW.*;

public class Camera extends EngineObject {
    public enum CameraMode {
        Free,
        Orbit
    };

    public float fov = 60.0f;
    public float near = .1f;
    public float far = 100.f;
    public final float MAX_DISTANCE = 20.f;

    public Matrix4f projection;
    private float yaw = -90.0f;
    private float pitch = -60.0f;
    private float speed = 5.0f;

    public Vector3f front = new Vector3f(0, 0, 0);
    private Vector3f up = new Vector3f(0, 1, 0);
    private Vector3f right = new Vector3f(1, 0, 0);
    private Vector3f worldUp = new Vector3f(0, 1, 0);

    CameraMode cameraMode;

    private float distance = 10.0f;

    public Camera(float width, float height, CameraMode cameraMode) {
        this.projection = new Matrix4f().perspective((float)Math.toRadians(fov), width/height, near, far);
        this.cameraMode = cameraMode;

        Input.scrollListeners.add((offsetX, offsetY) -> {
            distance -= offsetY;
            distance = Math.clamp(distance, 1.f, MAX_DISTANCE);
        });

        Window.resizeListeners.add((_width, _height) -> {
            projection = new Matrix4f().perspective((float)Math.toRadians(fov), _width/_height, near, far);
        });
    }

    @Override
    public void start() {}

    @Override
    public void update() {
        if (cameraMode == CameraMode.Orbit) {
            if (glfwGetMouseButton(Window.getHandle(), GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
                Input.setCursorVisibility(false);

                if (Input.isKeyDown(GLFW_KEY_LEFT_SHIFT))
                {
                    handleMoveMouse();
                }
                else
                {
                    handleMouse();
                }
            }
            else if (!Input.cursorEnabled) {
                Input.setCursorVisibility(true);
            }
            handleOrbitMouse();
            return;
        }

        if (Input.cursorEnabled)
            return;

        handleKeyboard();
    }

    private void handleMouse() {
        Vector2f mouseOffset = Input.getMouseOffset();

        yaw -= mouseOffset.x;
        pitch -= mouseOffset.y;

        if (pitch > -20.0f) pitch = -20.0f;
        if (pitch < -80.0f) pitch = -80.0f;
    }

    private void handleMoveMouse() {
        Vector2f mouseOffset = Input.getMouseOffset();

        Vector3f _front = new Vector3f(position.sub(front));
        _front.y = 0;
        _front.normalize();

        Vector3f _right = new Vector3f(_front);
        _right.cross(worldUp);
        _right.normalize();

        Vector3f scaledFront = new Vector3f(_front).mul(mouseOffset.y * .2f * (distance / MAX_DISTANCE));
        Vector3f scaledRight = new Vector3f(_right).mul(mouseOffset.x * .2f * (distance / MAX_DISTANCE));

        Vector3f _move = new Vector3f(scaledFront).add(scaledRight);

        front.add(_move);
    }

    private void handleOrbitMouse() {
        float x = (float) (distance * Math.sin(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
        float y = (float) (distance * Math.cos(Math.toRadians(pitch)));
        float z = (float) (distance * Math.sin(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));

        position.x = front.x + x;
        position.y = front.y + y;
        position.z = front.z + z;
    }

    private void handleKeyboard() {
        float velocity = speed * (float) Time.deltaTime;
        Vector3f movement = new Vector3f(0);

        if (Input.isKeyDown(GLFW_KEY_W))
            movement.add(new Vector3f(front).mul(velocity));
        if (Input.isKeyDown(GLFW_KEY_S))
            movement.sub(new Vector3f(front).mul(velocity));
        if (Input.isKeyDown(GLFW_KEY_A))
            movement.sub(new Vector3f(right).mul(velocity));
        if (Input.isKeyDown(GLFW_KEY_D))
            movement.add(new Vector3f(right).mul(velocity));
        if (Input.isKeyDown(GLFW_KEY_SPACE))
            movement.add(new Vector3f(up).mul(velocity));
        if (Input.isKeyDown(GLFW_KEY_LEFT_CONTROL))
            movement.sub(new Vector3f(up).mul(velocity));

        if (movement.lengthSquared() > 0)
            setPosition(position.add(movement));
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, front, up);
    }
}