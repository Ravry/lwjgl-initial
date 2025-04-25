package org.ravry.graphics.buffers;

public abstract class BufferObject {
    protected int id;
    public abstract void delete();
    public int getID() {
        return id;
    }
}
