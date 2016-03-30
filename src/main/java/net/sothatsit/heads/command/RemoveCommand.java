package net.sothatsit.heads.command;

import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.lang.Lang;
import net.sothatsit.heads.menu.mode.InvModeType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveCommand extends AbstractCommand {
    
    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length != 1) {
            Lang.Command.Errors.invalidArgs().send(sender, Placeholder.valid(Lang.Command.Remove.help().command()));
            return true;
        }
        
        InvModeType.REMOVE.open((Player) sender);
        return true;
    }
    
}
