package net.sothatsit.heads.volatilecode.reflection.authlib;

import net.sothatsit.heads.volatilecode.reflection.Version;

public class AuthLib {
    
    public static Class<?> getAuthLibClass(String clazz) {
        if (Version.v1_8.higherThan(Version.getVersion())) {
            clazz = "net.minecraft.util." + clazz;
        }
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
}
