package net.sothatsit.heads.command;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.volatilecode.reflection.Version;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AddCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getAddCommand();
    }

    @Override
    public String getPermission() {
        return "heads.add";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Add.help();
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (Version.v1_8.higherThan(Version.getVersion())) {
            Lang.Command.Add.notSupported().send(sender);
            return true;
        }
        
        if (args.length < 4) {
            sendInvalidArgs(sender);
            return true;
        }
        
        final String playerName = args[1];
        final String category = args[2];
        
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            nameBuilder.append(' ');
            nameBuilder.append(args[i]);
        }
        
        if (category.length() > 32) {
            Lang.Command.Add.categoryLength().send(sender, Placeholder.category(category), Placeholder.length(category.length()));
            return true;
        }
        
        final String name = nameBuilder.toString().substring(1);
        
        String texture = Heads.getTextureGetter().getCachedTexture(playerName);
        
        if (texture != null) {
            add(sender, category, name, playerName, texture);
        } else {
            Lang.Command.Add.fetching().send(sender);
            Heads.getTextureGetter().getTexture(playerName, (resolvedTexture) -> {
                add(sender, category, name, playerName, resolvedTexture);
            });
        }
        return true;
    }
    
    public void add(CommandSender sender, String category, String name, String playerName, String texture) {
        if (texture == null || texture.isEmpty()) {
            Lang.Command.Add.cantFind().send(sender, Placeholder.name(playerName));
            return;
        }

        CacheHead head = new CacheHead(name, category, texture);

        Heads.getCache().addHead(head);
        
        Lang.Command.Add.added().send(sender, Placeholder.name(name), Placeholder.category(category));
    }
    
}
