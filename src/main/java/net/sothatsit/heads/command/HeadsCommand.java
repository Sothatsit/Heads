package net.sothatsit.heads.command;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HeadsCommand implements CommandExecutor {

    private static final AbstractCommand openMenu = new OpenMenuCommand();
    private static final AbstractCommand help = new HelpCommand();

    public static final AbstractCommand[] commands = {
            new OpenMenuCommand(),
            new SearchCommand(),
            new GetCommand(),
            new RandomCommand(),

            new AddCommand(),
            new HandCommand(),
            new RemoveCommand(),
            new RenameCommand(),
            new IdCommand(),
            new GiveCommand(),
            new CostCommand(),
            new CategoryCostCommand(),

            new ReloadCommand(),
            new HelpCommand()
    };
    
    @Override
    public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] args) {
        if (args.length == 0) {
            String permission = openMenu.getPermission();

            if (permission != null && !sender.hasPermission(permission)) {
                Lang.Command.Errors.noPermission().send(sender);
                return true;
            }

            return openMenu.onCommand(sender, bukkitCommand, label, args);
        }

        String argument = args[0];
        MainConfig config = Heads.getMainConfig();

        for(AbstractCommand command : commands) {
            String commandLabel = command.getCommandLabel(config);

            if(commandLabel == null || !argument.equalsIgnoreCase(commandLabel))
                continue;

            String permission = command.getPermission();

            if (permission != null && !sender.hasPermission(permission)) {
                Lang.Command.Errors.noPermission().send(sender);
                return true;
            }

            return command.onCommand(sender, bukkitCommand, label, args);
        }
        
        return help.onCommand(sender, bukkitCommand, label, args);
    }
    
}
