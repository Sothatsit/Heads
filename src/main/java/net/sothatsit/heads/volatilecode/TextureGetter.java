package net.sothatsit.heads.volatilecode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.util.Callback;
import net.sothatsit.heads.volatilecode.reflection.authlib.GameProfile;
import net.sothatsit.heads.volatilecode.reflection.authlib.Property;
import net.sothatsit.heads.volatilecode.reflection.nms.MinecraftServer;
import net.sothatsit.heads.volatilecode.reflection.nms.TileEntitySkull;

import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Predicate;

public class TextureGetter {
    
    public String getCachedTexture(String name) {
        GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(name);
        
        if (profile != null && profile.isComplete() && profile.getProperties() != null && profile.getProperties().containsKey("textures")) {
            for (Property p : profile.getProperties().get("textures")) {
                return p.getValue();
            }
        }
        
        return null;
    }
    
    public void getTexture(String name, final Callback<String> callback) {
        GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(name);
        
        if (profile == null) {
            profile = new GameProfile(null, name);
        } else if (profile.isComplete() && profile.getProperties() != null && profile.getProperties().containsKey("textures")) {
            for (Property p : profile.getProperties().get("textures")) {
                callback.call(p.getValue());
            }
        }
        
        TileEntitySkull.resolveTexture(profile, new Predicate<Object>() {
            @Override
            public boolean apply(final Object gameprofile) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        GameProfile profile = new GameProfile(gameprofile);
                        if (profile != null && profile.isComplete() && profile.getProperties().containsKey("textures")) {
                            for (Property p : profile.getProperties().get("textures")) {
                                callback.call(p.getValue());
                                
                                if (p.getValue() != null && !p.getValue().isEmpty()) {
                                    MinecraftServer.getServer().getUserCache().addProfile(profile);
                                }
                                return;
                            }
                        }
                        
                        callback.call(null);
                    }
                }.runTask(Heads.getInstance());
                return true;
            }
        });
    }
}
