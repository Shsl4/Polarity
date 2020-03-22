package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionAllyAccept implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;
            acceptAllyToFaction(ply, args.getOne("factionName").get().toString());

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();


    }

    private void acceptAllyToFaction(Player caller, String targetFactionName){

        Optional<XFaction> optTargetFaction = XFactionCommandManager.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(Text.of(XError.XERROR_XFNULL.getDesc())); return; }

        Optional<XFaction> optCallerFaction = XFactionCommandManager.getPlayerFaction(caller);

        // Return if the provided faction doesn't exist
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        // Get the safe references to our factions
        XFaction targetFaction = optTargetFaction.get();
        XFaction callerFaction = optCallerFaction.get();

        Optional<XFactionMemberData> optTargetMemberData = XFactionCommandManager.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getConfigure()) {  caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        // Return if no alliance request was sent by the provided faction
        if(!targetFaction.getFactionAllyInvites().contains(callerFaction.getFactionName())) { caller.sendMessage(Text.of(XError.XERROR_NOALLYRQ.getDesc())); return; }

        // Remove the alliance request from the caller's faction data and add each other faction as an ally
        targetFaction.getFactionAllyInvites().remove(callerFaction.getFactionName());
        targetFaction.getFactionAllies().add(callerFaction.getFactionName());
        callerFaction.getFactionAllies().add(targetFactionName);

        // For each TARGET's faction member
        for(XFactionMemberData targetFactionMbData : targetFaction.getFactionMembers()){

            Optional<Player> optTargetFactionConfigPlayer = XFactionCommandManager.getPlayerByName(targetFactionMbData.playerName);

            // Check if the player exists / is online
            if(optTargetFactionConfigPlayer.isPresent()){

                // If the caller faction's display name has been set, pick it, else use the faction's raw name
                String niceCallerFactionName = callerFaction.getFactionDisplayName().equals("") ? callerFaction.getFactionName() : callerFaction.getFactionDisplayName();

                // Notify the player of the alliance creation
                optTargetFactionConfigPlayer.get().sendMessage(Text.of("\u00a7d[Factions] " + niceCallerFactionName + "\u00a7d are now your allies!"));

            }

        }

        // For each CALLER's faction member
        for(XFactionMemberData callerFactionMbData : callerFaction.getFactionMembers()){

            Optional<Player> optTargetFactionConfigPlayer = XFactionCommandManager.getPlayerByName(callerFactionMbData.playerName);

            // Check if the player exists / is online
            if(optTargetFactionConfigPlayer.isPresent()){

                // If the target faction's display name has been set, pick it, else use the faction's raw name
                String niceTargetFactionName = targetFaction.getFactionDisplayName().equals("") ? targetFaction.getFactionName() : targetFaction.getFactionDisplayName();

                // Notify the player of the alliance creation
                optTargetFactionConfigPlayer.get().sendMessage(Text.of("\u00a7d[Factions] " + niceTargetFactionName + "\u00a7d are now your allies!"));

            }

        }

    }

}
