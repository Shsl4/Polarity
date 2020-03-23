package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.List;

public class XFactionList implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        listFactions(src);

        return CommandResult.success();

    }

    private void listFactions(CommandSource src){

        src.sendMessage(Text.of("\u00a72============ Factions list ============"));

        List<XFaction> factionList = XManager.getXManager().getFactionContainer().getFactionList();

        if(factionList.size() <= 0){

            src.sendMessage(Text.of("\u00a7aNothing to see here... Yet!"));
            return;

        }
        else{

            int it = 1;

            for(XFaction faction : factionList){

                src.sendMessage(Text.of("\u00a7a#" + it + ". \u00a7f" + faction.getFactionDisplayName() + "\u00a7a | Owner: \u00a7f" + faction.getFactionOwner(), " \u00a7a| Raw name: \u00a7f" + faction.getFactionName()));
                it++;

            }

        }

    }

}
