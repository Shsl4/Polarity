package io.sl4sh.xmanager.factions.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class XFactionListHelp implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        printXFactionListHelp(src);

        return CommandResult.success();

    }

    private void printXFactionListHelp(CommandSource src){

        src.sendMessage(Text.of("\u00a72============ Factions listing Help ============"));
        src.sendMessage(Text.of("\u00a7a/factions list \u00a7fLists all existing factions."));
        src.sendMessage(Text.of("\u00a7a/factions list help \u00a7fShows this help page."));
        src.sendMessage(Text.of("\u00a7a/factions list members <factionName> \u00a7fLists all members of a faction."));
        src.sendMessage(Text.of("\u00a7a/factions list allies <factionName> \u00a7fLists all allied factions of a faction."));

    }
}
