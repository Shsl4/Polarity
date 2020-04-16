package dev.sl4sh.polarity.commands.factions;

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
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class FactionsLeave implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Leaves your faction."))
                .permission("polarity.factions.leave")
                .executor(new FactionsLeave())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player caller = (Player) src;
            if(!leaveFaction(caller)){

                caller.playSound(SoundTypes.BLOCK_NOTE_BASS, caller.getPosition(), 0.75);

            }

        }
        else{

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private boolean leaveFaction(Player caller){

        Optional<Faction> optTargetFaction = Utilities.getPlayerFaction(caller);

        if(optTargetFaction.isPresent()){

            Faction targetFaction = optTargetFaction.get();

            if(targetFaction.getOwner().equals(caller.getUniqueId())){

                caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You can't leave the faction you own. Use /factions disband instead, or /factions setowner to set a new owner."));

            }
            else{

                Optional<FactionMemberData> optMemberData = Utilities.getMemberDataForPlayer(caller);

                if(optMemberData.isPresent()){

                    if(targetFaction.getMemberDataList().remove(optMemberData.get())){

                        caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully left faction " , targetFaction.getDisplayName(), TextColors.GREEN, "!"));
                        caller.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, caller.getPosition(), 0.75);

                        for(FactionMemberData mbData : targetFaction.getMemberDataList()){

                            if(mbData.getPlayerUniqueID().equals(caller.getUniqueId())) { continue; }

                            Optional<Player> optPlayer = Utilities.getPlayerByUniqueID(mbData.getPlayerUniqueID());

                            if(optPlayer.isPresent()){

                                optPlayer.get().sendMessage(Text.of(TextColors.RED, "[Factions] | ", TextColors.LIGHT_PURPLE, caller.getName(), TextColors.RED, " just left your faction!"));
                                optPlayer.get().playSound(SoundTypes.BLOCK_NOTE_BASS, optPlayer.get().getPosition(), 0.75);

                            }

                        }

                        TabListManager.refreshTabLists();

                    }
                    else{

                        caller.sendMessage(PolarityErrors.XERROR_UNKNOWN.getDesc());

                    }


                }
                else{

                    caller.sendMessage(PolarityErrors.XERROR_UNKNOWN.getDesc());

                }

            }

        }
        else{

            caller.sendMessage(Text.of((PolarityErrors.XERROR_NOXF.getDesc())));

        }

        return false;

    }

}
