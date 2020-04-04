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
import org.spongepowered.api.effect.sound.SoundTypes;
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

    private void kickPlayer(Player caller, Player targetPlayer){

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

        XFaction callerFaction = optCallerFaction.get();

        if(!XUtilities.getPlayerFactionPermissions(caller).isPresent() || !XUtilities.getPlayerFactionPermissions(caller).get().getManage()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

        if(caller.equals(targetPlayer)) { caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You cannot kick yourself, use /factions leave instead.")); return; }

        Optional<XFaction> optTargetPlayerFaction = XUtilities.getPlayerFaction(targetPlayer);

        if(!optTargetPlayerFaction.isPresent()) { caller.sendMessage(XError.XERROR_NOTAMEMBER.getDesc()); return; }

        XFaction targetPlayerFaction = optTargetPlayerFaction.get();

        if(targetPlayerFaction != callerFaction) { caller.sendMessage(XError.XERROR_NOTAMEMBER.getDesc()); return; }

        if(callerFaction.getOwner().equals(targetPlayer.getUniqueId())) { caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        if(!XUtilities.getMemberDataForPlayer(targetPlayer).isPresent()) { caller.sendMessage(XError.XERROR_UNKNOWN.getDesc()); return;}

        callerFaction.getMemberDataList().remove(XUtilities.getMemberDataForPlayer(targetPlayer).get());

        XManager.getXManager().writeFactionsConfigurationFile();
        XTabListManager.refreshTabLists();

        for(XFactionMemberData mbData : callerFaction.getMemberDataList()){

            Optional<Player> optPly = XUtilities.getPlayerByUniqueID(mbData.getPlayerUniqueID());

            if(optPly.isPresent()){

                optPly.get().playSound(SoundTypes.AMBIENT_CAVE, optPly.get().getPosition(), 0.75);
                optPly.get().sendMessage(Text.of(TextColors.RED, "[Factions] | " , targetPlayer.getName() , " has been kicked from the faction by " , caller.getName()));

            }

        }

        targetPlayer.sendMessage(Text.of(TextColors.RED, "[Factions] | You've been kicked from your faction by ", caller.getName()));
        targetPlayer.playSound(SoundTypes.AMBIENT_CAVE, caller.getPosition(), 0.75);
        XManager.getXManager().writeFactionsConfigurationFile();

    }

}
