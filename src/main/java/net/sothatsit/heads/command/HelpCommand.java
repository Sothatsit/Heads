package net.sothatsit.heads.command;

import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.config.lang.Lang.HelpSection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Help.help();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] args) {
        if(args.length > 2) {
            sendInvalidArgs(sender);
            return true;
        }

        int page = 1;

        if(args.length == 2) {
            try {
                page = Integer.valueOf(args[1]);
            } catch(NumberFormatException e) {
                Lang.Command.Errors.integer().send(sender, Placeholder.number(args[1]));
                return true;
            }
        }

        int lineLength = Lang.Command.Help.line().getRaw().length;
        int linesPerPage = (lineLength >= 1 && lineLength <= 8 ? 8 / lineLength : 1);
        int pages = (linesPerPage + HeadsCommand.commands.length - 1) / linesPerPage;

        Placeholder[] pagePlaceholders = {
                new Placeholder("%page%", page),
                new Placeholder("%next-page%", (page >= pages ? 1 : page + 1)),
                new Placeholder("%pages%", pages)
        };

        if(page < 1 || page > pages) {
            Lang.Command.Help.unknownPage().send(sender, pagePlaceholders);
            return true;
        }

        Lang.Command.Help.header().send(sender, pagePlaceholders);

        int startIndex = (page - 1) * linesPerPage;
        int endIndex = page * linesPerPage;

        if(endIndex > HeadsCommand.commands.length) {
            endIndex = HeadsCommand.commands.length;
        }

        for(int index = startIndex; index < endIndex; ++index) {
            HelpSection helpSection = HeadsCommand.commands[index].getHelp();

            Placeholder[] placeholders = {
                    Placeholder.command(helpSection.command()),
                    Placeholder.description(helpSection.description())
            };

            Lang.Command.Help.line().send(sender, placeholders);
        }

        Lang.Command.Help.footer().send(sender, pagePlaceholders);
        return true;
    }
}
