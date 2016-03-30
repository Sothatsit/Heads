package net.sothatsit.heads.config;

import java.io.InputStreamReader;

import net.sothatsit.heads.Heads;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DefaultsConfigFile implements ConfigFile {
    
    private String name;
    private FileConfiguration config;
    
    public DefaultsConfigFile(String name) {
        this.name = name;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public void save() {
        
    }
    
    public void reload() {
        config = loadDefaults();
    }
    
    public boolean shouldReload() {
        return false;
    }
    
    public void saveDefaults() {
        
    }
    
    public FileConfiguration loadDefaults() {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(Heads.getInstance().getResource(name)));
    }
    
}
