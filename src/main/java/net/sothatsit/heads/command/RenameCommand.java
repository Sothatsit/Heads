package net.sothatsit.heads.command;

import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.lang.Lang;
import net.sothatsit.heads.menu.mode.InvModeType;
import net.sothatsit.heads.menu.mode.RenameMode;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenameCommand extends AbstractCommand {
    
    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length <= 1) {
            Lang.Command.Errors.invalidArgs().send(sender, Placeholder.valid(Lang.Command.Rename.help().command()));
            return true;
        }
        
        StringBuilder builder = new StringBuilder();
        
        for (int i = 1; i < args.length; i++) {
            if (i != 1) {
                builder.append(' ');
            }
            
            builder.append(args[i]);
        }
        
        String name = builder.toString();
        
        InvModeType.RENAME.open((Player) sender).asType(RenameMode.class).setName(name);
        return true;
    }
    
}
