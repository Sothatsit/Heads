package net.sothatsit.heads.volatilecode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.SafeCall;
import net.sothatsit.heads.volatilecode.reflection.authlib.GameProfile;
import net.sothatsit.heads.volatilecode.reflection.authlib.Property;
import net.sothatsit.heads.volatilecode.reflection.authlib.PropertyMap;
import net.sothatsit.heads.volatilecode.reflection.nms.MinecraftServer;
import net.sothatsit.heads.volatilecode.reflection.nms.TileEntitySkull;

import java.util.Collection;
import java.util.function.Consumer;

public class TextureGetter {
    
    public String getCachedTexture(String name) {
        GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(name);
        
        if (!profile.isNull() && profile.isComplete() && profile.getProperties() != null && profile.getProperties().containsKey("textures")) {
            for (Property p : profile.getProperties().get("textures")) {
                return p.getValue();
            }
        }
        
        return null;
    }
    
    public void getTexture(String name, Consumer<String> callback) {
        Checks.ensureNonNull(name, "name");
        Checks.ensureNonNull(callback, "callback");

        Consumer<String> safeCallback = SafeCall.consumer("callback", callback);

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
            Heads.sync(() -> {
                if(profile.isNull() || !profile.isComplete()) {
                    safeCallback.accept(null);
                    return;
                }

                PropertyMap properties = profile.getProperties();

                if(properties.isNull() || !properties.containsKey("textures")) {
                    safeCallback.accept(null);
                    return;
                }

                Collection<Property> textures = properties.get("textures");

                for (Property p : textures) {
                    safeCallback.accept(p.getValue());
                }

                if(textures.size() > 0) {
                    MinecraftServer.getServer().getUserCache().addProfile(profile);
                }
            });

            return true;
        });
    }
}
