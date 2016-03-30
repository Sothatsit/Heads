package net.sothatsit.heads.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Economy {
    
    private static Economy instance = new Economy();
    private net.milkbowl.vault.economy.Economy eco;
    
    protected Economy() {
        
    }
    
    public boolean hookEconomyImpl() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        try {
            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServer().getServicesManager()
                    .getRegistration(net.milkbowl.vault.economy.Economy.class);
            
            if (rsp == null) {
                return false;
            }
            
            eco = rsp.getProvider();
            return eco != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isHookedImpl() {
        return eco != null;
    }
    
    public boolean hasBalanceImpl(Player player, double bal) {
        return eco.has(player, bal);
    }
    
    public boolean takeBalanceImpl(Player player, double bal) {
        return eco.withdrawPlayer(player, bal).transactionSuccess();
    }
    
    public static boolean hookEconomy() {
        return instance.hookEconomyImpl();
    }
    
    public static boolean isHooked() {
        return instance.isHookedImpl();
    }
    
    public static boolean hasBalance(Player player, double bal) {
        return instance.hasBalanceImpl(player, bal);
    }
    
    public static boolean takeBalance(Player player, double bal) {
        return instance.takeBalanceImpl(player, bal);
    }
    
}
