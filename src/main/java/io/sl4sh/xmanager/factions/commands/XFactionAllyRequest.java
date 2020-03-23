package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.commands.XManagerCommandManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionAllyRequest implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            requestAllyToFaction(ply, args.getOne("factionName").get().toString());

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();


    }

    private void requestAllyToFaction(Player caller, String targetFactionName){

        Optional<XFaction> optTargetFaction = XFactionCommandManager.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(Text.of(XError.XERROR_XFNULL.getDesc())); return; }

        Optional<XFaction> optCallerFaction = XFactionCommandManager.getPlayerFaction(caller);

        // Return if the provided faction doesn't exist
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        // Get the safe references to our factions
        XFaction targetFaction = optTargetFaction.get();
        XFaction callerFaction = optCallerFaction.get();

        if(callerFaction == targetFaction) { caller.sendMessage(Text.of("\u00a7b[Factions] | You cannot ally your own faction!")); return;}

        // Return if the faction is already allied
        if(targetFaction.isFactionAllied(callerFaction)) { caller.sendMessage(Text.of(XError.XERROR_ALREADYALLIED.getDesc())); return;}

        Optional<XFactionMemberData> optTargetMemberData = XFactionCommandManager.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getConfigure()) {  caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        // Return if an ally request was already submitted to the provided faction
        if(callerFaction.getFactionAllyInvites().contains(targetFactionName)) {  caller.sendMessage(Text.of("\u00a7b[Factions] | You already submitted an ally request to this faction. Ask their owner to accept it.")); return;}

        // Add the provided faction to the caller's faction alliance invitation list and notify the caller
        callerFaction.getFactionAllyInvites().add(targetFactionName);
        caller.sendMessage(Text.of("\u00a7a[Factions] | Successfully submitted your ally request to " + (targetFaction.getFactionDisplayName().equals("") ? targetFactionName : targetFaction.getFactionDisplayName())));

        // For each TARGET faction's member
        for(XFactionMemberData playerData : targetFaction.getFactionMembers()){

            // If the player is allowed to configure the faction
            if(playerData.permissions.getConfigure()){

                Optional<Player> optTargetFactionConfigPlayer = XFactionCommandManager.getPlayerByName(playerData.playerName);

                // Check if the player exists / is online
                if(optTargetFactionConfigPlayer.isPresent()){

                    // If the caller faction's display name has been set, pick it, else use the faction's raw name
                    String niceCallerFactionName = callerFaction.getFactionDisplayName().equals("") ? callerFaction.getFactionName() : callerFaction.getFactionDisplayName();

                    // Notify the player that they received an alliance request
                    optTargetFactionConfigPlayer.get().sendMessage(Text.of("\u00a7a[Factions] | " + niceCallerFactionName + "\u00a7a just sent you an alliance request! Type /factions ally accept " + callerFaction.getFactionName() +  " to accept the alliance or /factions ally decline " + callerFaction.getFactionName() + " to decline it."));

                }

            }

        }

    }

}
