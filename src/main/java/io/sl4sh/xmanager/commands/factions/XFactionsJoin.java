package io.sl4sh.xmanager.commands.factions;

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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class XFactionsJoin implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Joins a faction."))
                .arguments(GenericArguments.string(Text.of("factionName")))
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

    private void joinFaction(Player ply, String factionName){

        if(XUtilities.doesFactionExist(factionName)){

            Optional<XFaction> optFac = XUtilities.getFactionByName(factionName);

            if(!optFac.isPresent()) { ply.sendMessage(XError.XERROR_XFNULL.getDesc()); return; }

            XFaction targetFaction = optFac.get();

            if(targetFaction.getFactionInvites().contains(ply.getName())){

                targetFaction.getFactionMembers().add(new XFactionMemberData(ply.getName(), new XFactionPermissionData(false, true, false)));

                targetFaction.getFactionInvites().remove(ply.getName());

                ply.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully joined the faction ",  targetFaction.getFactionDisplayName(), TextColors.GREEN, "!"));

                for(XFactionMemberData mbData : targetFaction.getFactionMembers()){

                    if(mbData.getPlayerName().equals(ply.getName())) { continue; }

                    Optional<Player> optPlayer = XUtilities.getPlayerByName(mbData.getPlayerName());

                    optPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.YELLOW, "[Factions] | ", TextColors.LIGHT_PURPLE, ply.getName(), TextColors.YELLOW, " just joined your faction!")));

                }

                XManager.getXManager().writeFactionsConfigurationFile();
                XTabListManager.refreshTabLists();

            }
            else{

                ply.sendMessage(XError.XERROR_NOTINVITED.getDesc());

            }

        }
        else{

            ply.sendMessage(XError.XERROR_XFNULL.getDesc());

        }

    }

}
