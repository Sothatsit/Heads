package net.sothatsit.heads.volatilecode.reflection.authlib;

import java.lang.reflect.Method;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class Property extends ReflectObject {
    
    public static Class<?> PropertyClass;
    public static Method getValueMethod;
    public static Method getSignatureMethod;
    public static Method hasSignatureMethod;
    
    static {
        PropertyClass = AuthLib.getAuthLibClass("com.mojang.authlib.properties.Property");
        
        getValueMethod = ReflectionUtils.getMethod(PropertyClass, "getValue", String.class);
        getSignatureMethod = ReflectionUtils.getMethod(PropertyClass, "getSignature", String.class);
        hasSignatureMethod = ReflectionUtils.getMethod(PropertyClass, "hasSignature", boolean.class);
    }
    
    public Property(Object handle) {
        super(handle);
    }
    
    public String getValue() {
        try {
            return (String) getValueMethod.invoke(getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getSignature() {
        try {
            return (String) getSignatureMethod.invoke(getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasSignature() {
        try {
            return (Boolean) hasSignatureMethod.invoke(getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
