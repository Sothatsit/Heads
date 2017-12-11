package net.sothatsit.heads.volatilecode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.SafeCall;
import net.sothatsit.heads.volatilecode.reflection.authlib.GameProfile;
import net.sothatsit.heads.volatilecode.reflection.authlib.Property;
import net.sothatsit.heads.volatilecode.reflection.authlib.PropertyMap;
import net.sothatsit.heads.volatilecode.reflection.nms.MinecraftServer;
import net.sothatsit.heads.volatilecode.reflection.nms.TileEntitySkull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class TextureGetter {
    
    public static String getCachedTexture(String name) {
        GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(name);
        
        if (!profile.isNull() && profile.isComplete() && profile.getProperties() != null && profile.getProperties().containsKey("textures")) {
            Iterator<Property> iterator = profile.getProperties().get("textures").iterator();

            return iterator.hasNext() ? iterator.next().getValue() : null;
        }
        
        return null;
    }
    
    public static void getTexture(String name, Consumer<String> callback) {
        Checks.ensureNonNull(name, "name");
        Checks.ensureNonNull(callback, "callback");

        Consumer<String> safeCallback = SafeCall.consumer(callback, "callback");

        GameProfile existingProfile = MinecraftServer.getServer().getUserCache().getProfile(name);
        
        if (existingProfile.isNull()) {
            existingProfile = new GameProfile(null, name);
        }

        if (existingProfile.isComplete()) {
            PropertyMap properties = existingProfile.getProperties();

            if(!properties.isNull() && properties.containsKey("textures")) {
                for (Property p : properties.get("textures")) {
                    safeCallback.accept(p.getValue());
                }

                return;
            }
        }
        
        TileEntitySkull.resolveTexture(existingProfile, profile -> {
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

        PropertyMap properties = profile.getProperties();

        if(properties.isNull() || !properties.containsKey("textures"))
            return null;

        List<Property> textures = properties.get("textures");

        if(cacheProfile && textures.size() > 0) {
            MinecraftServer.getServer().getUserCache().addProfile(profile);
        }

        return (textures.size() > 0 ? textures.get(0).getValue() : null);
    }

}
