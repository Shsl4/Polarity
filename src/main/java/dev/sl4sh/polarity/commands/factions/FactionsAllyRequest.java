package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.FactionCommandElement;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.enums.PolarityInfo;
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

public class FactionsAllyRequest implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Request an alliance to another faction."))
                .arguments(new FactionCommandElement(Text.of("factionName")))
                .permission("polarity.factions.ally.request")
                .executor(new FactionsAllyRequest())
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

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();


    }

    private void requestAllyToFaction(Player caller, String targetFactionName){

        Optional<Faction> optTargetFaction = Utilities.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(Text.of(PolarityErrors.XERROR_XFNULL.getDesc())); return; }

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        // Return if the provided faction doesn't exist
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(Text.of(PolarityErrors.XERROR_NOXF.getDesc())); return; }

        // Get the safe references to our factions
        Faction targetFaction = optTargetFaction.get();
        Faction callerFaction = optCallerFaction.get();

        if(callerFaction == targetFaction) { caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You cannot ally your own faction!")); return;}

        // Return if the faction is already allied
        if(targetFaction.isFactionAllied(callerFaction)) { caller.sendMessage(Text.of(PolarityInfo.XERROR_ALREADYALLIED.getDesc())); return;}

        Optional<FactionMemberData> optTargetMemberData = Utilities.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getManage()) {  caller.sendMessage(Text.of(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc())); return; }

        // Return if an ally request was already submitted to the provided faction
        if(callerFaction.getAllyInvites().contains(targetFaction.getUniqueId())) {  caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You already submitted an ally request to this faction. Ask their owner to accept it.")); return; }

        // Add the provided faction to the caller's faction alliance invitation list and notify the caller
        callerFaction.getAllyInvites().add(targetFaction.getUniqueId());

        caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully submitted your ally request to " , targetFaction.getDisplayName()));

        // For each TARGET faction's member
        for(FactionMemberData playerData : targetFaction.getMemberDataList()){

            // If the player is allowed to configure the faction
            if(playerData.permissions.getManage()){

                Optional<Player> optTargetFactionConfigPlayer = Utilities.getPlayerByUniqueID(playerData.getPlayerUniqueID());

                // Check if the player exists / is online
                // Notify the player that they received an alliance request
                optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.AQUA, "[Factions] | ", callerFaction.getDisplayName(), TextColors.RESET, TextColors.AQUA, " just sent you an alliance request! Type /factions ally accept ", callerFaction.getName(), " to accept the alliance or /factions ally decline ", callerFaction.getName(), " to decline it.")));

            }

        }

    }

}
