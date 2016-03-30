package net.sothatsit.heads.volatilecode.reflection.nms;

import java.lang.reflect.Method;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;
import net.sothatsit.heads.volatilecode.reflection.authlib.GameProfile;

public class UserCache extends ReflectObject {
    
    public static Class<?> UserCacheClass;
    public static Method getProfileMethod;
    public static Method addProfileMethod;
    
    static {
        UserCacheClass = ReflectionUtils.getNMSClass("UserCache");
        
        getProfileMethod = ReflectionUtils.getMethod(UserCacheClass, "getProfile", GameProfile.GameProfileClass, String.class);
        addProfileMethod = ReflectionUtils.getMethod(UserCacheClass, void.class, GameProfile.GameProfileClass);
    }
    
    public UserCache(Object handle) {
        super(handle);
    }
    
    public GameProfile getProfile(String name) {
        try {
            return new GameProfile(getProfileMethod.invoke(handle, name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void addProfile(GameProfile profile) {
        try {
            addProfileMethod.invoke(handle, profile.getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
