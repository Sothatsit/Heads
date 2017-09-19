package net.sothatsit.heads.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sothatsit.heads.Heads;

import net.sothatsit.heads.util.Clock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class MainConfig {
    
    private final ConfigFile configFile;

    private double defaultHeadCost;
    private boolean economyEnabled;
    private boolean hatMode;
    private boolean hideNoPermCategories;

    private Map<String, Double> categoryCosts;
    
    private String headLabel;
    private String[] headAliases;
    private String headDescription;

    private String reloadLabel;
    private String addLabel;
    private String handLabel;
    private String getLabel;
    private String giveLabel;
    private String randomLabel;
    private String removeLabel;
    private String renameLabel;
    private String costLabel;
    private String categoryCostLabel;
    private String idLabel;
    private String searchLabel;
    
    public MainConfig(ConfigFile configFile) {
        this.configFile = configFile;
        
        reload();
    }

    public void reload() {
        Clock timer = Clock.start();

        configFile.saveDefaults();
        configFile.reload();
        
        FileConfiguration config = configFile.getConfig();

        AtomicBoolean shouldSave = new AtomicBoolean(false);

        loadCommandInfo(config, shouldSave);
        loadCategoryCosts(config, shouldSave);

        economyEnabled       = loadBoolean(config, "economy.enabled", false, shouldSave);
        hatMode              = loadBoolean(config, "hat-mode", false, shouldSave);
        hideNoPermCategories = loadBoolean(config, "hide-no-perm-categories", true, shouldSave);
        defaultHeadCost      = loadDouble(config, "economy.default-head-cost", 0, shouldSave);

        if (defaultHeadCost < 0) {
            Heads.info("\"economy.default-head-cost\" cannot be less than 0 in legacy.yml, defaulting to 0");
            defaultHeadCost = 0;
        }

        if (shouldSave.get()) {
            configFile.save();
        }
        
        Heads.info("Loaded Main Config " + timer);
    }

    private void loadCommandInfo(FileConfiguration config, AtomicBoolean shouldSave) {
        reloadLabel       = loadString(config, "commands.heads.sub-commands.reload", "reload", shouldSave);
        addLabel          = loadString(config, "commands.heads.sub-commands.add", "add", shouldSave);
        handLabel         = loadString(config, "commands.heads.sub-commands.hand", "hand", shouldSave);
        getLabel          = loadString(config, "commands.heads.sub-commands.get", "get", shouldSave);
        giveLabel         = loadString(config, "commands.heads.sub-commands.give", "give", shouldSave);
        randomLabel       = loadString(config, "commands.heads.sub-commands.random", "random", shouldSave);
        removeLabel       = loadString(config, "commands.heads.sub-commands.remove", "remove", shouldSave);
        renameLabel       = loadString(config, "commands.heads.sub-commands.rename", "rename", shouldSave);
        costLabel         = loadString(config, "commands.heads.sub-commands.cost", "cost", shouldSave);
        categoryCostLabel = loadString(config, "commands.heads.sub-commands.category-cost", "categorycost", shouldSave);
        idLabel           = loadString(config, "commands.heads.sub-commands.id", "id", shouldSave);
        searchLabel       = loadString(config, "commands.heads.sub-commands.search", "search", shouldSave);

        headLabel         = loadString(config, "commands.heads.label", "heads", shouldSave);
        headDescription   = loadString(config, "commands.heads.description", "Get a cool head", shouldSave);
        headAliases       = loadStringArray(config, "commands.heads.aliases", new String[] {"head"}, shouldSave);
    }

    private void loadCategoryCosts(FileConfiguration config, AtomicBoolean shouldSave) {
        categoryCosts = new HashMap<>();

        if(!config.isSet("economy.categories") || !config.isConfigurationSection("economy.categories"))
            return;

        ConfigurationSection categories = config.getConfigurationSection("economy.categories");

        for(String key : categories.getKeys(false)) {
            double cost = categories.getDouble(key, -1);

            if(cost < 0)
                continue;

            categoryCosts.put(key.toLowerCase(), cost);
        }
    }
    
    private String loadString(FileConfiguration config, String key, String defaultVal, AtomicBoolean shouldSave) {
        if (config.isSet(key) && config.isString(key) && !config.getString(key).isEmpty())
            return config.getString(key);

        Heads.warning("\"" + key + "\" not set or invalid in legacy, resetting to \"" + defaultVal + "\"");

        config.set(key, defaultVal);
        shouldSave.set(true);

        return defaultVal;
    }

    private String[] loadStringArray(FileConfiguration config, String key, String[] defaultVal, AtomicBoolean shouldSave) {
        if(config.isSet(key) && config.isList(key))
            return config.getStringList(key).toArray(new String[0]);

        Heads.warning("\"" + key + "\" not set or invalid in legacy, resetting to " + Arrays.toString(defaultVal));

        config.set(key, Arrays.asList(defaultVal));
        shouldSave.set(true);

        return defaultVal;
    }

    private boolean loadBoolean(FileConfiguration config, String key, boolean defaultVal, AtomicBoolean shouldSave) {
        if(config.isSet(key) && config.isBoolean(key))
            return config.getBoolean(key);

        Heads.warning("\"" + key + "\" not set or invalid in legacy, resetting to " + defaultVal);

        config.set(key, defaultVal);
        shouldSave.set(true);

        return defaultVal;
    }

    private double loadDouble(FileConfiguration config, String key, double defaultVal, AtomicBoolean shouldSave) {
        if(config.isSet(key) && (config.isInt(key) || config.isDouble(key)))
            return config.getDouble(key);

        Heads.warning("\"" + key + "\" not set or invalid in legacy, resetting to " + defaultVal);

        config.set(key, defaultVal);
        shouldSave.set(true);

        return defaultVal;
    }
    
    public boolean isEconomyEnabled() {
        return economyEnabled;
    }

    private String getPlainCategoryName(String category) {
        return category.toLowerCase().replace(" ", "");
    }

    public boolean hasCategoryCost(String category) {
        return categoryCosts.containsKey(getPlainCategoryName(category));
    }

    public double getCategoryCost(String category) {
        return categoryCosts.getOrDefault(getPlainCategoryName(category), defaultHeadCost);
    }

    public void setCategoryCost(String category, double cost) {
        categoryCosts.put(getPlainCategoryName(category), cost);

        saveCategoryCosts();
    }

    public void removeCategoryCost(String category) {
        categoryCosts.remove(getPlainCategoryName(category));

        saveCategoryCosts();
    }

    private void saveCategoryCosts() {
        Clock timer = Clock.start();

        FileConfiguration config = this.configFile.getConfig();

        config.set("economy.categories", null);

        if(categoryCosts.size() > 0) {
            ConfigurationSection section = config.createSection("economy.categories");

            for(Map.Entry<String, Double> entry : categoryCosts.entrySet()) {
                section.set(entry.getKey(), entry.getValue());
            }
        }

        configFile.save();

        Heads.info("Saved Main Config " + timer);
    }

    public double getDefaultHeadCost() {
        return this.defaultHeadCost;
    }
    
    public boolean isHatMode() {
        return this.hatMode;
    }

    public boolean shouldHideNoPermCategories() {
        return this.hideNoPermCategories;
    }
    
    public String getHeadCommand() {
        return this.headLabel;
    }
    
    public String[] getHeadAliases() {
        return this.headAliases;
    }
    
    public String getHeadDescription() {
        return this.headDescription;
    }

    public String getReloadCommand() {
        return this.reloadLabel;
    }

    public String getAddCommand() {
        return this.addLabel;
    }
    
    public String getHandCommand() {
        return this.handLabel;
    }
    
    public String getGetCommand() {
        return this.getLabel;
    }
    
    public String getGiveCommand() {
        return this.giveLabel;
    }
    
    public String getRandomCommand() {
        return this.randomLabel;
    }
    
    public String getRemoveCommand() {
        return this.removeLabel;
    }
    
    public String getRenameCommand() {
        return this.renameLabel;
    }
    
    public String getCostCommand() {
        return this.costLabel;
    }

    public String getCategoryCostCommand() {
        return this.categoryCostLabel;
    }
    
    public String getIdCommand() {
        return this.idLabel;
    }

    public String getSearchCommand() {
        return this.searchLabel;
    }
}
