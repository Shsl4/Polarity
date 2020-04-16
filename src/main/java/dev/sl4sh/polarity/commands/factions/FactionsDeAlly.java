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

public class FactionsDeAlly implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Destroy an alliance with another faction."))
                .arguments(new FactionCommandElement(Text.of("factionName")))
                .permission("polarity.factions.deally")
                .executor(new FactionsDeAlly())
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

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void deallyFaction(Player caller, String targetFactionName){

        Optional<Faction> optTargetFaction = Utilities.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(PolarityErrors.XERROR_XFNULL.getDesc()); return; }

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        // Return if the caller doesn't have any faction
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(PolarityErrors.XERROR_NOXF.getDesc()); return; }

        // Get the safe references to our factions
        Faction targetFaction = optTargetFaction.get();
        Faction callerFaction = optCallerFaction.get();

        // Return if the faction not an ally
        if(!targetFaction.isFactionAllied(callerFaction)) { caller.sendMessage(PolarityInfo.XERROR_NOTALLIED.getDesc()); return;}

        Optional<FactionMemberData> optTargetMemberData = Utilities.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getManage()) {  caller.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc()); return; }

        targetFaction.getAllies().remove(callerFaction.getUniqueId());
        callerFaction.getAllies().remove(targetFaction.getUniqueId());

        // For each TARGET's faction member
        for(FactionMemberData targetFactionMbData : targetFaction.getMemberDataList()){

            Optional<Player> optTargetFactionConfigPlayer = Utilities.getPlayerByUniqueID(targetFactionMbData.getPlayerUniqueID());

            // Check if the player exists / is online
            // Notify the player of the alliance destruction
            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.RED, "[Factions] | " , callerFaction.getDisplayName(), TextColors.RESET, TextColors.RED, " are no longer your allies!")));

        }

        // For each CALLER's faction member
        for(FactionMemberData callerFactionMbData : callerFaction.getMemberDataList()){

            Optional<Player> optTargetFactionConfigPlayer = Utilities.getPlayerByUniqueID(callerFactionMbData.getPlayerUniqueID());

            // Check if the player exists / is online
            // Notify the player of the alliance destruction
            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.RED, "[Factions] | ", targetFaction.getDisplayName(), TextColors.RED, " are no longer your allies!")));

        }

    }

}
