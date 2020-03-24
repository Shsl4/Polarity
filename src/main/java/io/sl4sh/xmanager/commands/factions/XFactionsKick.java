package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.tablist.XTabListManager;
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

public class XFactionsKick implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Kicks a member of your faction."))
                .arguments(GenericArguments.player(Text.of("playerName")))
                .permission("xmanager.factions.kick")
                .executor(new XFactionsKick())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            if(args.getOne("playerName").isPresent()){

                kickPlayer(ply, (Player)args.getOne("playerName").get());

            }
            else{

                ply.sendMessage(XError.XERROR_NULLPLAYER.getDesc());

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void kickPlayer(Player Caller, Player TargetPlayer){

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(Caller);

        if(!optCallerFaction.isPresent()) { Caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

        XFaction callerFaction = optCallerFaction.get();

        if(!XUtilities.getPlayerFactionPermissions(Caller).isPresent() || !XUtilities.getPlayerFactionPermissions(Caller).get().getConfigure()) { Caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

        if(Caller.equals(TargetPlayer)) { Caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You cannot kick yourself, use /factions leave instead.")); return; }

        Optional<XFaction> optTargetPlayerFaction = XUtilities.getPlayerFaction(TargetPlayer);

        if(!optTargetPlayerFaction.isPresent()) { Caller.sendMessage(XError.XERROR_NOTAMEMBER.getDesc()); return; }

        XFaction targetPlayerFaction = optTargetPlayerFaction.get();

        if(targetPlayerFaction != callerFaction) { Caller.sendMessage(XError.XERROR_NOTAMEMBER.getDesc()); return; }

        if(callerFaction.getFactionOwner().equals(TargetPlayer.getName())) { Caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        if(!XUtilities.getMemberDataForPlayer(TargetPlayer).isPresent()) { Caller.sendMessage(XError.XERROR_UNKNOWN.getDesc()); return;}

        callerFaction.getFactionMembers().remove(XUtilities.getMemberDataForPlayer(TargetPlayer).get());

        XManager.getXManager().writeFactionsConfigurationFile();
        XTabListManager.refreshTabLists();

        for(XFactionMemberData mbData : callerFaction.getFactionMembers()){

            Optional<Player> optPly = XUtilities.getPlayerByName(mbData.playerName);
            optPly.ifPresent(player -> player.sendMessage(Text.of(TextColors.RED, "[Factions] | " , TargetPlayer.getName() , " has been kicked from the faction by " , Caller.getName())));

        }

        TargetPlayer.sendMessage(Text.of(TextColors.RED, "[Factions] | You've been kicked from your faction by ", Caller.getName()));
        XManager.getXManager().writeFactionsConfigurationFile();

    }

}
