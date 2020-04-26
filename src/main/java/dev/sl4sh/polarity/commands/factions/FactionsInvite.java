package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.TabListManager;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.enums.PolarityInfo;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;

public class FactionsInvite implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Invites a player to your faction."))
                .arguments(GenericArguments.player(Text.of("playerName")))
                .permission("polarity.factions.invite")
                .executor(new FactionsInvite())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player ply = (Player) src;

            // If the argument exists (this should theoretically always be true)
            if(args.getOne("playerName").isPresent()){

                invitePlayer(ply, (Player)args.getOne("playerName").get());

            }
            else{

                // Will send the player an error message
                ply.sendMessage(PolarityErrors.NULLPLAYER.getDesc());

            }

        }
        else{

            src.sendMessage(PolarityErrors.PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void invitePlayer(Player caller, Player target){

        if(Utilities.getPlayerFaction(caller).isPresent()){

            Faction callerFac = Utilities.getPlayerFaction(caller).get();

            Optional<FactionPermissionData> optMemberData = Utilities.getPlayerFactionPermissions(caller);

            if(!optMemberData.isPresent()) { caller.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc()); return; }

            if(optMemberData.get().getManage()){

                if(!Utilities.getPlayerFaction(target).isPresent()){

                    caller.sendMessage(Text.of(TextColors.GREEN, "Successfully invited player '" , target.getName() , "' to your faction."));

                    Text joinText = Text.builder().onClick(TextActions.executeCallback((source) -> joinFaction(target, callerFac))).append(Text.of(TextStyles.UNDERLINE, TextColors.GOLD, "Click here")).build();

                    target.sendMessage(Text.of(TextColors.GREEN,"You've been invited to join the faction '" , callerFac.getDisplayName(), TextColors.GREEN, "' by " , caller.getName() , ". ", joinText, TextStyles.RESET, TextColors.GREEN, " to join the faction."));
                    Polarity.getPolarity().writeAllConfig();

                }
                else{

                    caller.sendMessage(PolarityInfo.XERROR_XFEMEMBER.getDesc());

                }

            }
            else{

                caller.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc());

            }

        }
        else{

            caller.sendMessage(PolarityErrors.NOFACTION.getDesc());

        }

    }

    private void joinFaction(Player target, Faction targetFaction){

        targetFaction.getMemberDataList().add(new FactionMemberData(target.getUniqueId(), new FactionPermissionData(false, true, false)));
        targetFaction.getFactionChannel().addMember(target);
        target.sendMessage(Text.of(TextColors.GREEN, "Successfully joined the faction ",  targetFaction.getDisplayName(), TextColors.GREEN, "!"));
        target.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, target.getPosition(), 0.25);

        for(FactionMemberData mbData : targetFaction.getMemberDataList()){

            if(mbData.getPlayerUniqueID().equals(target.getUniqueId())) { continue; }

            Optional<Player> optPlayer = Utilities.getPlayerByUniqueID(mbData.getPlayerUniqueID());

            if(optPlayer.isPresent()){

                optPlayer.get().sendMessage(Text.of(TextColors.YELLOW, "", TextColors.LIGHT_PURPLE, target.getName(), TextColors.YELLOW, " just joined your faction!"));
                optPlayer.get().playSound(SoundTypes.BLOCK_NOTE_BELL, optPlayer.get().getPosition(), 0.25);

            }

        }

        Polarity.getPolarity().writeAllConfig();
        TabListManager.refreshTabLists();

    }

}
