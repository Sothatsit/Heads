package net.sothatsit.heads.volatilecode.reflection.authlib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class GameProfile extends ReflectObject {
    
    public static Class<?> GameProfileClass;
    public static Constructor<?> GameProfileConstructor;
    public static Method isCompleteMethod;
    public static Method getPropertiesMethod;
    public static Method getNameMethod;
    
    static {
        GameProfileClass = AuthLib.getAuthLibClass("com.mojang.authlib.GameProfile");
        
        GameProfileConstructor = ReflectionUtils.getConstructor(GameProfileClass, UUID.class, String.class);
        
        isCompleteMethod = ReflectionUtils.getMethod(GameProfileClass, "isComplete", boolean.class);
        getPropertiesMethod = ReflectionUtils.getMethod(GameProfileClass, "getProperties", PropertyMap.PropertyMapClass);
        getNameMethod = ReflectionUtils.getMethod(GameProfileClass, "getName", String.class);
    }
    
    public GameProfile(Object handle) {
        super(handle);
    }
    
    public GameProfile(UUID uuid, String name) {
        super(newInstance(uuid, name));
    }
    
    public boolean isComplete() {
        try {
            return (boolean) isCompleteMethod.invoke(getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public PropertyMap getProperties() {
        try {
            return new PropertyMap(getPropertiesMethod.invoke(getHandle()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getName() {
        try {
            return (String) getNameMethod.invoke(getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Object newInstance(UUID uuid, String name) {
        try {
            return GameProfileConstructor.newInstance(uuid, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
