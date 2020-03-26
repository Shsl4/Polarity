package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class XFactionsForceDisband implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Force disband a faction."))
                .permission("xmanager.factions.forcedisband")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("factionName"))))
                .executor(new XFactionsForceDisband())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof ConsoleSource) {

            ConsoleSource serv = (ConsoleSource)src;

            if(args.getOne("factionName").isPresent()){

                if(forceDisbandFaction(args.getOne("factionName").get().toString().toLowerCase())){

                    serv.sendMessage(Text.of(TextColors.GREEN, "[Factions] | The faction " , args.getOne("factionName").get().toString().toLowerCase() , " has been successfully disbanded."));
                    XTabListManager.refreshTabLists();

                }
                else{

                    serv.sendMessage(Text.of(TextColors.RED, "[Factions] | Failed to disband the faction. Does it exists?"));

                }

            }
            else{

                serv.sendMessage(XError.XERROR_UNKNOWN.getDesc());

            }

        }
        else{

            src.sendMessage(XError.XERROR_SERVERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private Boolean forceDisbandFaction(String factionName){

        List<XFaction> factions = XManager.getXManager().getFactions();

        Optional<XFaction> optPendingDeleteFaction = XUtilities.getFactionByName(factionName);

        if(optPendingDeleteFaction.isPresent()){

            XFaction pendingDeleteFaction = optPendingDeleteFaction.get();
            List<Player> players = new ArrayList<>();

            for(XFactionMemberData memberData : pendingDeleteFaction.getFactionMembers()){

                if(Sponge.getServer().getPlayer(memberData.getPlayerName()).isPresent()){

                    players.add(Sponge.getServer().getPlayer(memberData.getPlayerName()).get());

                }

            }

            factions.remove(pendingDeleteFaction);
            XManager.getXManager().writeFactionsConfigurationFile();

            for(Player ply : players){

                ply.sendMessage(Text.of(TextColors.RED, "\u00a7l[Factions] | Your faction has been disbanded by an administrator."));

            }

            return true;

        }

        return false;

    }

}
