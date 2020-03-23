package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
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

public class XFactionDeAlly implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            deallyFaction(ply, args.getOne("factionName").get().toString());

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void deallyFaction(Player caller, String targetFactionName){

        Optional<XFaction> optTargetFaction = XFactionCommandManager.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(Text.of(XError.XERROR_XFNULL.getDesc())); return; }

        Optional<XFaction> optCallerFaction = XFactionCommandManager.getPlayerFaction(caller);

        // Return if the caller doesn't have any faction
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        // Get the safe references to our factions
        XFaction targetFaction = optTargetFaction.get();
        XFaction callerFaction = optCallerFaction.get();

        // Return if the faction not an ally
        if(!targetFaction.isFactionAllied(callerFaction)) { caller.sendMessage(Text.of(XError.XERROR_NOTALLIED.getDesc())); return;}

        Optional<XFactionMemberData> optTargetMemberData = XFactionCommandManager.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getConfigure()) {  caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        targetFaction.getFactionAllies().remove(callerFaction.getFactionName());
        callerFaction.getFactionAllies().remove(targetFactionName);

        // For each TARGET's faction member
        for(XFactionMemberData targetFactionMbData : targetFaction.getFactionMembers()){

            Optional<Player> optTargetFactionConfigPlayer = XFactionCommandManager.getPlayerByName(targetFactionMbData.playerName);

            // Check if the player exists / is online
            // Notify the player of the alliance destruction
            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of("\u00a7c[Factions] | " + callerFaction.getFactionDisplayName() + "\u00a7c are no longer your allies!")));

        }

        // For each CALLER's faction member
        for(XFactionMemberData callerFactionMbData : callerFaction.getFactionMembers()){

            Optional<Player> optTargetFactionConfigPlayer = XFactionCommandManager.getPlayerByName(callerFactionMbData.playerName);

            // Check if the player exists / is online
            // Notify the player of the alliance destruction
            optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of("\u00a7c[Factions] | " + targetFaction.getFactionDisplayName() + "\u00a7c are no longer your allies!")));

        }

    }

}
