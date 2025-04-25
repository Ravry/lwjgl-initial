package org.ravry.gui;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Panel extends GUIElement{
    private List<GUIElement> children;

    public Panel() {
        children = new ArrayList<>();
    }

    @Override
    public void render(@Nullable Canvas parent) {
        children.forEach((child) -> {
            child.render(parent);
        });
    }

    @Override
    public void delete() {
        children.forEach((child) -> {
            child.delete();
        });
    }
}
