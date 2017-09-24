package net.sothatsit.heads.command;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.mode.SearchMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        StringBuilder queryBuilder = new StringBuilder();

        for(int i=1; i < args.length; i++) {
            queryBuilder.append(args[i]);
            queryBuilder.append(' ');
        }

        String query = queryBuilder.toString().trim();
        List<CacheHead> matches = Heads.getCache().searchHeads(query);

        if(matches.size() == 0) {
            Lang.Command.Search.noneFound(query).send(sender);
            return true;
        }

        Lang.Command.Search.found(query, matches.size()).send(sender);

        new SearchMode((Player) sender, matches);
        return true;
    }
}
