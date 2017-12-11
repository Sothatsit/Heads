package net.sothatsit.heads.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sothatsit.heads.Heads;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileConfigFile extends ConfigFile {

    private YamlConfiguration config;
    private ConfigurationSection defaults;
    
    public FileConfigFile(String name) {
        super(name);
    }

    public File getFile() {
        return new File(Heads.getInstance().getDataFolder(), getName());
    }

    @Override
    public ConfigurationSection getConfig() {
        return config;
    }

    @Override
    public void save() {
        File file = getFile();

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
        config = YamlConfiguration.loadConfiguration(getFile());
    }

    @Override
    public void copyDefaults() {
        if(getFile().exists())
            return;

        Heads.getInstance().saveResource(getName(), false);
    }

    @Override
    public ConfigurationSection getDefaults() {
        if(defaults == null) {
            InputStream resource = Heads.getInstance().getResource(getName());
            InputStreamReader reader = new InputStreamReader(resource);
            defaults = YamlConfiguration.loadConfiguration(reader);
        }

        return defaults;
    }
    
}
