package net.sothatsit.heads.config.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.AbstractConfig;
import net.sothatsit.heads.config.ConfigFile;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

public class LangConfig extends AbstractConfig {
    
    private ConfigFile configFile;
    private Map<String, LangMessage> defaults;
    private Map<String, LangMessage> messages;
    
    public LangConfig(ConfigFile configFile) {
        this.configFile = configFile;
        
        reload();
    }
    
    public ConfigFile getConfigFile() {
        return configFile;
    }
    
    public void reload() {
        Heads.info("Loading Lang File...");
        long start = System.currentTimeMillis();
        
        configFile.saveDefaults();
        configFile.reload();
        
        FileConfiguration defaultConfig = configFile.loadDefaults();
        
        this.defaults = load(defaultConfig);
        
        FileConfiguration config = configFile.getConfig();
        
        this.messages = load(config);
        
        boolean save = false;
        for (Entry<String, LangMessage> def : defaults.entrySet()) {
            if (!messages.containsKey(def.getKey())) {
                Heads.warning("\"lang.yml\" is missing key \"" + def.getKey() + "\", adding it");
                
                config.set(def.getKey(), def.getValue().getConfigValue());
                messages.put(def.getKey(), def.getValue());
                save = true;
            }
        }
        
        if (save) {
            configFile.save();
        }
        
        Heads.info("Loaded Lang File with " + messages.size() + " messages " + getTime(start));
    }
    
    public String getTime(long start) {
        return "(" + (System.currentTimeMillis() - start) + " ms)";
    }
    
    public Map<String, LangMessage> load(MemorySection sec) {
        Map<String, LangMessage> map = new HashMap<>();
        
        for (String key : sec.getKeys(false)) {
            if (sec.isConfigurationSection(key)) {
                map.putAll(load((MemorySection) sec.getConfigurationSection(key)));
            } else if (sec.isList(key)) {
                List<String> list = sec.getStringList(key);
                
                if (list != null) {
                    String secKey = sec.getCurrentPath();
                    String trueKey = (secKey.isEmpty() ? key : secKey + "." + key);
                    map.put(trueKey, new LangMessage(list.toArray(new String[0])));
                }
            } else {
                String str = sec.getString(key);
                
                if (str != null) {
                    String secKey = sec.getCurrentPath();
                    String trueKey = (secKey.isEmpty() ? key : secKey + "." + key);
                    map.put(trueKey, new LangMessage(str.isEmpty() ? new String[0] : new String[] { str }));
                }
            }
        }
        
        return map;
    }
    
    public LangMessage getMessage(String key) {
        LangMessage message = messages.get(key);
        return (message == null ? defaults.get(key) : message);
    }
}
