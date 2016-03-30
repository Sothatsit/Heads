package net.sothatsit.heads.command;

import net.sothatsit.heads.lang.Lang;
import net.sothatsit.heads.menu.mode.InvModeType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenMenuCommand extends AbstractCommand {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        InvModeType.GET.open((Player) sender);
        return true;
    }
    
}
