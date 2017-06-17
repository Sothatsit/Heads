package net.sothatsit.heads;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHook {

    private static Economy economy;
    
    private EconomyHook() {
        
    }

    public static boolean hookEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (rsp == null)
            return false;

        economy = rsp.getProvider();

        return economy != null;
    }
    
    public static boolean isHooked() {
        return economy != null;
    }
    
    public static boolean hasBalance(Player player, double bal) {
        return economy.has(player, bal);
    }
    
    public static boolean takeBalance(Player player, double bal) {
        return economy.withdrawPlayer(player, bal).transactionSuccess();
    }
    
}
