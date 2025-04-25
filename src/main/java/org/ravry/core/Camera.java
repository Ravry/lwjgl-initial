package org.ravry.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.ravry.graphics.Window;
import org.ravry.utilities.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.ravry.utilities.Logger.LOG_STATE.DEFAULT_LOG;

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

        yaw -= mouseOffset.x * Time.deltaTime;
        pitch -= mouseOffset.y * Time.deltaTime;

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

        Vector3f scaledFront = new Vector3f(_front).mul(mouseOffset.y * Time.deltaTime * .25f * (distance / MAX_DISTANCE));
        Vector3f scaledRight = new Vector3f(_right).mul(mouseOffset.x * Time.deltaTime * .25f * (distance / MAX_DISTANCE));

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

    public Vector3f screenPosToWorldPos(Vector2f screenPos, float depth) {
        float ndcX = screenPos.x * 2.0f - 1.0f;
        float ndcY = screenPos.y * 2.0f - 1.0f;
        float ndcZ = depth;

        Vector4f ndcPos = new Vector4f(ndcX, ndcY, ndcZ, 1.0f);

        Matrix4f viewProjMatrix = new Matrix4f(projection).mul(getViewMatrix());
        Matrix4f invViewProjMatrix = new Matrix4f(viewProjMatrix).invert();

        Vector4f worldPos = ndcPos;
        worldPos.mul(invViewProjMatrix);

        if (worldPos.w != 0.0f) {
            worldPos.div(worldPos.w);
        }

        Vector3f result = new Vector3f(worldPos.x, worldPos.y, worldPos.z);
        Logger.LOG(DEFAULT_LOG, "World Position: " + result);
        return result;
    }
}