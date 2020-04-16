package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import dev.sl4sh.polarity.tablist.TabListManager;
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

public class FactionsKick implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Kicks a member of your faction."))
                .arguments(GenericArguments.player(Text.of("playerName")))
                .permission("polarity.factions.kick")
                .executor(new FactionsKick())
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

                ply.sendMessage(PolarityErrors.XERROR_NULLPLAYER.getDesc());

            }

        }
        else{

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void kickPlayer(Player caller, Player targetPlayer){

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(PolarityErrors.XERROR_NOXF.getDesc()); return; }

        Faction callerFaction = optCallerFaction.get();

        if(!Utilities.getPlayerFactionPermissions(caller).isPresent() || !Utilities.getPlayerFactionPermissions(caller).get().getManage()) { caller.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc()); return; }

        if(caller.equals(targetPlayer)) { caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You cannot kick yourself, use /factions leave instead.")); return; }

        Optional<Faction> optTargetPlayerFaction = Utilities.getPlayerFaction(targetPlayer);

        if(!optTargetPlayerFaction.isPresent()) { caller.sendMessage(PolarityErrors.XERROR_NOTAMEMBER.getDesc()); return; }

        Faction targetPlayerFaction = optTargetPlayerFaction.get();

        if(targetPlayerFaction != callerFaction) { caller.sendMessage(PolarityErrors.XERROR_NOTAMEMBER.getDesc()); return; }

        if(callerFaction.getOwner().equals(targetPlayer.getUniqueId())) { caller.sendMessage(Text.of(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc())); return; }

        if(!Utilities.getMemberDataForPlayer(targetPlayer).isPresent()) { caller.sendMessage(PolarityErrors.XERROR_UNKNOWN.getDesc()); return;}

        callerFaction.getMemberDataList().remove(Utilities.getMemberDataForPlayer(targetPlayer).get());

        Polarity.getPolarity().writeAllConfig();
        TabListManager.refreshTabLists();

        for(FactionMemberData mbData : callerFaction.getMemberDataList()){

            Optional<Player> optPly = Utilities.getPlayerByUniqueID(mbData.getPlayerUniqueID());

            if(optPly.isPresent()){

                optPly.get().playSound(SoundTypes.AMBIENT_CAVE, optPly.get().getPosition(), 0.75);
                optPly.get().sendMessage(Text.of(TextColors.RED, "[Factions] | " , targetPlayer.getName() , " has been kicked from the faction by " , caller.getName()));

            }

        }

        targetPlayer.sendMessage(Text.of(TextColors.RED, "[Factions] | You've been kicked from your faction by ", caller.getName()));
        targetPlayer.playSound(SoundTypes.AMBIENT_CAVE, caller.getPosition(), 0.75);
        Polarity.getPolarity().writeAllConfig();

    }

}
