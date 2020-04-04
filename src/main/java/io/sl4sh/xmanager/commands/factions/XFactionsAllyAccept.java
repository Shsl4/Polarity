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

public class XFactionsAllyAccept implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

       return CommandSpec.builder()
                .description(Text.of("Accept an alliance from another faction."))
                .arguments(new XFactionCommandElement(Text.of("factionName")))
                .permission("xmanager.factions.ally.accept")
                .executor(new XFactionsAllyAccept())
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

                acceptAllyToFaction(ply, args.getOne("factionName").get().toString());

            }
            else{

                // Will send the player an error message
                acceptAllyToFaction(ply, "");

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void acceptAllyToFaction(Player caller, String targetFactionName){

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

        // Remove the alliance request from the caller's faction data and add each other faction as an ally
        targetFaction.getAllyInvites().remove(callerFaction.getUniqueId());
        targetFaction.getAllies().add(callerFaction.getUniqueId());
        callerFaction.getAllies().add(targetFaction.getUniqueId());

        // For each TARGET's faction member
        for(XFactionMemberData targetFactionMbData : targetFaction.getMemberDataList()){

            Optional<Player> optTargetFactionConfigPlayer = XUtilities.getPlayerByUniqueID(targetFactionMbData.getPlayerUniqueID());

            // Check if the player exists / is online
            // Notify the player of the alliance creation

            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.AQUA, "[Factions] | ", callerFaction.getDisplayName(), TextColors.RESET, TextColors.AQUA, " are now your allies!")));

        }

        // For each CALLER's faction member
        for(XFactionMemberData callerFactionMbData : callerFaction.getMemberDataList()){

            Optional<Player> optTargetFactionConfigPlayer = XUtilities.getPlayerByUniqueID(callerFactionMbData.getPlayerUniqueID());

            // Check if the player exists / is online
            // Notify the player of the alliance creation
            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.AQUA, "[Factions] | ", targetFaction.getDisplayName(), TextColors.RESET, TextColors.AQUA, " are now your allies!")));

        }

    }

}
