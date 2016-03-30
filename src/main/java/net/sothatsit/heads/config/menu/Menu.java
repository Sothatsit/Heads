package net.sothatsit.heads.config.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sothatsit.heads.Heads;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

public class Menu {
    
    private String name;
    private Map<String, MenuItem> items = new HashMap<>();
    private Map<String, MenuItem> defaults = new HashMap<>();
    
    public void load(MemorySection sec) {
        for (String key : sec.getKeys(false)) {
            if (!sec.isConfigurationSection(key)) {
                if (key.equalsIgnoreCase("title")) {
                    name = sec.getString(key, "Menu");
                    continue;
                }
                
                Heads.warning("Unknown use of value \"" + key + "\" in menu \"" + sec.getCurrentPath() + "\"");
                continue;
            }
            
            ConfigurationSection section = sec.getConfigurationSection(key);
            
            if (!section.isSet("type") || !section.isInt("type")) {
                Heads.warning("Invalid \"type\" of item \"" + key + "\" in menu \"" + sec.getCurrentPath() + "\", expected integer");
                continue;
            }
            
            int type = section.getInt("type");
            int data = section.getInt("data", 0);
            String name = section.getString("name", null);
            
            List<String> loreList = section.getStringList("lore");
            String[] lore = (loreList == null || loreList.size() == 0 ? null : loreList.toArray(new String[0]));
            
            addItem(key, new MenuItem(type, data, name, lore));
        }
    }
    
    public String getName(Placeholder... placeholders) {
        String str = new String(name);
        
        for (Placeholder placeholder : placeholders) {
            str = placeholder.apply(str);
        }
        
        return str;
    }
    
    public void addItem(String name, MenuItem item) {
        items.put(name.toLowerCase(), item);
    }
    
    public void addDefaultItem(String name, MenuItem item) {
        defaults.put(name.toLowerCase(), item);
    }
    
    public MenuItem getItem(String name) {
        MenuItem item = items.get(name.toLowerCase());
        return (item == null ? getDefaultItem(name) : item);
    }
    
    public MenuItem getDefaultItem(String name) {
        return items.get(name.toLowerCase());
    }
    
    public ItemStack getItemStack(String name, Placeholder... placeholders) {
        MenuItem menu = getItem(name);
        return (menu == null ? null : menu.create(placeholders));
    }
    
    public void copyAsDefaults(Menu other) {
        other.defaults = this.items;
    }
    
}
