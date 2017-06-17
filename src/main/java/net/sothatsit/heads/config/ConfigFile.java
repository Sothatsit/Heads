package net.sothatsit.heads.config;

import org.bukkit.configuration.file.YamlConfiguration;

public interface ConfigFile {
    
    public YamlConfiguration getConfig();
    
    public void save();
    
    public void reload();

    public void clear();
    
    public boolean shouldReload();
    
    public void saveDefaults();
    
    public YamlConfiguration loadDefaults();
    
}
