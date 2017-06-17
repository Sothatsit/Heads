package net.sothatsit.heads.command;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.config.lang.Lang;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getGiveCommand();
    }

    @Override
    public String getPermission() {
        return "heads.give";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Give.help();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 4) {
            sendInvalidArgs(sender);
            return true;
        }
        
        String idStr = args[1];
        
        int id = -1;
        
        try {
            id = Integer.valueOf(idStr);
        } catch (NumberFormatException e) {
            Lang.Command.Errors.integer().send(sender, Placeholder.number(idStr));
            return true;
        }
        
        String amountStr = args[3];
        
        int amount = -1;
        
        try {
            amount = Integer.valueOf(amountStr);
        } catch (NumberFormatException e) {
            Lang.Command.Give.invalidAmount().send(sender, Placeholder.number(amountStr));
            return true;
        }
        
        if (amount <= 0) {
            Lang.Command.Give.invalidAmount().send(sender, Placeholder.number(amountStr));
        }
        
        String playerStr = args[2];
        
        Player player = Bukkit.getPlayer(playerStr);
        
        if (player == null || !player.isOnline()) {
            Lang.Command.Give.cantFindPlayer().send(sender, Placeholder.name(playerStr));
            return true;
        }
        
        CachedHead head = Heads.getCacheConfig().getHead(id);
        
        if (head == null) {
            Lang.Command.Give.cantFindHead().send(sender, Placeholder.id(idStr));
            return true;
        }
        
        ItemStack headItem = head.getItemStack();
        for (int i = 0; i < amount; i++) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(headItem.clone());
            } else {
                Item item = player.getWorld().dropItemNaturally(player.getEyeLocation(), headItem.clone());
                item.setPickupDelay(0);
            }
        }
        
        Lang.Command.Give.give().send(sender, Placeholder.amount(amountStr), Placeholder.head(head.getName()), Placeholder.name(player.getName()));
        return true;
    }
    
    public void giveHead(Player player, String name, String texture) {
        if (player != null) {
            if (texture == null || texture.isEmpty()) {
                Lang.Command.Get.cantFind().send(player, Placeholder.name(name));
                return;
            }
            
            CachedHead head = new CachedHead(-1, "givecommand", name, texture, new String[0]);
            
            Lang.Command.Get.adding().send(player, Placeholder.name(name));
            
            if (Heads.isHatMode()) {
                player.getInventory().setHelmet(head.getItemStack());
            } else {
                player.getInventory().addItem(head.getItemStack());
            }
        }
    }
}
