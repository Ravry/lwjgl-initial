package org.ravry.core;

public abstract class EngineObject extends Transform{
    public EngineObject() {
        start();
    }

    public abstract void start();
    public abstract void update();
}