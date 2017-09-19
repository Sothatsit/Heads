package net.sothatsit.heads.volatilecode.reflection.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Executor;

import com.google.common.base.Predicate;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;
import net.sothatsit.heads.volatilecode.reflection.authlib.GameProfile;

public class TileEntitySkull extends ReflectObject {
    
    public static Class<?> TileEntitySkullClass;
    public static Method resolveTextureMethod;
    public static Method getGameProfileMethod;
    public static Field executorField;
    
    static {
        TileEntitySkullClass = ReflectionUtils.getNMSClass("TileEntitySkull");
        
        resolveTextureMethod = ReflectionUtils.getMethod(TileEntitySkullClass, true, void.class, GameProfile.GameProfileClass, Predicate.class);
        getGameProfileMethod = ReflectionUtils.getMethod(TileEntitySkullClass, "getGameProfile");
        
        for (Method m : TileEntitySkullClass.getMethods()) {
            Class<?>[] params = m.getParameterTypes();
            if (Modifier.isStatic(m.getModifiers()) && params.length == 2 && params[0].equals(GameProfile.class) && params[1].equals(Predicate.class)) {
                resolveTextureMethod = m;
                resolveTextureMethod.setAccessible(true);
                break;
            }
        }
        
        try {
            executorField = TileEntitySkullClass.getDeclaredField("executor");
            executorField.setAccessible(true);
            
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(executorField, executorField.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {
            executorField = null;
        }
    }
    
    public TileEntitySkull(Object handle) {
        super(handle);
    }
    
    public GameProfile getGameProfile() {
        try {
            return new GameProfile(getGameProfileMethod.invoke(handle));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void resolveTexture(GameProfile profile, Predicate<GameProfile> callback) {
        try {
            resolveTextureMethod.invoke(null, profile.getHandle(), (Predicate) gameProfileHandle -> {
                return callback.apply(new GameProfile(gameProfileHandle));
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Executor getExecutor() {
        try {
            return (Executor) executorField.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void setExecutor(Executor executor) {
        try {
            executorField.set(null, executor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
