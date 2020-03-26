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

public class XFactionsAllyRequest implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Request an alliance to another faction."))
                .arguments(GenericArguments.string(Text.of("factionName")))
                .permission("xmanager.factions.ally.request")
                .executor(new XFactionsAllyRequest())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            // If the argument exists (this should theoretically always be true)
            if(args.getOne("factionName").isPresent()){

                requestAllyToFaction(ply, args.getOne("factionName").get().toString());

            }
            else{

                // Will send the player an error message
                requestAllyToFaction(ply, "");

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();


    }

    private void requestAllyToFaction(Player caller, String targetFactionName){

        Optional<XFaction> optTargetFaction = XUtilities.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(Text.of(XError.XERROR_XFNULL.getDesc())); return; }

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

        // Return if the provided faction doesn't exist
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        // Get the safe references to our factions
        XFaction targetFaction = optTargetFaction.get();
        XFaction callerFaction = optCallerFaction.get();

        if(callerFaction == targetFaction) { caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You cannot ally your own faction!")); return;}

        // Return if the faction is already allied
        if(targetFaction.isFactionAllied(callerFaction)) { caller.sendMessage(Text.of(XInfo.XERROR_ALREADYALLIED.getDesc())); return;}

        Optional<XFactionMemberData> optTargetMemberData = XUtilities.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getManage()) {  caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        // Return if an ally request was already submitted to the provided faction
        if(callerFaction.getFactionAllyInvites().contains(targetFactionName)) {  caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You already submitted an ally request to this faction. Ask their owner to accept it.")); return; }

        // Add the provided faction to the caller's faction alliance invitation list and notify the caller
        callerFaction.getFactionAllyInvites().add(targetFactionName);

        caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully submitted your ally request to " , targetFaction.getFactionDisplayName()));

        // For each TARGET faction's member
        for(XFactionMemberData playerData : targetFaction.getFactionMembers()){

            // If the player is allowed to configure the faction
            if(playerData.permissions.getManage()){

                Optional<Player> optTargetFactionConfigPlayer = XUtilities.getPlayerByName(playerData.playerName);

                // Check if the player exists / is online
                // Notify the player that they received an alliance request
                optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.AQUA, "[Factions] | ", callerFaction.getFactionDisplayName(), TextColors.RESET, TextColors.AQUA, " just sent you an alliance request! Type /factions ally accept ", callerFaction.getFactionName(), " to accept the alliance or /factions ally decline ", callerFaction.getFactionName(), " to decline it.")));

            }

        }

    }

}
