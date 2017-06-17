package net.sothatsit.heads;

import net.sothatsit.heads.command.HeadsCommand;
import net.sothatsit.heads.command.RuntimeCommand;
import net.sothatsit.heads.config.FileConfigFile;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.cache.CacheConfig;
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
import java.util.Arrays;
import java.util.Map;

public class Heads extends JavaPlugin implements Listener {

    private static Heads instance;
    private CacheConfig cacheConfig;
    private MenuConfig menuConfig;
    private MainConfig mainConfig;
    private LangConfig langConfig;
    private TextureGetter textureGetter;
    private boolean commandsRegistered = false;
    
    @Override
    public void onEnable() {
        instance = this;

        Clock timer = Clock.start();

        this.cacheConfig = new CacheConfig(true, new FileConfigFile(new File(getDataFolder(), "cache.yml")));
        this.cacheConfig.installAddons();
        this.cacheConfig.saveIfRequired();

        this.menuConfig = new MenuConfig(new FileConfigFile(new File(getDataFolder(), "menus.yml")));
        this.langConfig = new LangConfig(new FileConfigFile(new File(getDataFolder(), "lang.yml")));
        this.mainConfig = new MainConfig(new FileConfigFile(new File(getDataFolder(), "config.yml")));
        this.textureGetter = new TextureGetter();

        ProtocolHackFixer.fix();

        registerCommands();
        hookPlugins();

        Bukkit.getPluginManager().registerEvents(this, this);

        info("Heads Plugin Enabled with " + cacheConfig.getTotalHeads() + " heads " + timer);
    }

    @Override
    public void onDisable() {
        instance = null;

        unregisterCommands();
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
            severe("Unable to hook Vault Economy and economy is enabled in config");
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
        cacheConfig.reload();
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

    public static Heads getInstance() {
        return instance;
    }
    
    public static MainConfig getMainConfig() {
        return instance.mainConfig;
    }
    
    public static boolean isHatMode() {
        return instance.mainConfig.isHatMode();
    }
    
    public static CacheConfig getCacheConfig() {
        return instance.cacheConfig;
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