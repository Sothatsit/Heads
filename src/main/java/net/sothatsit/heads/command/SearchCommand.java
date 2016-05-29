package net.sothatsit.heads.command;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.lang.Lang;
import net.sothatsit.heads.menu.mode.SearchMode;
import net.sothatsit.heads.util.Callback;
import net.sothatsit.heads.volatilecode.reflection.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SearchCommand extends AbstractCommand {
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length == 1) {
            Lang.Command.Errors.invalidArgs().send(sender, Placeholder.valid(Lang.Command.Search.help().command()));
            return true;
        }

        StringBuilder builder = new StringBuilder();

        for(int i=1; i < args.length; i++) {
            builder.append(args[i]);
            builder.append(' ');
        }

        String query = builder.toString().trim().toLowerCase();

        List<CachedHead> matches = new ArrayList<>();

        for(List<CachedHead> list : Heads.getCacheConfig().getHeads().values()) {
            for(CachedHead head : list) {
                if(head.getName().toLowerCase().contains(query)) {
                    matches.add(head);
                }
            }
        }

        if(matches.size() == 0) {
            Lang.Command.Search.noneFound().send(sender, new Placeholder("%query%", builder.toString().trim()), new Placeholder("%heads%", "0"));
            return true;
        }

        Lang.Command.Search.found().send(sender, new Placeholder("%query%", builder.toString().trim()), new Placeholder("%heads%", Integer.toString(matches.size())));

        new SearchMode((Player) sender, matches);
        return true;
    }
}
