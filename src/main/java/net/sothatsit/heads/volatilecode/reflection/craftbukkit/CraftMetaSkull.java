package net.sothatsit.heads.volatilecode.reflection.craftbukkit;

import java.lang.reflect.Field;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;
import net.sothatsit.heads.volatilecode.reflection.authlib.GameProfile;

public class CraftMetaSkull extends ReflectObject {
    
    public static Class<?> CraftMetaSkullClass;
    public static Field profileField;
    
    static {
        CraftMetaSkullClass = ReflectionUtils.getCraftBukkitClass("inventory.CraftMetaSkull");
        
        try {
            profileField = CraftMetaSkullClass.getDeclaredField("profile");
            profileField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    
    public CraftMetaSkull(Object handle) {
        super(handle);
    }
    
    public GameProfile getProfile() {
        try {
            return new GameProfile(profileField.get(getHandle()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
