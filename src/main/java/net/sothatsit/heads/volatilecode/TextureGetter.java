package net.sothatsit.heads.volatilecode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.SafeCall;
import net.sothatsit.heads.volatilecode.reflection.authlib.GameProfile;
import net.sothatsit.heads.volatilecode.reflection.nms.MinecraftServer;
import net.sothatsit.heads.volatilecode.reflection.nms.TileEntitySkull;

import java.util.function.Consumer;

public class TextureGetter {
    
    public static String getCachedTexture(String name) {
        GameProfile profile = MinecraftServer.getServer().getUserCache().getCachedProfile(name);

        return (!profile.isNull() && profile.isComplete() ? profile.getTextureIfAvailable() : null);
    }
    
    public static void getTexture(String name, Consumer<String> callback) {
        Checks.ensureNonNull(name, "name");
        Checks.ensureNonNull(callback, "callback");

        Consumer<String> safeCallback = SafeCall.consumer(callback, "callback");

        String cachedTexture = getCachedTexture(name);

        if(cachedTexture != null) {
            callback.accept(cachedTexture);
            return;
        }
        
        TileEntitySkull.resolveTexture(name, profile -> {
            Heads.sync(() -> safeCallback.accept(findTexture(profile, true)));

            return true;
        });
    }

    public static String findTexture(GameProfile profile) {
        return findTexture(profile, false);
    }

    private static String findTexture(GameProfile profile, boolean cacheProfile) {
        if(profile.isNull() || !profile.isComplete())
            return null;

        String texture = profile.getTextureIfAvailable();

        if(cacheProfile && texture != null) {
            MinecraftServer.getServer().getUserCache().addProfile(profile);
        }

        return texture;
    }

}
