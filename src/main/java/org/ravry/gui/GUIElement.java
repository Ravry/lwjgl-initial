package org.ravry.gui;

import org.jetbrains.annotations.Nullable;

public abstract class GUIElement {
    public abstract void render(@Nullable Canvas parent);
    public abstract void delete();
}