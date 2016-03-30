package net.sothatsit.heads.command;

import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.lang.Lang;
import net.sothatsit.heads.menu.mode.CostMode;
import net.sothatsit.heads.menu.mode.InvModeType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CostCommand extends AbstractCommand {
    
    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length != 2) {
            Lang.Command.Errors.invalidArgs().send(sender, Placeholder.valid(Lang.Command.Cost.help().command()));
            return true;
        }
        
        double cost;
        try {
            cost = Double.valueOf(args[1]);
        } catch (NumberFormatException e) {
            Lang.Command.Errors.number().send(sender, Placeholder.number(args[1]));
            return true;
        }
        
        if (cost < 0) {
            Lang.Command.Errors.negative().send(sender, Placeholder.number(args[1]));
            return true;
        }
        
        InvModeType.COST.open((Player) sender).asType(CostMode.class).setCost(cost);
        return true;
    }
    
}
