package net.sothatsit.heads.config;

import java.util.Arrays;

import net.sothatsit.heads.Heads;

import org.bukkit.configuration.file.FileConfiguration;

public class MainConfig extends AbstractConfig {
    
    private ConfigFile configFile;
    private double defaultHeadCost;
    private boolean economyEnabled;
    private boolean hatMode;
    
    private String headCommand;
    private String[] headAliases;
    private String headDescription;
    
    private String addCommand;
    private String handCommand;
    private String getCommand;
    private String giveCommand;
    private String randomCommand;
    private String removeCommand;
    private String renameCommand;
    private String costCommand;
    private String idCommand;
    private String searchCommand;
    
    public MainConfig(ConfigFile configFile) {
        this.configFile = configFile;
        
        reload();
    }
    
    public ConfigFile getConfigFile() {
        return configFile;
    }
    
    public void reload() {
        Heads.info("Loading Main Config...");
        long start = System.currentTimeMillis();
        
        configFile.saveDefaults();
        configFile.reload();
        
        FileConfiguration config = configFile.getConfig();
        
        boolean save = checkString(config, "commands.heads.sub-commands.add", "add", false);
        save = checkString(config, "commands.heads.sub-commands.hand", "hand", save);
        save = checkString(config, "commands.heads.sub-commands.get", "get", save);
        save = checkString(config, "commands.heads.sub-commands.give", "give", save);
        save = checkString(config, "commands.heads.sub-commands.random", "random", save);
        save = checkString(config, "commands.heads.sub-commands.remove", "remove", save);
        save = checkString(config, "commands.heads.sub-commands.rename", "rename", save);
        save = checkString(config, "commands.heads.sub-commands.cost", "cost", save);
        save = checkString(config, "commands.heads.sub-commands.id", "id", save);
        save = checkString(config, "commands.heads.sub-commands.search", "search", save);
        save = checkString(config, "commands.heads.label", "heads", save);
        save = checkString(config, "commands.heads.description", "Get a cool head", save);
        
        addCommand = config.getString("commands.heads.sub-commands.add");
        handCommand = config.getString("commands.heads.sub-commands.hand");
        getCommand = config.getString("commands.heads.sub-commands.get");
        giveCommand = config.getString("commands.heads.sub-commands.give");
        randomCommand = config.getString("commands.heads.sub-commands.random");
        removeCommand = config.getString("commands.heads.sub-commands.remove");
        renameCommand = config.getString("commands.heads.sub-commands.rename");
        costCommand = config.getString("commands.heads.sub-commands.cost");
        idCommand = config.getString("commands.heads.sub-commands.id");
        searchCommand = config.getString("commands.heads.sub-commands.search");
        headCommand = config.getString("commands.heads.label");
        headDescription = config.getString("commands.heads.description");
        
        if (!config.isSet("commands.heads.aliases") || !config.isList("commands.heads.aliases")) {
            Heads.warning("\"commands.heads.aliases\" not set or invalid in config.yml, resetting to \"head\"");
            config.set("commands.heads.aliases", Arrays.asList("head"));
        }
        
        headAliases = config.getStringList("commands.heads.aliases").toArray(new String[0]);
        
        if (!config.isSet("economy.default-head-cost") || (!config.isInt("economy.default-head-cost") && !config.isDouble("economy.default-head-cost"))) {
            Heads.warning("\"economy.default-head-cost\" not set or invalid in config.yml, defaulting to 0");
            
            config.set("economy.default-head-cost", 0);
            defaultHeadCost = 0;
            
            save = true;
        } else {
            defaultHeadCost = config.getDouble("economy.default-head-cost");
            
            if (defaultHeadCost < 0) {
                Heads.info("\"economy.default-head-cost\" cannot be less than 0 in config.yml, defaulting to 0");
                defaultHeadCost = 0;
            }
        }
        
        if (!config.isSet("economy.enabled") || !config.isBoolean("economy.enabled")) {
            Heads.warning("\"economy.enabled\" not set or invalid in config.yml, defaulting to False");
            
            config.set("economy.enabled", false);
            economyEnabled = false;
            
            save = true;
        } else {
            economyEnabled = config.getBoolean("economy.enabled");
        }
        
        if (!config.isSet("hat-mode") || !config.isBoolean("hat-mode")) {
            Heads.warning("\"hat-mode\" not set or invalid in config.yml, defaulting to False");
            
            config.set("hat-mode", false);
            hatMode = false;
            
            save = true;
        } else {
            hatMode = config.getBoolean("hat-mode");
        }
        
        if (save) {
            configFile.save();
        }
        
        Heads.info("Loaded Main Config " + getTime(start));
        
        Heads.getInstance().registerCommands();
    }
    
    public boolean checkString(FileConfiguration config, String key, String defaultVal, boolean save) {
        if (!config.isSet(key) || !config.isString(key) || config.getString(key).isEmpty()) {
            Heads.warning("\"key\" not set or invalid in config, resetting to \"" + defaultVal + "\"");
            config.set(key, defaultVal);
            return true;
        }
        return save;
    }
    
    public String getTime(long start) {
        return "(" + (System.currentTimeMillis() - start) + " ms)";
    }
    
    public boolean isEconomyEnabled() {
        return economyEnabled;
    }
    
    public double getDefaultHeadCost() {
        return defaultHeadCost;
    }
    
    public boolean isHatMode() {
        return hatMode;
    }
    
    public String getHeadCommand() {
        return headCommand;
    }
    
    public String[] getHeadAliases() {
        return headAliases;
    }
    
    public String getHeadDescription() {
        return headDescription;
    }
    
    public String getAddCommand() {
        return addCommand;
    }
    
    public String getHandCommand() {
        return handCommand;
    }
    
    public String getGetCommand() {
        return getCommand;
    }
    
    public String getGiveCommand() {
        return giveCommand;
    }
    
    public String getRandomCommand() {
        return randomCommand;
    }
    
    public String getRemoveCommand() {
        return removeCommand;
    }
    
    public String getRenameCommand() {
        return renameCommand;
    }
    
    public String getCostCommand() {
        return costCommand;
    }
    
    public String getIdCommand() {
        return idCommand;
    }

    public String getSearchCommand() {
        return searchCommand;
    }
}
