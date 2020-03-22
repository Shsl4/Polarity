package io.sl4sh.xmanager.factions.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class XFactionHelp implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        printFactionsHelp(src);

        return CommandResult.success();

    }

    static public void printFactionsHelp(CommandSource src){

        src.sendMessage(Text.of("\u00a72============ Factions Help ============"));
        src.sendMessage(Text.of("\u00a7a/xm factions help \u00a7fPrints this help menu."));
        src.sendMessage(Text.of("\u00a7a/xm factions create \u00a7fCreates a faction."));
        src.sendMessage(Text.of("\u00a7a/xm factions disband \u00a7fDisbands your current faction."));
        src.sendMessage(Text.of("\u00a7a/xm factions invite \u00a7fInvites one or several player(s) to your faction."));
        src.sendMessage(Text.of("\u00a7a/xm factions claim \u00a7fClaims a chunk for your faction."));
        src.sendMessage(Text.of("\u00a7a/xm factions unclaim \u00a7fUnclaims one of your faction's chunks."));
        src.sendMessage(Text.of("\u00a7a/xm factions list \u00a7fLists all members of a faction."));
        src.sendMessage(Text.of("\u00a7a/xm factions leave \u00a7fLeaves your current faction."));
        src.sendMessage(Text.of("\u00a7a/xm factions kick \u00a7fKicks a member of your faction."));



    }

}
