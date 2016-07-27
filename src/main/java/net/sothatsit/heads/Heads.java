package net.sothatsit.heads;

import net.sothatsit.heads.command.HeadsCommand;
import net.sothatsit.heads.command.RuntimeCommand;
import net.sothatsit.heads.config.FileConfigFile;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.cache.CacheConfig;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.lang.LangConfig;
import net.sothatsit.heads.config.menu.MenuConfig;
import net.sothatsit.heads.economy.Economy;
import net.sothatsit.heads.menu.ClickInventory;
import net.sothatsit.heads.volatilecode.TextureGetter;
import net.sothatsit.heads.volatilecode.injection.ProtocolHackFixer;
import net.sothatsit.heads.volatilecode.reflection.craftbukkit.CommandMap;
import net.sothatsit.heads.volatilecode.reflection.craftbukkit.CraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class Heads extends JavaPlugin implements Listener {
    
    private static Heads instance;
    private CacheConfig cacheConfig;
    private MenuConfig menuConfig;
    private MainConfig mainConfig;
    private LangConfig langConfig;
    private TextureGetter textureGetter;
    private boolean commandsRegistered = false;
    private boolean enabled = false;
    
    @Override
    public void onEnable() {
        instance = this;

        long start = System.currentTimeMillis();

        this.cacheConfig = new CacheConfig(true, new FileConfigFile(new File(getDataFolder(), "cache.yml")));
        this.cacheConfig.checkAddons();
        
        this.menuConfig = new MenuConfig(new FileConfigFile(new File(getDataFolder(), "menus.yml")));
        this.langConfig = new LangConfig(new FileConfigFile(new File(getDataFolder(), "lang.yml")));
        this.mainConfig = new MainConfig(new FileConfigFile(new File(getDataFolder(), "config.yml")));
        this.textureGetter = new TextureGetter();
        
        Bukkit.getPluginManager().registerEvents(this, this);
        
        ProtocolHackFixer.fix();
        
        if (Economy.hookEconomy()) {
            info("Hooked Vault Economy");
        } else {
            info("Unable to Hook Vault Economy");
            
            if (mainConfig.isEconomyEnabled()) {
                severe("Vault Economy not hooked, but economy enabled in config");
                severe("Users will not be able to obtain heads");
            }
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                menuConfig.checkReload();
                mainConfig.checkReload();
                langConfig.checkReload();
            }
        }.runTaskTimer(this, 20, 20);
        
        enabled = true;
        
        registerCommands();
        
        if (Bukkit.getPluginManager().getPlugin("BlockStore") != null) {
            info("Attemping to hook BlockStore");
            long start2 = System.currentTimeMillis();
            
            try {
                new net.sothatsit.heads.blockstore.BlockStoreHook();
            } catch (Exception e) {
                e.printStackTrace();
                severe("Error hooking BlockStore, please report this error to the author");
            }
            
            info("Hooked BlockStore " + getTime(start2));
        }
        
        info("Heads Plugin Enabled " + getTime(start));
    }
    
    public void registerCommands() {
        if (!enabled) {
            return;
        }
        
        if (commandsRegistered) {
            unregisterCommands();
        }
        
        info("Registering Commands...");
        long start = System.currentTimeMillis();
        
        SimpleCommandMap commandMap = CraftServer.get().getCommandMap();
        
        RuntimeCommand heads = new RuntimeCommand(mainConfig.getHeadCommand());
        heads.setExecutor(new HeadsCommand());
        heads.setDescription(mainConfig.getHeadDescription());
        heads.setAliases(Arrays.asList(mainConfig.getHeadAliases()));
        
        commandMap.register("heads", heads);
        
        commandsRegistered = true;
        
        info("Registered Commands " + getTime(start));
    }
    
    public void unregisterCommands() {
        info("Unregistering Commands...");
        long start = System.currentTimeMillis();
        
        SimpleCommandMap commandMap = CraftServer.get().getCommandMap();
        Map<String, Command> map = CommandMap.getCommandMap(commandMap);
        
        List<String> remove = new ArrayList<>();
        
        for (Entry<String, Command> entry : map.entrySet()) {
            if (entry.getValue() instanceof RuntimeCommand) {
                remove.add(entry.getKey());
            }
        }
        
        for (String key : remove) {
            map.remove(key);
        }
        
        commandsRegistered = false;
        
        info("Unregistered Commands " + getTime(start));
    }
    
    public String getTime(long start) {
        return "(" + (System.currentTimeMillis() - start) + " ms)";
    }
    
    @Override
    public void onDisable() {
        instance = null;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if(isHatMode() && e.getClickedInventory() instanceof PlayerInventory && e.getSlotType() == InventoryType.SlotType.ARMOR && item != null && item.getType() == Material.SKULL_ITEM && ((SkullMeta) item.getItemMeta()).getOwner().equals("SpigotHeadPlugin")) {
            e.setCancelled(true);

            if(e.getWhoClicked() instanceof Player) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> ((Player) e.getWhoClicked()).updateInventory(), 1);
            }
        } else if (e.getInventory() != null && e.getInventory().getHolder() instanceof ClickInventory) {
            ((ClickInventory) e.getInventory().getHolder()).onClick(e);
        }
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
    
}
