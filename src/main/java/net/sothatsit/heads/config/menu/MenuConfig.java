package net.sothatsit.heads.config.menu;

import java.util.HashMap;
import java.util.Map;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.AbstractConfig;
import net.sothatsit.heads.config.ConfigFile;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

public class MenuConfig extends AbstractConfig {
    
    private FileConfiguration defaults;
    private ConfigFile configFile;
    private Map<String, Menu> menus;
    private Map<String, Menu> defaultMenus;
    
    public MenuConfig(ConfigFile configFile) {
        this.configFile = configFile;
        
        reloadDefaults();
        reload();
    }
    
    public ConfigFile getConfigFile() {
        return configFile;
    }
    
    public void reloadDefaults() {
        Heads.info("Loading Menu Defaults...");
        long start = System.currentTimeMillis();
        
        defaults = configFile.loadDefaults();
        
        defaultMenus = new HashMap<>();
        for (String key : defaults.getKeys(false)) {
            if (!defaults.isConfigurationSection(key)) {
                continue;
            }
            
            Menu menu = new Menu();
            
            menu.load((MemorySection) defaults.getConfigurationSection(key));
            
            defaultMenus.put(key.toLowerCase(), menu);
        }
        
        Heads.info("Loaded " + defaultMenus.size() + " Menu Defaults " + getTime(start));
    }
    
    public String getTime(long start) {
        return "(" + (System.currentTimeMillis() - start) + " ms)";
    }
    
    public void reload() {
        Heads.info("Loading Menus...");
        long start = System.currentTimeMillis();
        
        configFile.saveDefaults();
        configFile.reload();
        
        FileConfiguration config = configFile.getConfig();
        
        menus = new HashMap<>();
        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key)) {
                continue;
            }
            
            Menu menu = new Menu();
            
            menu.load((MemorySection) config.getConfigurationSection(key));
            
            Menu defaultMenu = defaultMenus.get(key.toLowerCase());
            
            if (defaultMenu != null) {
                defaultMenu.copyAsDefaults(menu);
            }
            
            menus.put(key.toLowerCase(), menu);
        }
        
        boolean save = false;
        
        for (String key : defaultMenus.keySet()) {
            if (menus.get(key) == null) {
                config.set(key, defaults.getConfigurationSection(key));
                save = true;
                Heads.warning("\"" + key + "\" was missing in config.yml, creating it");
            }
        }
        
        if (save) {
            configFile.save();
        }
        
        Heads.info("Loaded " + menus.size() + " Menus " + getTime(start));
    }
    
    public Menu getMenu(String name) {
        Menu menu = menus.get(name.toLowerCase());
        return (menu == null ? defaultMenus.get(name.toLowerCase()) : menu);
    }
}
