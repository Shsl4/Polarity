package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.FactionCommandElement;
import dev.sl4sh.polarity.enums.PolarityErrors;
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

public class FactionsAllyAccept implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

       return CommandSpec.builder()
                .description(Text.of("Accept an alliance from another faction."))
                .arguments(new FactionCommandElement(Text.of("factionName")))
                .permission("polarity.factions.ally.accept")
                .executor(new FactionsAllyAccept())
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

            src.sendMessage(PolarityErrors.PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void acceptAllyToFaction(Player caller, String targetFactionName){

        Optional<Faction> optTargetFaction = Utilities.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(PolarityErrors.NULLFACTION.getDesc()); return; }

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        // Return if the provided faction doesn't exist
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(PolarityErrors.NOFACTION.getDesc()); return; }

        // Get the safe references to our factions
        Faction targetFaction = optTargetFaction.get();
        Faction callerFaction = optCallerFaction.get();

        Optional<FactionMemberData> optTargetMemberData = Utilities.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getManage()) {  caller.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc()); return; }

        // Return if no alliance request was sent by the provided faction
        if(!targetFaction.getAllyInvites().contains(callerFaction.getUniqueId())) { caller.sendMessage(PolarityErrors.FACTION_NOALLYREQUEST.getDesc()); return; }

        // Remove the alliance request from the caller's faction data and add each other faction as an ally
        targetFaction.getAllyInvites().remove(callerFaction.getUniqueId());
        targetFaction.getAllies().add(callerFaction.getUniqueId());
        callerFaction.getAllies().add(targetFaction.getUniqueId());

        // For each TARGET's faction member
        for(FactionMemberData targetFactionMbData : targetFaction.getMemberDataList()){

            Optional<Player> optTargetFactionConfigPlayer = Utilities.getPlayerByUniqueID(targetFactionMbData.getPlayerUniqueID());

            // Check if the player exists / is online
            // Notify the player of the alliance creation

            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.AQUA, "", callerFaction.getDisplayName(), TextColors.RESET, TextColors.AQUA, " are now your allies!")));

        }

        // For each CALLER's faction member
        for(FactionMemberData callerFactionMbData : callerFaction.getMemberDataList()){

            Optional<Player> optTargetFactionConfigPlayer = Utilities.getPlayerByUniqueID(callerFactionMbData.getPlayerUniqueID());

            // Check if the player exists / is online
            // Notify the player of the alliance creation
            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.AQUA, "", targetFaction.getDisplayName(), TextColors.RESET, TextColors.AQUA, " are now your allies!")));

        }

    }

}
