package io.sl4sh.xmanager.factions.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class XFactionList implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        printXFactionListHelp(src);

        return CommandResult.success();

    }

    private void printXFactionListHelp(CommandSource src){

        src.sendMessage(Text.of("\u00a72============ XManager Help ============"));
        src.sendMessage(Text.of("\u00a7a/xm factions list members <factionName> \u00a7fLists all members of a faction."));

    }
}
