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

            Player ply = (Player) src;
            leaveFaction(ply);

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void leaveFaction(Player ply){

        Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(ply);

        if(optTargetFaction.isPresent()){

            XFaction targetFaction = optTargetFaction.get();

            if(targetFaction.getFactionOwner().equals(ply.getName())){

                ply.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You can't leave the faction you own. Use /factions disband instead, or /factions setowner to set a new owner."));

            }
            else{

                Optional<XFactionMemberData> optMemberData = XUtilities.getMemberDataForPlayer(ply);

                if(optMemberData.isPresent()){

                    if(targetFaction.getFactionMembers().remove(optMemberData.get())){

                        ply.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully left faction " , XUtilities.getStringReplacingModifierChar(targetFaction.getFactionDisplayName()) , TextColors.GREEN, "!"));
                        XTabListManager.refreshTabLists();

                    }
                    else{

                        ply.sendMessage(XError.XERROR_UNKNOWN.getDesc());

                    }


                }
                else{

                    ply.sendMessage(XError.XERROR_UNKNOWN.getDesc());

                }

            }

        }
        else{

            ply.sendMessage(Text.of((XError.XERROR_NOXF.getDesc())));

        }

    }

}
