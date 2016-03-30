package net.sothatsit.heads.config.menu;

import java.util.ArrayList;
import java.util.List;

import net.sothatsit.heads.Heads;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuItem {
    
    private int type;
    private int data;
    private String name;
    private String[] lore;
    
    public MenuItem(int type, int data, String name, String[] lore) {
        this.type = verifyType(type);
        this.data = data;
        this.name = name;
        this.lore = lore;
    }
    
    @SuppressWarnings("deprecation")
    public int verifyType(int type) {
        Material mat = Material.getMaterial(type);
        
        return (mat == null ? 35 : type);
    }
    
    @SuppressWarnings("deprecation")
    public ItemStack create(Placeholder... placeholders) {
        ItemStack item = new ItemStack(type, 1, (short) data);
        
        if (name != null || lore != null) {
            ItemMeta meta = item.getItemMeta();
            
            if (name != null) {
                meta.setDisplayName(apply(name, placeholders));
            }
            
            if (lore != null) {
                List<String> list = new ArrayList<>();
                
                boolean economy = Heads.getMainConfig().isEconomyEnabled();
                
                for (String str : lore) {
                    if (economy || !str.contains("%cost%")) {
                        list.add(apply(str, placeholders));
                    }
                }
                
                meta.setLore(list);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private String apply(String str, Placeholder... placeholders) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        
        for (Placeholder placeholder : placeholders) {
            str = placeholder.apply(str);
        }
        
        return str;
    }
    
}
