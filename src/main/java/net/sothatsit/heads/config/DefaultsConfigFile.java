package net.sothatsit.heads.config;

import java.io.InputStreamReader;

import net.sothatsit.heads.Heads;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DefaultsConfigFile implements ConfigFile {
    
    private final String name;
    private YamlConfiguration config;
    
    public DefaultsConfigFile(String name) {
        this.name = name;
    }

    @Override
    public YamlConfiguration getConfig() {
        return config;
    }

    @Override
    public void save() {
        
    }

    @Override
    public void reload() {
        config = loadDefaults();
    }

    @Override
    public void clear() {
        config = new YamlConfiguration();
    }

    @Override
    public boolean shouldReload() {
        return false;
    }

    @Override
    public void saveDefaults() {
        
    }

    @Override
    public YamlConfiguration loadDefaults() {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(Heads.getInstance().getResource(name)));
    }
    
}
