package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.commands.elements.XFactionCommandElement;
import io.sl4sh.xmanager.enums.XError;
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

public class XFactionsAllyDecline implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Decline an alliance from another faction."))
                .arguments(new XFactionCommandElement(Text.of("factionName")))
                .permission("xmanager.factions.ally.decline")
                .executor(new XFactionsAllyDecline())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player ply = (Player) src;

            // If the argument exists (this should theoretically always be true)
            if(args.getOne("factionName").isPresent()){

                declineFactionRequest(ply, args.getOne("factionName").get().toString());

            }
            else{

                // Will send the player an error message
                declineFactionRequest(ply, "");

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void declineFactionRequest(Player caller, String targetFactionName){

        Optional<XFaction> optTargetFaction = XUtilities.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(XError.XERROR_XFNULL.getDesc()); return; }

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

        // Return if the provided faction doesn't exist
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

        // Get the safe references to our factions
        XFaction targetFaction = optTargetFaction.get();
        XFaction callerFaction = optCallerFaction.get();

        Optional<XFactionMemberData> optTargetMemberData = XUtilities.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getManage()) {  caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

        // Return if no alliance request was sent by the provided faction
        if(!targetFaction.getAllyInvites().contains(callerFaction.getUniqueId())) { caller.sendMessage(XError.XERROR_NOALLYRQ.getDesc()); return; }

        // Remove the alliance request from the caller's faction
        targetFaction.getAllyInvites().remove(callerFaction.getUniqueId());

        // Send info message
        caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You declined ", targetFaction.getDisplayName(), TextColors.RESET, TextColors.LIGHT_PURPLE, "'s alliance request."));

        // For each TARGET faction's member
        for(XFactionMemberData playerData : targetFaction.getMemberDataList()){

            // If the player is allowed to configure the faction
            if(playerData.permissions.getManage()){

                Optional<Player> optTargetFactionConfigPlayer = XUtilities.getPlayerByUniqueID(playerData.getPlayerUniqueID());

                // Check if the player exists / is online, Notify the player that their alliance request has been declined
                optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Factions] | ", callerFaction.getDisplayName(), TextColors.RESET, TextColors.LIGHT_PURPLE, " declined your alliance request.")));

            }

        }

    }

}
