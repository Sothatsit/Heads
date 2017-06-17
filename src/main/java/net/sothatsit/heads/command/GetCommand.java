package net.sothatsit.heads.command;

import java.util.UUID;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.volatilecode.reflection.Version;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class GetCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getGetCommand();
    }

    @Override
    public String getPermission() {
        return "heads.get";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Get.help();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length != 2) {
            sendInvalidArgs(sender);
            return true;
        }
        
        if (Version.v1_8.higherThan(Version.getVersion())) {
            Lang.Command.Get.oldMethod().send(sender);
            
            ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            
            meta.setOwner(args[1]);
            meta.setDisplayName(Lang.Command.Get.headName().getSingle(Placeholder.name(args[1])));
            
            head.setItemMeta(meta);
            
            Lang.Command.Get.adding().send(sender, Placeholder.name(args[1]));
            ((Player) sender).getInventory().addItem(head);
            return true;
        }
        
        String texture = Heads.getTextureGetter().getCachedTexture(args[1]);
        
        if (texture != null) {
            giveHead((Player) sender, args[1], texture);
            return true;
        }
        
        Lang.Command.Get.fetching().send(sender);
        
        final UUID uuid = ((Player) sender).getUniqueId();
        final String name = args[1];
        
        Heads.getTextureGetter().getTexture(name, (resolvedTexture) -> {
            giveHead(Bukkit.getPlayer(uuid), name, resolvedTexture);
        });
        return true;
    }
    
    public void giveHead(Player player, String name, String texture) {
        if (player != null) {
            if (texture == null || texture.isEmpty()) {
                Lang.Command.Get.cantFind().send(player, Placeholder.name(name));
                return;
            }
            
            CachedHead head = new CachedHead(-1, "getcommand", name, texture, new String[0]);
            
            Lang.Command.Get.adding().send(player, Placeholder.name(name));
            
            if (Heads.isHatMode()) {
                player.getInventory().setHelmet(head.getItemStack());
            } else {
                player.getInventory().addItem(head.getItemStack());
            }
        }
    }
}
