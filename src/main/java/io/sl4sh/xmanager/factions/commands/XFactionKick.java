package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import io.sl4sh.xmanager.factions.XFactionMemberRank;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import io.sl4sh.xmanager.player.XPlayer;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionKick implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;
            kickPlayer(ply, args.getOne("playerName").get().toString());

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void kickPlayer(Player Caller, String TargetName){

        XFaction CallerFaction = XFactionCommandManager.getPlayerFaction(Caller);

        if(CallerFaction == null) { Caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        if(!XFactionCommandManager.getPlayerFactionPermissions(Caller).getConfigure()) { Caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        Optional<Player> OptTarget = XFactionCommandManager.getPlayerByName(TargetName);

        if(!OptTarget.isPresent()) { Caller.sendMessage(Text.of(XError.XERROR_NULLPLAYER.getDesc())); return; }

        Player TargetPlayer = OptTarget.get();

        if(XFactionCommandManager.getPlayerFaction(TargetPlayer) != CallerFaction) { Caller.sendMessage(Text.of(XError.XERROR_NOTAMEMBER.getDesc())); return; }

        XFactionPermissionData TargetPerms = XFactionCommandManager.getPlayerFactionPermissions(TargetPlayer);

        if(TargetPerms.getRank() == XFactionMemberRank.Owner) { Caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        CallerFaction.getFactionMembers().remove(XFactionCommandManager.getMemberDataForPlayer(TargetPlayer));
        XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(TargetPlayer).setPlayerFaction(null);

        XManager.getXManager().writeFactions();
        XManager.getXManager().writePlayerInfo();
        XTabListManager.refreshTabLists();

        String modDPName = CallerFaction.getFactionDisplayName();
        modDPName = modDPName.replace("&", "\u00a7");

        for(XFactionMemberData mbData : CallerFaction.getFactionMembers()){

            Optional<Player> optPly = XFactionCommandManager.getPlayerByName(mbData.playerName);
            optPly.ifPresent(player -> player.sendMessage(Text.of("\u00a7c " + TargetName + "has been kicked from the faction by " + Caller.getName())));

        }

        TargetPlayer.sendMessage(Text.of("\u00a7cYou've been kicked from your faction by " + Caller.getName()));

    }

}
