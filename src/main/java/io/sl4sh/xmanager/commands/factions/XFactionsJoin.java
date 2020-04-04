package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.commands.elements.XFactionCommandElement;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
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

public class XFactionsJoin implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Joins a faction."))
                .arguments(new XFactionCommandElement(Text.of("factionName")))
                .permission("xmanager.factions.join")
                .executor(new XFactionsJoin())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player ply = (Player) src;

            // If the argument exists (this should theoretically always be true)
            if(args.getOne("factionName").isPresent()){

                joinFaction(ply, args.getOne("factionName").get().toString());

            }
            else{

                // Will send the player an error message
                joinFaction(ply, "");

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void joinFaction(Player caller, String factionName){

        if(XUtilities.doesFactionExistByName(factionName)){

            Optional<XFaction> optFac = XUtilities.getFactionByName(factionName);

            if(!optFac.isPresent()) { caller.sendMessage(XError.XERROR_XFNULL.getDesc()); return; }

            XFaction targetFaction = optFac.get();

            if(targetFaction.getPlayerInvites().contains(caller.getUniqueId())){

                targetFaction.getMemberDataList().add(new XFactionMemberData(caller.getUniqueId(), new XFactionPermissionData(false, true, false)));

                targetFaction.getPlayerInvites().remove(caller.getUniqueId());

                caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully joined the faction ",  targetFaction.getDisplayName(), TextColors.GREEN, "!"));
                caller.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, caller.getPosition(), 0.75);

                for(XFactionMemberData mbData : targetFaction.getMemberDataList()){

                    if(mbData.getPlayerUniqueID().equals(caller.getUniqueId())) { continue; }

                    Optional<Player> optPlayer = XUtilities.getPlayerByUniqueID(mbData.getPlayerUniqueID());

                    if(optPlayer.isPresent()){

                        optPlayer.get().sendMessage(Text.of(TextColors.YELLOW, "[Factions] | ", TextColors.LIGHT_PURPLE, caller.getName(), TextColors.YELLOW, " just joined your faction!"));
                        optPlayer.get().playSound(SoundTypes.BLOCK_NOTE_BELL, optPlayer.get().getPosition(), 0.75);

                    }

                }

                XManager.getXManager().writeFactionsConfigurationFile();
                XTabListManager.refreshTabLists();

            }
            else{

                caller.sendMessage(XError.XERROR_NOTINVITED.getDesc());

            }

        }
        else{

            caller.sendMessage(XError.XERROR_XFNULL.getDesc());

        }

    }

}
