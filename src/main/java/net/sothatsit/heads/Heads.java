package net.sothatsit.heads;

import net.sothatsit.heads.cache.AddonsFile;
import net.sothatsit.heads.cache.AddonsFileHeader;
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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class Heads extends JavaPlugin implements Listener {

    private static Heads instance;
    private CacheFile cache;
    private MenuConfig menuConfig;
    private MainConfig mainConfig;
    private LangConfig langConfig;
    private TextureGetter textureGetter;
    private boolean commandsRegistered = false;
    
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

        Bukkit.getPluginManager().registerEvents(this, this);

        info("Heads plugin enabled with " + cache.getHeadCount() + " heads " + timer);
    }

    @Override
    public void onDisable() {
        instance = null;

        unregisterCommands();
    }

    private File getCacheFile() {
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

    private AddonsFileHeader readAddonsFileHeader() {
        try {
            return AddonsFileHeader.readResource("addons.caches");
        } catch (IOException e) {
            severe("Unable to read header of addons.cache");
            throw new RuntimeException("Unable to read header of addons.caches", e);
        }
    }

    private AddonsFile readAddonsFile() {
        try {
            return AddonsFile.readResource("addons.caches");
        } catch (IOException e) {
            severe("Unable to read addons from addons.cache");
            throw new RuntimeException("Unable to read addons from addons.cache", e);
        }
    }

    private boolean installAddons() {
        Clock timer = Clock.start();

        AddonsFileHeader header = readAddonsFileHeader();
        int newAddons = header.getUninstalledAddons(cache);

        if(newAddons <= 0)
            return false;

        AddonsFile addons = readAddonsFile();
        int newHeads = addons.installAddons(cache);

        info("Added " + newHeads + " new heads from " + newAddons + " addons " + timer);

        return true;
    }

    private void hookPlugins() {
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

        if (!ecoHooked && mainConfig.isEconomyEnabled()) {
            severe("Unable to hook Vault Economy and economy is enabled in legacy");
            severe("Users will not be able to purchase heads");
        }

        if (Bukkit.getPluginManager().getPlugin("BlockStore") != null) {
            try {
                new BlockStoreHook();
            } catch (Exception e) {
                e.printStackTrace();
                severe("Error hooking BlockStore, please report this error to the author.");
            }

            info("Hooked BlockStore");
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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        ItemStack item = e.getCurrentItem();

        if(e.getSlotType() == InventoryType.SlotType.ARMOR && isHatMode() && isHeadsItem(item)) {
            e.setCancelled(true);

            if(e.getWhoClicked() instanceof Player) {
                Player player = (Player) e.getWhoClicked();

                Bukkit.getScheduler().scheduleSyncDelayedTask(this, player::updateInventory, 1);
            }

            return;
        }

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
    
    public static boolean isHatMode() {
        return instance.mainConfig.isHatMode();
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