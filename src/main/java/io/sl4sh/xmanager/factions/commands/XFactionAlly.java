package io.sl4sh.xmanager.factions.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class XFactionAlly implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        printAllianceHelp(src);

        return CommandResult.success();

    }

    static public void printAllianceHelp(CommandSource src){

        src.sendMessage(Text.of("\u00a72============ Factions Alliance Help ============"));
        src.sendMessage(Text.of("\u00a7a/factions ally request \u00a7fRequests an alliance to another faction."));
        src.sendMessage(Text.of("\u00a7a/factions ally accept \u00a7fAccept an alliance from another faction."));
        src.sendMessage(Text.of("\u00a7a/factions ally decline \u00a7fDeclines an alliance from another faction."));

    }

}
