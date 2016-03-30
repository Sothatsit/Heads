package net.sothatsit.heads.volatilecode.reflection;

public abstract class ReflectObject {
    
    protected Object handle;
    
    public ReflectObject(Object handle) {
        this.handle = handle;
    }
    
    public Object getHandle() {
        return handle;
    }
    
}
