package net.sothatsit.heads;

import net.sothatsit.heads.cache.ModsFile;
import net.sothatsit.heads.cache.ModsFileHeader;
import net.sothatsit.heads.cache.CacheFile;
import net.sothatsit.heads.cache.legacy.CacheFileConverter;
import net.sothatsit.heads.command.HeadsCommand;
import net.sothatsit.heads.command.RuntimeCommand;
import net.sothatsit.heads.config.FileConfigFile;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.cache.legacy.LegacyCacheConfig;
import net.sothatsit.heads.config.lang.LangConfig;
import net.sothatsit.heads.config.menu.MenuConfig;
import net.sothatsit.heads.menu.ui.InventoryMenu;
import net.sothatsit.heads.oldmenu.ClickInventory;
import net.sothatsit.heads.util.Clock;
import net.sothatsit.heads.volatilecode.TextureGetter;
import net.sothatsit.heads.volatilecode.injection.ProtocolHackFixer;
import net.sothatsit.heads.volatilecode.reflection.craftbukkit.CommandMap;
import net.sothatsit.heads.volatilecode.reflection.craftbukkit.CraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

public class Heads extends JavaPlugin implements Listener {

    private static Heads instance;
    private CacheFile cache;
    private MenuConfig menuConfig;
    private MainConfig mainConfig;
    private LangConfig langConfig;
    private TextureGetter textureGetter;
    private boolean commandsRegistered = false;
    private boolean blockStoreAvailable = false;
    
    @Override
    public void onEnable() {
        instance = this;

        Clock timer = Clock.start();

        loadCache();

        this.menuConfig = new MenuConfig(new FileConfigFile(new File(getDataFolder(), "menus.yml")));
        this.langConfig = new LangConfig(new FileConfigFile(new File(getDataFolder(), "lang.yml")));
        this.mainConfig = new MainConfig(new FileConfigFile(new File(getDataFolder(), "config.yml")));
        this.textureGetter = new TextureGetter();

        ProtocolHackFixer.fix();

        registerCommands();
        hookPlugins();

        HeadNamer headNamer = new HeadNamer();
        headNamer.registerEvents();

        Bukkit.getPluginManager().registerEvents(this, this);

        checkForUpdates();

        info("Heads plugin enabled with " + cache.getHeadCount() + " heads " + timer);
    }

    @Override
    public void onDisable() {
        instance = null;

        unregisterCommands();
    }

    private void checkForUpdates() {
        if(!mainConfig.shouldCheckForUpdates())
            return;

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                String currentVersion = UpdateChecker.getCurrentVersion();
                String latestVersion = UpdateChecker.getLatestVersion();

                if(!UpdateChecker.isNewerVersion(latestVersion))
                    return;

                warning("A newer version of Heads, Heads v" + latestVersion + ", is available for download");
                warning("You are currently using Heads v" + currentVersion);
            } catch(IOException e) {
                severe("There was an error checking for an update for Heads");
            }
        });
    }

    public File getCacheFile() {
        if(!getDataFolder().exists() && !getDataFolder().mkdirs())
            throw new RuntimeException("Unable to create the data folder to save plugin files");

        if(!getDataFolder().isDirectory())
            throw new RuntimeException("plugins/Heads should be a directory, yet there is a file with the same name");

        return new File(getDataFolder(), "heads.cache");
    }

    private CacheFile loadCache() {
        File file = getCacheFile();
        File legacyConfigFile = new File(getDataFolder(), "cache.yml");

        boolean requiresWrite = false;

        if(!file.exists()) {
            requiresWrite = true;

            if(legacyConfigFile.exists()) {
                Clock timer = Clock.start();

                FileConfigFile config = new FileConfigFile(legacyConfigFile);
                LegacyCacheConfig legacy = new LegacyCacheConfig(config);
                cache = CacheFileConverter.convertToCacheFile("main-cache", legacy);

                info("Converted legacy yaml cache file to new binary file " + timer);
            } else {
                cache = new CacheFile("main-cache");
            }
        } else {
            try {
                Clock timer = Clock.start();

                cache = CacheFile.read(file);

                info("Loaded cache file " + timer);
            } catch (IOException e) {
                severe("Unable to read heads.cache file");
                throw new RuntimeException("There was an exception reading the heads.cache file", e);
            }
        }

        if(installAddons() || requiresWrite) {
            saveCache();
        }

        if(legacyConfigFile.exists() && !legacyConfigFile.delete()) {
            severe("Unable to delete legacy yaml cache file");
        }

        return cache;
    }

    public void saveCache() {
        File file = getCacheFile();

        try {
            Clock timer = Clock.start();

            cache.write(file);

            info("Saved cache file " + timer);
        } catch (IOException e) {
            severe("Unable to save the cache to heads.cache");
            throw new RuntimeException("There was an exception saving the legacy", e);
        }
    }

    private ModsFileHeader readModsFileHeader() {
        try {
            return ModsFileHeader.readResource("cache.mods");
        } catch (IOException e) {
            severe("Unable to read header of cache.mods");
            throw new RuntimeException("Unable to read header of cache.mods", e);
        }
    }

    private ModsFile readModsFile() {
        try {
            return ModsFile.readResource("cache.mods");
        } catch (IOException e) {
            severe("Unable to read mods from cache.mods");
            throw new RuntimeException("Unable to read mods from cache.mods", e);
        }
    }

    private boolean installAddons() {
        Clock timer = Clock.start();

        ModsFileHeader header = readModsFileHeader();
        int newMods = header.getUninstalledMods(cache);

        if(newMods == 0)
            return false;

        ModsFile mods = readModsFile();

        int newHeads = mods.installMods(cache);

        if(newHeads > 0) {
            info("Added " + newHeads + " new heads from " + newMods + " addons " + timer);
        } else {
            info("Installed " + newMods + " addons " + timer);
        }

        return true;
    }

    private void hookPlugins() {
        if(mainConfig.isEconomyEnabled()) {
            boolean ecoHooked = false;

            try {
                if (EconomyHook.hookEconomy()) {
                    info("Hooked Vault Economy");
                    ecoHooked = true;
                }
            } catch(Exception exception) {
                warning("There was an error hooking Vault Economy");
                exception.printStackTrace();
            }

            if (!ecoHooked) {
                severe("Unable to hook Vault Economy and economy is enabled in the config.");
                severe("Users will not be able to purchase heads.");
            }
        }

        if (mainConfig.shouldUseBlockStore() && Bukkit.getPluginManager().getPlugin("BlockStore") != null) {
            blockStoreAvailable = false;

            try {
                Class<?> apiClass = Class.forName("net.sothatsit.blockstore.BlockStoreApi");

                apiClass.getDeclaredMethod("retrieveBlockMeta",
                        Plugin.class, Location.class, Plugin.class, String.class, Consumer.class);

                info("Hooked BlockStore");

                blockStoreAvailable = true;

            } catch (ClassNotFoundException | NoSuchMethodException e) {
                severe("Unable to hook BlockStore, the version of BlockStore you are " +
                        "using may be outdated. Heads requires BlockStore v1.5.0.");
                severe("Please update BlockStore and report this to Sothatsit if the problem persists.");
            }
        }
    }
    
    private void registerCommands() {
        if (commandsRegistered) {
            unregisterCommands();
        }

        SimpleCommandMap commandMap = CraftServer.get().getCommandMap();
        
        RuntimeCommand heads = new RuntimeCommand(mainConfig.getHeadCommand());
        heads.setExecutor(new HeadsCommand());
        heads.setDescription(mainConfig.getHeadDescription());
        heads.setAliases(Arrays.asList(mainConfig.getHeadAliases()));
        
        commandMap.register("heads", heads);
        
        commandsRegistered = true;
    }
    
    private void unregisterCommands() {
        SimpleCommandMap commandMap = CraftServer.get().getCommandMap();
        Map<String, Command> map = CommandMap.getCommandMap(commandMap);

        map.values().removeIf(command -> command instanceof RuntimeCommand);
        
        commandsRegistered = false;
    }

    public void reloadConfigs() {
        langConfig.reload();
        mainConfig.reload();

        registerCommands();
        hookPlugins();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        ItemStack item = e.getCurrentItem();

        if(inventory == null)
            return;

        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof ClickInventory) {
            ((ClickInventory) holder).onClick(e);
        } else if (holder instanceof InventoryMenu) {
            ((InventoryMenu) holder).onClick(e);
        }
    }

    public static boolean isHeadsItem(ItemStack item) {
        if(item == null || item.getType() != Material.SKULL_ITEM)
            return false;

        SkullMeta meta = (SkullMeta) item.getItemMeta();

        return meta.hasOwner() && meta.getOwner().equals("SpigotHeadPlugin");
    }

    public static String getCategoryPermission(String category) {
        return "heads.category." + category.toLowerCase().replace(' ', '_');
    }

    public static Heads getInstance() {
        return instance;
    }

    public static MainConfig getMainConfig() {
        return instance.mainConfig;
    }
    
    public static CacheFile getCache() {
        return instance.cache;
    }
    
    public static MenuConfig getMenuConfig() {
        return instance.menuConfig;
    }
    
    public static LangConfig getLangConfig() {
        return instance.langConfig;
    }
    
    public static TextureGetter getTextureGetter() {
        return instance.textureGetter;
    }

    public static boolean isBlockStoreAvailable() {
        return instance.blockStoreAvailable;
    }

    public static void info(String info) {
        instance.getLogger().info(info);
    }
    
    public static void warning(String warning) {
        instance.getLogger().warning(warning);
    }
    
    public static void severe(String severe) {
        instance.getLogger().severe(severe);
    }

    public static void sync(Runnable task) {
        Bukkit.getScheduler().runTask(instance, task);
    }

    public static void sync(Runnable task, int delay) {
        Bukkit.getScheduler().runTaskLater(instance, task, delay);
    }

    public static void async(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, task);
    }

}