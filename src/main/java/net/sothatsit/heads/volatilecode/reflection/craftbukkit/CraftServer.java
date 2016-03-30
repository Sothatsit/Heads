package net.sothatsit.heads.volatilecode.reflection.craftbukkit;

import java.lang.reflect.Field;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;

public class CraftServer extends ReflectObject {
    
    private static Class<?> CraftServerClass;
    private static Field SimpleCommandMapField;
    
    static {
        CraftServerClass = ReflectionUtils.getCraftBukkitClass("CraftServer");
        
        for (Field f : CraftServerClass.getDeclaredFields()) {
            if (f.getType().equals(SimpleCommandMap.class)) {
                SimpleCommandMapField = f;
                SimpleCommandMapField.setAccessible(true);
                break;
            }
        }
    }
    
    public CraftServer(Object handle) {
        super(handle);
    }
    
    public SimpleCommandMap getCommandMap() {
        try {
            return (SimpleCommandMap) SimpleCommandMapField.get(getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static CraftServer get() {
        return new CraftServer(Bukkit.getServer());
    }
    
}
