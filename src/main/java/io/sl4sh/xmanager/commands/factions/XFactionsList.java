package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class XFactionsList implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists the existing factions."))
                .permission("xmanager.factions.list")
                .child(XFactionsListAllies.getCommandSpec(), "allies")
                .child(XFactionsListMembers.getCommandSpec(), "members")
                .child(XFactionsListHelp.getCommandSpec(), "help")
                .executor(new XFactionsList())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        listFactions(src);

        return CommandResult.success();

    }

    private void listFactions(CommandSource src){

        TextColor listTintColor = TextColors.GREEN;

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Factions list ============"));

        List<XFaction> factionList = XManager.getXManager().getFactionsContainer().getFactionList();

        if(factionList.size() <= 0){

            src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!"));

        }
        else{

            int it = 1;

            for(XFaction faction : factionList){

                src.sendMessage(Text.of(listTintColor , "#" , it , ". " , TextColors.WHITE , XUtilities.getStringReplacingModifierChar(faction.getFactionDisplayName()), listTintColor , " | Owner: " , TextColors.WHITE , faction.getFactionOwner() , listTintColor , " | Raw name: " , TextColors.WHITE , faction.getFactionName()));
                it++;

            }

        }

    }

}