package net.sothatsit.heads.command;

import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.lang.Lang;
import net.sothatsit.heads.lang.Lang.HelpSection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand extends AbstractCommand {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Placeholder[][] placeholders = {
                create(Lang.Command.OpenMenu.help()),
                create(Lang.Command.Add.help()),
                create(Lang.Command.Remove.help()),
                create(Lang.Command.Rename.help()),
                create(Lang.Command.Get.help()),
                create(Lang.Command.Id.help()),
                create(Lang.Command.Help.help()),
        };
        
        Lang.Command.Help.header().send(sender);
        
        for (Placeholder[] holders : placeholders) {
            Lang.Command.Help.line().send(sender, holders);
        }
        
        return true;
    }
    
    public static Placeholder[] create(HelpSection section) {
        return new Placeholder[] {
                Placeholder.command(section.command()),
                Placeholder.description(section.description())
        };
    }
}
