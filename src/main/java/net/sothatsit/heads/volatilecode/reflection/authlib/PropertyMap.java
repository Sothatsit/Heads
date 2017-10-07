package net.sothatsit.heads.volatilecode.reflection.authlib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class PropertyMap extends ReflectObject {
    
    public static Class<?> PropertyMapClass;
    public static Class<?> MultiMapClass;
    public static Method containsKeyMethod;
    public static Method getMethod;
    
    static {
        PropertyMapClass = AuthLib.getAuthLibClass("com.mojang.authlib.properties.PropertyMap");
        MultiMapClass = AuthLib.getAuthLibClass("com.google.common.collect.Multimap");
        
        containsKeyMethod = ReflectionUtils.getMethod(MultiMapClass, "containsKey", boolean.class, Object.class);
        
        for (Method m : MultiMapClass.getDeclaredMethods()) {
            if (m.getName().equals("get") && m.getParameterTypes().length == 1) {
                getMethod = m;
                break;
            }
        }
    }
    
    public PropertyMap(Object handle) {
        super(handle);
    }
    
    public boolean containsKey(Object key) {
        try {
            return (boolean) containsKeyMethod.invoke(getHandle(), key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<Property> get(String key) {
        try {
            Collection<?> collection = (Collection<?>) getMethod.invoke(getHandle(), key);

            List<Property> list = new ArrayList<>();
            for (Object obj : collection) {
                list.add(new Property(obj));
            }
            
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
