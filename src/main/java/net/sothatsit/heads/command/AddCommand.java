package net.sothatsit.heads.command;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.lang.Lang;
import net.sothatsit.heads.util.Callback;
import net.sothatsit.heads.volatilecode.reflection.Version;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddCommand extends AbstractCommand {
    
    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (Version.v1_8.higherThan(Version.getVersion())) {
            Lang.Command.Add.notSupported().send(sender);
            return true;
        }
        
        if (args.length < 4) {
            Lang.Command.Errors.invalidArgs().send(sender, Placeholder.valid(Lang.Command.Add.help().command()));
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
            Heads.getTextureGetter().getTexture(playerName, new Callback<String>() {
                @Override
                public void call(String texture) {
                    add(sender, category, name, playerName, texture);
                }
            });
        }
        return true;
    }
    
    public void add(CommandSender sender, String category, String name, String playerName, String texture) {
        if (texture == null || texture.isEmpty()) {
            Lang.Command.Add.cantFind().send(sender, Placeholder.name(playerName));
            return;
        }
        
        CachedHead head = new CachedHead(-1, category, name, texture);
        
        Heads.getCacheConfig().add(head);
        
        Lang.Command.Add.adding().send(sender, Placeholder.name(name), Placeholder.category(category));
    }
    
}
