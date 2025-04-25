package org.ravry.gui;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.ravry.graphics.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Canvas extends GUIElement {
    public final List<GUIElement> children;
    private Matrix4f projection;

    public Canvas(float width, float height) {
        children = new ArrayList<>();
        projection = new Matrix4f().ortho(0, width, height, 0, -1.f, 1.f);

        Window.resizeListeners.add((_width, _height) -> {
            projection = new Matrix4f().ortho(0, _width, _height, 0, -1.f, 1.f);
        });
    }

    public void addChildren(GUIElement ... elements) {
        children.addAll(Arrays.stream(elements).toList());
    }


    @Override
    public void render(@Nullable Canvas parent) {
        children.forEach((child) -> {
            child.render(this);
        });
    }

    @Override
    public void delete() {
        children.forEach((child) -> {
            child.delete();
        });
    }

    public Matrix4f getProjection() {
        return projection;
    }
}
