package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class XFactionForceDisband implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof ConsoleSource) {

            ConsoleSource serv = (ConsoleSource) src;

            if(forceDisbandFaction(args.getOne("factionName").get().toString().toLowerCase())){

                serv.sendMessage(Text.of("\u00a7aThe faction " + args.getOne("factionName").get().toString().toLowerCase() + " has been successfully disbanded."));
                XTabListManager.refreshTabLists();

            }
            else{

                serv.sendMessage(Text.of("\u00a7cFailed to disband the faction. Does it exists?"));

            }


        }
        else{

            src.sendMessage(Text.of(XError.XERROR_SERVERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private Boolean forceDisbandFaction(String factionName){

        List<XFaction> factions = XManager.getXManager().getFactionContainer().getFactionList();

        if(XFactionCommandManager.getFaction(factionName) != null){

            XFaction pendingDeleteFaction = XFactionCommandManager.getFaction(factionName);
            List<Player> plys = new ArrayList<>();

            for(XFactionMemberData memberData : pendingDeleteFaction.getFactionMembers()){

                if(Sponge.getServer().getPlayer(memberData.getPlayerName()).isPresent()){

                    plys.add(Sponge.getServer().getPlayer(memberData.getPlayerName()).get());

                }

            }

            factions.remove(pendingDeleteFaction);
            XManager.getXManager().writeFactions();

            for(Player ply : plys){

                XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(ply).setPlayerFaction("");

                ply.sendMessage(Text.of("\u00a7c\u00a7lYour faction has been disbanded by an administrator."));

            }

            XManager.getXManager().writePlayerInfo();
            return true;

        }

        return false;

    }

}
