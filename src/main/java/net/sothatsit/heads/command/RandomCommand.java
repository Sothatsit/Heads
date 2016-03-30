package net.sothatsit.heads.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.lang.Lang;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RandomCommand extends AbstractCommand {
    
    private static final Random rand = new Random();
    
    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length != 1) {
            Lang.Command.Errors.invalidArgs().send(sender, Placeholder.valid(Lang.Command.Rename.help().command()));
            return true;
        }
        
        List<CachedHead> heads = new ArrayList<>();
        
        for (List<CachedHead> list : Heads.getCacheConfig().getHeads().values()) {
            heads.addAll(list);
        }
        
        if (heads.size() == 0) {
            Lang.Command.Random.noHeads().send(sender);
            return true;
        }
        
        CachedHead random = heads.get(rand.nextInt(heads.size()));
        
        Lang.Command.Random.giving().send(sender, Placeholder.name(random.getName()), Placeholder.category(random.getCategory()));
        
        if (Heads.isHatMode()) {
            ((Player) sender).getInventory().setHelmet(random.getItemStack());
        } else {
            ((Player) sender).getInventory().addItem(random.getItemStack());
        }
        return true;
    }
}
