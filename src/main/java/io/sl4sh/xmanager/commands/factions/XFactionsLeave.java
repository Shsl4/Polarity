package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.tablist.XTabListManager;
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

public class XFactionsLeave implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Leaves your faction."))
                .permission("xmanager.factions.leave")
                .executor(new XFactionsLeave())
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

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private boolean leaveFaction(Player caller){

        Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(caller);

        if(optTargetFaction.isPresent()){

            XFaction targetFaction = optTargetFaction.get();

            if(targetFaction.getOwner().equals(caller.getUniqueId())){

                caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You can't leave the faction you own. Use /factions disband instead, or /factions setowner to set a new owner."));

            }
            else{

                Optional<XFactionMemberData> optMemberData = XUtilities.getMemberDataForPlayer(caller);

                if(optMemberData.isPresent()){

                    if(targetFaction.getMemberDataList().remove(optMemberData.get())){

                        caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully left faction " , targetFaction.getDisplayName(), TextColors.GREEN, "!"));
                        caller.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, caller.getPosition(), 0.75);

                        for(XFactionMemberData mbData : targetFaction.getMemberDataList()){

                            if(mbData.getPlayerUniqueID().equals(caller.getUniqueId())) { continue; }

                            Optional<Player> optPlayer = XUtilities.getPlayerByUniqueID(mbData.getPlayerUniqueID());

                            if(optPlayer.isPresent()){

                                optPlayer.get().sendMessage(Text.of(TextColors.RED, "[Factions] | ", TextColors.LIGHT_PURPLE, caller.getName(), TextColors.RED, " just left your faction!"));
                                optPlayer.get().playSound(SoundTypes.BLOCK_NOTE_BASS, optPlayer.get().getPosition(), 0.75);

                            }

                        }

                        XTabListManager.refreshTabLists();

                    }
                    else{

                        caller.sendMessage(XError.XERROR_UNKNOWN.getDesc());

                    }


                }
                else{

                    caller.sendMessage(XError.XERROR_UNKNOWN.getDesc());

                }

            }

        }
        else{

            caller.sendMessage(Text.of((XError.XERROR_NOXF.getDesc())));

        }

        return false;

    }

}
