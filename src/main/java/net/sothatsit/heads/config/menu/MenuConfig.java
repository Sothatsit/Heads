package net.sothatsit.heads.config.menu;

import java.util.HashMap;
import java.util.Map;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.ConfigFile;

import net.sothatsit.heads.util.Clock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class MenuConfig {
    
    private final ConfigurationSection defaults;
    private final ConfigFile configFile;
    private final Map<String, Menu> menus;
    private final Map<String, Menu> defaultMenus;
    
    public MenuConfig(ConfigFile configFile) {
        this.menus = new HashMap<>();
        this.defaultMenus = new HashMap<>();

        this.configFile = configFile;
        this.defaults = loadDefaults();

        reload();
    }

    public Menu getMenu(String name) {
        Menu menu = menus.get(name.toLowerCase());

        return (menu != null ? menu : defaultMenus.get(name.toLowerCase()));
    }
    
    public void reload() {
        Clock timer = Clock.start();
        
        configFile.saveDefaults();
        configFile.reload();
        
        FileConfiguration config = configFile.getConfig();

        menus.clear();

        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key)) {
                Heads.warning("Unknown use of value \"" + key + "\" in the menu config");
                continue;
            }

            Menu defaultMenu = defaultMenus.get(key.toLowerCase());
            Menu menu = Menu.loadMenu(config.getConfigurationSection(key), defaultMenu);
            
            menus.put(key.toLowerCase(), menu);
        }
        
        boolean save = false;
        
        for (String key : defaultMenus.keySet()) {
            if(menus.containsKey(key))
                continue;

            config.set(key, defaults.getConfigurationSection(key));

            Heads.warning("\"" + key + "\" was missing in the menu config, creating it");
            save = true;
        }
        
        if (save) {
            configFile.save();
        }
        
        Heads.info("Loaded Menu Config with " + menus.size() + " Menus " + timer);
    }

    private ConfigurationSection loadDefaults() {
        ConfigurationSection config = configFile.loadDefaults();

        defaultMenus.clear();

        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key))
                continue;

            defaultMenus.put(key.toLowerCase(), Menu.loadMenu(config.getConfigurationSection(key)));
        }

        return config;
    }

}
