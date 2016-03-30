package net.sothatsit.heads.volatilecode.reflection.nms.nbt;

import java.lang.reflect.Method;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class NBTTagCompound extends ReflectObject {
    
    public static Class<?> NBTTagCompoundClass;
    public static Method getCompoundMethod;
    public static Method getListMethod;
    public static Method getStringMethod;
    public static Method setStringMethod;
    public static Method setMethod;
    public static Method hasKeyMethod;
    
    static {
        NBTTagCompoundClass = ReflectionUtils.getNMSClass("NBTTagCompound");
        
        getCompoundMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "getCompound", NBTTagCompoundClass, String.class);
        getListMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "getList", NBTTagList.NBTTagListClass, String.class, int.class);
        getStringMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "getString", String.class, String.class);
        setStringMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "setString", void.class, String.class, String.class);
        setMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "set", void.class, String.class, NBTBase.NBTBaseClass);
        hasKeyMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "hasKey", boolean.class, String.class);
    }
    
    public NBTTagCompound(Object handle) {
        super(handle);
    }
    
    public NBTTagCompound() {
        super(newInstance());
    }
    
    public NBTTagCompound getCompound(String key) {
        try {
            return new NBTTagCompound(getCompoundMethod.invoke(handle, key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public NBTTagList getList(String key, int type) {
        try {
            return new NBTTagList(getListMethod.invoke(handle, key, type));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getString(String key) {
        try {
            return (String) getStringMethod.invoke(handle, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setString(String key, String value) {
        try {
            setStringMethod.invoke(handle, key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void set(String key, ReflectObject value) {
        try {
            setMethod.invoke(handle, key, value.getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean hasKey(String key) {
        try {
            return (boolean) hasKeyMethod.invoke(handle, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Object newInstance() {
        try {
            return NBTTagCompoundClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
