package net.sothatsit.heads.command;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.mode.SearchMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getSearchCommand();
    }

    @Override
    public String getPermission() {
        return "heads.search";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Search.help();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length <= 1) {
            sendInvalidArgs(sender);
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
                    continue;
                }

                for(String tag : head.getTags()) {
                    if(tag.toLowerCase().contains(query)) {
                        matches.add(head);
                        break;
                    }
                }
            }
        }

        Collections.sort(matches);

        if(matches.size() == 0) {
            Lang.Command.Search.noneFound().send(sender, new Placeholder("%query%", builder.toString().trim()), new Placeholder("%heads%", "0"));
            return true;
        }

        Lang.Command.Search.found().send(sender, new Placeholder("%query%", builder.toString().trim()), new Placeholder("%heads%", Integer.toString(matches.size())));

        new SearchMode((Player) sender, matches);
        return true;
    }
}
