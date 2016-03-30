package net.sothatsit.heads.command;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.MainConfig;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HeadsCommand extends AbstractCommand {
    
    private CommandExecutor openMenu;
    private CommandExecutor add;
    private CommandExecutor hand;
    private CommandExecutor random;
    private CommandExecutor remove;
    private CommandExecutor rename;
    private CommandExecutor get;
    private CommandExecutor give;
    private CommandExecutor help;
    private CommandExecutor id;
    private CommandExecutor cost;
    
    public HeadsCommand() {
        this.openMenu = new OpenMenuCommand();
        this.add = new AddCommand();
        this.hand = new HandCommand();
        this.random = new RandomCommand();
        this.remove = new RemoveCommand();
        this.rename = new RenameCommand();
        this.get = new GetCommand();
        this.give = new GiveCommand();
        this.help = new HelpCommand();
        this.id = new IdCommand();
        this.cost = new CostCommand();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return runCommand(sender, command, label, args, openMenu, "heads.menu");
        }
        
        MainConfig config = Heads.getMainConfig();
        
        if (args[0].equalsIgnoreCase(config.getAddCommand())) {
            return runCommand(sender, command, label, args, add, "heads.add");
        }
        
        if (args[0].equalsIgnoreCase(config.getHandCommand())) {
            return runCommand(sender, command, label, args, hand, "heads.hand");
        }
        
        if (args[0].equalsIgnoreCase(config.getRandomCommand())) {
            return runCommand(sender, command, label, args, random, "heads.random");
        }
        
        if (args[0].equalsIgnoreCase(config.getRemoveCommand())) {
            return runCommand(sender, command, label, args, remove, "heads.remove");
        }
        
        if (args[0].equalsIgnoreCase(config.getRenameCommand())) {
            return runCommand(sender, command, label, args, rename, "heads.rename");
        }
        
        if (args[0].equalsIgnoreCase(config.getGetCommand())) {
            return runCommand(sender, command, label, args, get, "heads.get");
        }
        
        if (args[0].equalsIgnoreCase(config.getGiveCommand())) {
            return runCommand(sender, command, label, args, give, "heads.give");
        }
        
        if (args[0].equalsIgnoreCase(config.getCostCommand())) {
            return runCommand(sender, command, label, args, cost, "heads.cost");
        }
        
        if (args[0].equalsIgnoreCase(config.getIdCommand())) {
            return runCommand(sender, command, label, args, id, "heads.id");
        }
        
        return help.onCommand(sender, command, label, args);
    }
    
    public boolean runCommand(CommandSender sender, Command command, String label, String[] args, CommandExecutor executor, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to run this command"));
            return true;
        }
        
        return executor.onCommand(sender, command, label, args);
    }
    
}
