package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
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

            if(args.getOne("playerName").isPresent()){

                kickPlayer(ply, (Player)args.getOne("playerName").get());

            }
            else{

                ply.sendMessage(Text.of(XError.XERROR_NULLPLAYER.getDesc()));

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void kickPlayer(Player Caller, Player TargetPlayer){

        Optional<XFaction> optCallerFaction = XFactionCommandManager.getPlayerFaction(Caller);

        if(!optCallerFaction.isPresent()) { Caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        XFaction callerFaction = optCallerFaction.get();

        if(!XFactionCommandManager.getPlayerFactionPermissions(Caller).isPresent() || !XFactionCommandManager.getPlayerFactionPermissions(Caller).get().getConfigure()) { Caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        Optional<XFaction> optTargetPlayerFaction = XFactionCommandManager.getPlayerFaction(TargetPlayer);

        if(!optTargetPlayerFaction.isPresent()) { Caller.sendMessage(Text.of(XError.XERROR_NOTAMEMBER.getDesc())); return; }

        XFaction targetPlayerFaction = optTargetPlayerFaction.get();

        if(targetPlayerFaction != callerFaction) { Caller.sendMessage(Text.of(XError.XERROR_NOTAMEMBER.getDesc())); return; }

        Optional<XFactionPermissionData> optTargetPerms = XFactionCommandManager.getPlayerFactionPermissions(TargetPlayer);

        if(callerFaction.getFactionOwner().equals(TargetPlayer.getName())) { Caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        callerFaction.getFactionMembers().remove(XFactionCommandManager.getMemberDataForPlayer(TargetPlayer).get());
        XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(TargetPlayer).setPlayerFaction(null);

        XManager.getXManager().writeFactions();
        XManager.getXManager().writePlayerInfo();
        XTabListManager.refreshTabLists();

        for(XFactionMemberData mbData : callerFaction.getFactionMembers()){

            Optional<Player> optPly = XFactionCommandManager.getPlayerByName(mbData.playerName);
            optPly.ifPresent(player -> player.sendMessage(Text.of("\u00a7c[Factions] | " + TargetPlayer.getName() + " has been kicked from the faction by " + Caller.getName())));

        }

        TargetPlayer.sendMessage(Text.of("\u00a7c[Factions] | You've been kicked from your faction by " + Caller.getName()));
        XManager.getXManager().writeFactions();
        XManager.getXManager().writePlayerInfo();

    }

}
