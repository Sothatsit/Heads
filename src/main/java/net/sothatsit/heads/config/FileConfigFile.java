package net.sothatsit.heads.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sothatsit.heads.Heads;

import org.bukkit.configuration.file.YamlConfiguration;

public class FileConfigFile implements ConfigFile {
    
    private File file;
    private YamlConfiguration config;
    private long lastModified;
    
    public FileConfigFile(File file) {
        this.file = file;
        this.lastModified = -1;
    }

    @Override
    public YamlConfiguration getConfig() {
        return config;
    }

    @Override
    public void save() {
        try {
            if(!file.exists() && !file.createNewFile())
                throw new IOException("Unable to create config file " + file);

            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        lastModified = file.lastModified();
    }

    @Override
    public void clear() {
        config = new YamlConfiguration();
    }

    @Override
    public boolean shouldReload() {
        return !file.exists() || lastModified == -1 || file.lastModified() != lastModified;
    }

    @Override
    public void saveDefaults() {
        if(file.exists())
            return;

        Heads.getInstance().saveResource(file.getName(), false);
    }

    @Override
    public YamlConfiguration loadDefaults() {
        InputStreamReader defaultsStream = new InputStreamReader(Heads.getInstance().getResource(file.getName()));

        return YamlConfiguration.loadConfiguration(defaultsStream);
    }
    
}
