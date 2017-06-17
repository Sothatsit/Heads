package net.sothatsit.heads.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.config.lang.Lang;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RandomCommand extends AbstractCommand {
    
    private static final Random rand = new Random();

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getRandomCommand();
    }

    @Override
    public String getPermission() {
        return "heads.random";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Random.help();
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length != 1) {
            sendInvalidArgs(sender);
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
