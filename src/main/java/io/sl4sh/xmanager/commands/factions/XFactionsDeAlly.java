package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.enums.XInfo;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class XFactionsDeAlly implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Destroy an alliance with another faction."))
                .arguments(GenericArguments.string(Text.of("factionName")))
                .permission("xmanager.factions.deally")
                .executor(new XFactionsDeAlly())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            if(args.getOne("factionName").isPresent()) {

                deallyFaction(ply, args.getOne("factionName").get().toString());

            }
            else{

                deallyFaction(ply, "");

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void deallyFaction(Player caller, String targetFactionName){

        Optional<XFaction> optTargetFaction = XUtilities.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(XError.XERROR_XFNULL.getDesc()); return; }

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

        // Return if the caller doesn't have any faction
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

        // Get the safe references to our factions
        XFaction targetFaction = optTargetFaction.get();
        XFaction callerFaction = optCallerFaction.get();

        // Return if the faction not an ally
        if(!targetFaction.isFactionAllied(callerFaction)) { caller.sendMessage(XInfo.XERROR_NOTALLIED.getDesc()); return;}

        Optional<XFactionMemberData> optTargetMemberData = XUtilities.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getManage()) {  caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

        targetFaction.getFactionAllies().remove(callerFaction.getFactionName());
        callerFaction.getFactionAllies().remove(targetFactionName);

        // For each TARGET's faction member
        for(XFactionMemberData targetFactionMbData : targetFaction.getFactionMembers()){

            Optional<Player> optTargetFactionConfigPlayer = XUtilities.getPlayerByName(targetFactionMbData.playerName);

            // Check if the player exists / is online
            // Notify the player of the alliance destruction
            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.RED, "[Factions] | " , callerFaction.getFactionDisplayName(), TextColors.RESET, TextColors.RED, " are no longer your allies!")));

        }

        // For each CALLER's faction member
        for(XFactionMemberData callerFactionMbData : callerFaction.getFactionMembers()){

            Optional<Player> optTargetFactionConfigPlayer = XUtilities.getPlayerByName(callerFactionMbData.playerName);

            // Check if the player exists / is online
            // Notify the player of the alliance destruction
            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.RED, "[Factions] | ", targetFaction.getFactionDisplayName(), TextColors.RED, " are no longer your allies!")));

        }

    }

}
