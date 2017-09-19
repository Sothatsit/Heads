package net.sothatsit.heads;

import net.sothatsit.heads.volatilecode.reflection.authlib.GameProfile;
import net.sothatsit.heads.volatilecode.reflection.authlib.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DBRipper {

    public static void rip() throws Exception {
        Plugin pl = Bukkit.getPluginManager().getPlugin("HeadDatabase");

        if(pl != null) {
            List<ItemStack> items = new ArrayList<>();

            items.addAll(getList(pl, "food"));
            items.addAll(getList(pl, "blocks"));
            items.addAll(getList(pl, "decoration"));
            items.addAll(getList(pl, "mobs"));
            items.addAll(getList(pl, "alphabet"));
            items.addAll(getList(pl, "colors"));
            items.addAll(getList(pl, "characters"));
            items.addAll(getList(pl, "devices"));

            loop: for(ItemStack item : items) {
                SkullMeta meta = (SkullMeta) item.getItemMeta();

                Field field = meta.getClass().getDeclaredField("profile");
                field.setAccessible(true);

                GameProfile profile = new GameProfile(field.get(meta));

                String texture = null;

                for(Property property : profile.getProperties().get("textures")) {
                    texture = property.getValue();
                }

                if(texture != null) {
                    for(List<CachedHead> list : Heads.getCacheConfig().getHeads().values()) {
                        for(CachedHead head : list) {
                            if(head.getTexture().equals(texture)) {
                                continue loop;
                            }
                        }
                    }

                    CachedHead head = new CachedHead(-1, "HeadDB", ChatColor.stripColor(meta.getDisplayName()), texture);

                    Heads.getCacheConfig().add(head, false);
                }
            }

            Heads.getCacheConfig().save();
        }
    }

    private static List<ItemStack> getList(Plugin pl, String field) throws Exception {
        Field f = pl.getClass().getDeclaredField(field);

        f.setAccessible(true);

        return (List<ItemStack>) f.get(pl);
    }

}
