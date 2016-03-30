package net.sothatsit.heads.config.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.ConfigFile;
import net.sothatsit.heads.config.DefaultsConfigFile;

public class CacheConfig {
    
    private boolean log;
    private ConfigFile configFile;
    private Map<String, List<CachedHead>> heads;
    private List<String> addons;
    
    public CacheConfig(boolean log, ConfigFile configFile) {
        this.log = log;
        this.configFile = configFile;
        
        reload();
    }
    
    private void info(String message) {
        if (log) {
            Heads.info(message);
        }
    }
    
    public Map<String, List<CachedHead>> getHeads() {
        return heads;
    }
    
    public List<CachedHead> getHeads(String category) {
        return heads.get(category);
    }
    
    public CachedHead getHead(int id) {
        for (List<CachedHead> list : heads.values()) {
            for (CachedHead head : list) {
                if (head.getId() == id) {
                    return head;
                }
            }
        }
        return null;
    }
    
    public Set<String> getCategories() {
        return heads.keySet();
    }
    
    public void add(CachedHead head) {
        add(head, true);
    }
    
    public void add(CachedHead head, boolean save) {
        String category = null;
        
        for (String key : heads.keySet()) {
            if (key.equalsIgnoreCase(head.getCategory())) {
                category = key;
            }
        }
        
        if (category == null) {
            List<CachedHead> list = new ArrayList<>();
            
            list.add(head);
            
            heads.put(head.getCategory(), list);
        } else {
            head.setCategory(category);
            heads.get(category).add(head);
        }
        
        int max = 0;
        
        for (List<CachedHead> list : heads.values()) {
            for (CachedHead h : list) {
                if (h.hasId()) {
                    max = Math.max(max, h.getId());
                }
            }
        }
        
        head.setId(max + 1);
        
        if (save) {
            save();
        }
    }
    
    public void remove(CachedHead head) {
        List<CachedHead> list = heads.get(head.getCategory());
        
        if (list != null) {
            list.remove(head);
            
            if (list.size() == 0) {
                heads.remove(head.getCategory());
            }
        }
        
        save();
    }
    
    public void reload() {
        info("Loading Head Cache...");
        
        long start = System.currentTimeMillis();
        
        configFile.saveDefaults();
        configFile.reload();
        
        FileConfiguration config = configFile.getConfig();
        
        addons = new ArrayList<>(config.getStringList("addons"));
        
        heads = new HashMap<>();
        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key)) {
                continue;
            }
            
            CachedHead head = new CachedHead();
            head.load((MemorySection) config.getConfigurationSection(key));
            
            if (head.isValid()) {
                if (!heads.containsKey(head.getCategory())) {
                    heads.put(head.getCategory(), new ArrayList<CachedHead>());
                }
                
                heads.get(head.getCategory()).add(head);
            }
        }
        
        List<CachedHead> noId = new ArrayList<>();
        
        int total = 0;
        int max = 0;
        
        for (List<CachedHead> list : heads.values()) {
            total += list.size();
            
            for (CachedHead head : list) {
                if (head.hasId()) {
                    max = Math.max(max, head.getId());
                } else {
                    noId.add(head);
                }
            }
        }
        
        for (CachedHead head : noId) {
            max++;
            head.setId(max);
        }
        
        info("Loaded " + heads.size() + " Head Categories with " + total + " Total Heads " + getTime(start));
        
        if (noId.size() > 0) {
            save();
        }
    }
    
    public void checkAddons() {
        info("Checking for Head Addons...");
        long start = System.currentTimeMillis();
        
        configFile.saveDefaults();
        configFile.reload();
        
        FileConfiguration config = configFile.getConfig();
        
        if (!config.isSet("addons") || !config.isList("addons")) {
            config.set("addons", new ArrayList<String>());
        }
        
        AtomicInteger installed = new AtomicInteger(0);
        
        checkAddon("easter", "Easter", "addons/easter-addon.yml", installed);
        checkAddon("christmas", "Christmas", "addons/christmas-addon.yml", installed);
        checkAddon("halloween", "Halloween", "addons/halloween-addon.yml", installed);
        checkAddon("animals", "Animals", "addons/animals-addon.yml", installed);
        checkAddon("lol", "League of Legends", "addons/lol-addon.yml", installed);
        checkAddon("humans", "Humans", "addons/humans-addon.yml", installed);
        
        if (installed.get() == 0) {
            info("No new addons found " + getTime(start));
        } else {
            save();
            info("Loaded " + installed + " Addons " + getTime(start));
        }
    }
    
    public void checkAddon(String id, String name, String file, AtomicInteger installed) {
        if (!addons.contains(id)) {
            info("Installing " + name + " Addon...");
            long startChristmas = System.currentTimeMillis();
            
            CacheConfig addon = new CacheConfig(false, new DefaultsConfigFile(file));
            
            for (Entry<String, List<CachedHead>> category : addon.getHeads().entrySet()) {
                for (CachedHead head : category.getValue()) {
                    add(head, false);
                }
            }
            
            addons.add(id);
            
            info("Installed " + name + " Addon " + getTime(startChristmas));
            
            installed.set(installed.get() + 1);
        }
    }
    
    public String getTime(long start) {
        return "(" + (System.currentTimeMillis() - start) + " ms)";
    }
    
    public void save() {
        info("Saving Head Cache");
        long start = System.currentTimeMillis();
        
        configFile.saveDefaults();
        configFile.reload();
        
        FileConfiguration config = configFile.getConfig();
        
        for (String key : config.getKeys(false)) {
            config.set(key, null);
        }
        
        config.set("addons", addons);
        
        int index = 1;
        for (List<CachedHead> list : heads.values()) {
            for (CachedHead head : list) {
                String key = "Head-" + index;
                index++;
                
                ConfigurationSection section = config.createSection(key);
                
                head.save((MemorySection) section);
            }
        }
        
        configFile.save();
        
        info("Saved " + heads.size() + " Head Categories with " + (index - 1) + " Total Heads " + getTime(start));
    }
}
