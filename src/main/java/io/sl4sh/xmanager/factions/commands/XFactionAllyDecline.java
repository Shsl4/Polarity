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

public class XFactionAllyDecline implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            declineFactionRequest(ply, args.getOne("factionName").get().toString());

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void declineFactionRequest(Player caller, String targetFactionName){

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

        // Remove the alliance request from the caller's faction
        targetFaction.getFactionAllyInvites().remove(callerFaction.getFactionName());

        caller.sendMessage(Text.of("\u00a7b[Factions] | You declined " + targetFactionName + "'s alliance request."));

        // For each TARGET faction's member
        for(XFactionMemberData playerData : targetFaction.getFactionMembers()){

            // If the player is allowed to configure the faction
            if(playerData.permissions.getConfigure()){

                Optional<Player> optTargetFactionConfigPlayer = XFactionCommandManager.getPlayerByName(playerData.playerName);

                // Check if the player exists / is online
                if(optTargetFactionConfigPlayer.isPresent()){

                    // If the caller faction's display name has been set, pick it, else use the faction's raw name
                    String niceCallerFactionName = callerFaction.getFactionDisplayName().equals("") ? callerFaction.getFactionName() : callerFaction.getFactionDisplayName();

                    // Notify the player that their alliance request has been declined
                    optTargetFactionConfigPlayer.get().sendMessage(Text.of("\u00a7c[Factions] | " + niceCallerFactionName + "\u00a7c declined your alliance request."));

                }

            }

        }

    }

}
