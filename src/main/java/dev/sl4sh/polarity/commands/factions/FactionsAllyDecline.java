package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.FactionCommandElement;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class FactionsAllyDecline implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Decline an alliance from another faction."))
                .arguments(new FactionCommandElement(Text.of("factionName")))
                .permission("polarity.factions.ally.decline")
                .executor(new FactionsAllyDecline())
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

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void declineFactionRequest(Player caller, String targetFactionName){

        Optional<Faction> optTargetFaction = Utilities.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(PolarityErrors.XERROR_XFNULL.getDesc()); return; }

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        // Return if the provided faction doesn't exist
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(PolarityErrors.XERROR_NOXF.getDesc()); return; }

        // Get the safe references to our factions
        Faction targetFaction = optTargetFaction.get();
        Faction callerFaction = optCallerFaction.get();

        Optional<FactionMemberData> optTargetMemberData = Utilities.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getManage()) {  caller.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc()); return; }

        // Return if no alliance request was sent by the provided faction
        if(!targetFaction.getAllyInvites().contains(callerFaction.getUniqueId())) { caller.sendMessage(PolarityErrors.XERROR_NOALLYRQ.getDesc()); return; }

        // Remove the alliance request from the caller's faction
        targetFaction.getAllyInvites().remove(callerFaction.getUniqueId());

        // Send info message
        caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You declined ", targetFaction.getDisplayName(), TextColors.RESET, TextColors.LIGHT_PURPLE, "'s alliance request."));

        // For each TARGET faction's member
        for(FactionMemberData playerData : targetFaction.getMemberDataList()){

            // If the player is allowed to configure the faction
            if(playerData.permissions.getManage()){

                Optional<Player> optTargetFactionConfigPlayer = Utilities.getPlayerByUniqueID(playerData.getPlayerUniqueID());

                // Check if the player exists / is online, Notify the player that their alliance request has been declined
                optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Factions] | ", callerFaction.getDisplayName(), TextColors.RESET, TextColors.LIGHT_PURPLE, " declined your alliance request.")));

            }

        }

    }

}
