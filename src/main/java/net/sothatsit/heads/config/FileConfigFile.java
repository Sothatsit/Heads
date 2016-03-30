package net.sothatsit.heads.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sothatsit.heads.Heads;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileConfigFile implements ConfigFile {
    
    private File file;
    private FileConfiguration config;
    private long lastEdit;
    
    public FileConfigFile(File file) {
        this.file = file;
        this.lastEdit = -1;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public void save() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        lastEdit = file.lastModified();
    }
    
    public boolean shouldReload() {
        return !file.exists() || lastEdit == -1 || file.lastModified() != lastEdit;
    }
    
    public void saveDefaults() {
        if (!file.exists()) {
            Heads.getInstance().saveResource(file.getName(), false);
        }
    }
    
    public FileConfiguration loadDefaults() {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(Heads.getInstance().getResource(file.getName())));
    }
    
}
