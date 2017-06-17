package net.sothatsit.heads.config.cache;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import net.sothatsit.heads.config.FileConfigFile;
import net.sothatsit.heads.util.Clock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.ConfigFile;
import net.sothatsit.heads.config.DefaultsConfigFile;

public class CacheConfig {
    
    private final boolean logInfo;
    private final ConfigFile configFile;

    private Map<String, List<CachedHead>> heads = new HashMap<>();
    private Map<String, String> categoryNames = new HashMap<>();
    private Set<String> addons = new HashSet<>();

    private boolean dirty = false;
    
    public CacheConfig(boolean logInfo, ConfigFile configFile) {
        this.logInfo = logInfo;
        this.configFile = configFile;

        reload(false);
    }

    public CacheConfig(File file, Set<CachedHead> heads) {
        this.logInfo = false;
        this.configFile = new FileConfigFile(file);

        heads.forEach(head -> add(head, false));

        save();
    }

    public int getTotalHeads() {
        int total = 0;

        for(List<CachedHead> category : heads.values()) {
            total += category.size();
        }

        return total;
    }
    
    public Map<String, List<CachedHead>> getHeads() {
        return this.heads;
    }

    public List<CachedHead> getAllHeads() {
        List<CachedHead> allHeads = new ArrayList<>();

        heads.values().forEach(allHeads::addAll);

        return allHeads;
    }

    public List<CachedHead> getHeads(String category) {
        return this.heads.get(category.toLowerCase());
    }
    
    public CachedHead getHead(int id) {
        for (List<CachedHead> list : this.heads.values()) {
            for (CachedHead head : list) {
                if (head.getId() == id) {
                    return head;
                }
            }
        }
        return null;
    }
    
    public Collection<String> getCategories() {
        return this.categoryNames.values();
    }

    private String getCategoryName(String category) {
        return this.categoryNames.getOrDefault(category.toLowerCase(), category);
    }

    private List<CachedHead> getOrCreateCategory(String category) {
        String lowercase = category.toLowerCase();

        if(!this.heads.containsKey(lowercase)) {
            List<CachedHead> headsInCategory = new ArrayList<>();

            this.heads.put(lowercase, headsInCategory);
            this.categoryNames.put(lowercase, category);

            return headsInCategory;
        }

        return this.heads.get(lowercase);
    }

    public void reload() {
        reload(true);
    }

    public void reload(boolean autoSave) {
        Clock timer = Clock.start();

        this.configFile.saveDefaults();
        this.configFile.reload();

        FileConfiguration config = this.configFile.getConfig();

        this.addons = new HashSet<>(config.getStringList("addons"));
        
        this.clearHeads();

        // Load all the heads from the config
        int totalHeads = 0;
        int maxId = 0;
        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key))
                continue;
            
            CachedHead head = new CachedHead();

            head.load(config.getConfigurationSection(key));

            if(!head.isValid())
                continue;

            getOrCreateCategory(head.getCategory()).add(head);

            ++totalHeads;

            if(head.hasId()) {
                maxId = Math.max(maxId, head.getId());
            }
        }

        heads.values().forEach(Collections::sort);

        // Give IDs to heads that need them
        boolean shouldSave = false;
        for(CachedHead head : getAllHeads()) {
            if(!head.hasId()) {
                head.setId(++maxId);
                shouldSave = true;
            }
        }
        
        info("Loaded " + heads.size() + " Head Categories with " + totalHeads + " Total Heads " + timer);

        if(shouldSave) {
            if(autoSave) {
                save();
            } else {
                dirty = true;
            }
        }
    }
    
    public void installAddons() {
        Clock timer = Clock.start();

        AtomicInteger installed = new AtomicInteger(0);

        checkAddon("twitch", "Twitch", "addons/twitch-addon.yml", installed);
        checkAddon("easter", "Easter", "addons/easter-addon.yml", installed);
        checkAddon("christmas", "Christmas", "addons/christmas-addon.yml", installed);
        checkAddon("halloween", "Halloween", "addons/halloween-addon.yml", installed);
        checkAddon("animals", "Animals", "addons/animals-addon.yml", installed);
        checkAddon("lol", "League of Legends", "addons/lol-addon.yml", installed);
        checkAddon("humans", "Humans", "addons/humans-addon.yml", installed);

        int miscAddonsEnabled = 21;

        for(int i = 1; i <= miscAddonsEnabled; i++) {
            checkAddon("misc" + i, "Miscellaneous " + i, "addons/misc-addon-" + i + ".yml", installed);
        }
        
        if (installed.get() > 0) {
            info("Installed " + installed + " Addons " + timer);
        }
    }
    
    private void checkAddon(String id, String name, String file, AtomicInteger installed) {
        if (addons.contains(id))
            return;

        CacheConfig addon = new CacheConfig(false, new DefaultsConfigFile(file));

        addon.getAllHeads().forEach(head -> add(head, false));

        addons.add(id);

        installed.set(installed.get() + 1);
    }

    public void saveIfRequired() {
        if(dirty) {
            save();
        }
    }
    
    public void save() {
        Clock timer = Clock.start();

        configFile.clear();

        FileConfiguration config = configFile.getConfig();

        config.set("addons", new ArrayList<>(addons));
        
        int index = 0;
        for(CachedHead head : getAllHeads()) {
            String key = "Head-" + (++index);

            ConfigurationSection section = config.createSection(key);

            head.save(section);
        }
        
        configFile.save();

        dirty = false;

        info("Saved " + heads.size() + " Head Categories with " + index + " Total Heads " + timer);
    }

    public void add(CachedHead head) {
        add(head, true);
    }

    public void add(CachedHead head, boolean autoSave) {
        String category = this.getCategoryName(head.getCategory());

        head.setCategory(category);

        List<CachedHead> headsInCategory = this.getOrCreateCategory(category);

        headsInCategory.add(head);
        Collections.sort(headsInCategory);

        int max = 0;

        for (List<CachedHead> list : this.heads.values()) {
            for (CachedHead h : list) {
                max = Math.max(max, h.getId());
            }
        }

        head.setId(max + 1);

        if (autoSave) {
            save();
        } else {
            dirty = true;
        }
    }

    public void remove(CachedHead head) {
        String category = head.getCategory().toLowerCase();

        List<CachedHead> headsInCategory = heads.get(category);

        if (headsInCategory != null) {
            headsInCategory.remove(head);

            if (headsInCategory.size() == 0) {
                heads.remove(category.toLowerCase());
                categoryNames.remove(category.toLowerCase());
            }

            save();
        }
    }

    public void clearHeads() {
        this.heads.clear();
        this.categoryNames.clear();
    }

    private void info(String message) {
        if (logInfo) {
            Heads.info(message);
        }
    }

}
