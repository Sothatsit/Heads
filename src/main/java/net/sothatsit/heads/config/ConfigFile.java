package net.sothatsit.heads.config;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigFile {
    
    public FileConfiguration getConfig();
    
    public void save();
    
    public void reload();
    
    public boolean shouldReload();
    
    public void saveDefaults();
    
    public FileConfiguration loadDefaults();
    
}
