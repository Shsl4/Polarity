package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionContainer;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class XFactionCommandManager implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        XFactionHelp.printFactionsHelp(src);
        return CommandResult.success();

    }

    static public Boolean doesFactionExist(String factionName) {

        XFactionContainer factionsContainer = XManager.getXManager().getFactionContainer();

        if(factionsContainer != null) {

            for(XFaction faction : factionsContainer.getFactionList()){

                if(faction.getFactionName().equals(factionName)){

                    return true;

                }

            }

        }

        return false;

    }

    static public Optional<XFaction> getFactionByName(String factionName){

        XFactionContainer factionsContainer = XManager.getXManager().getFactionContainer();

        if(factionsContainer != null) {

            for(XFaction faction : factionsContainer.getFactionList()){

                if(faction.getFactionName().equals(factionName)){

                    return Optional.of(faction);

                }

            }

        }

        return Optional.empty();

    }


    static public Optional<XFactionPermissionData> getPlayerFactionPermissions(Player ply) {

        if (getPlayerFaction(ply).isPresent()) {

            for (XFactionMemberData mbData : getPlayerFaction(ply).get().getFactionMembers()) {

                if (mbData.getPlayerName().equals(ply.getName())) {

                    return Optional.ofNullable(mbData.getPermissions());

                }

            }

        }

        return Optional.empty();

    }

    static public Optional<XFactionMemberData> getMemberDataForPlayer(Player ply) {

        if (getPlayerFaction(ply).isPresent()) {

            for (XFactionMemberData mbData : getPlayerFaction(ply).get().getFactionMembers()) {

                if (mbData.getPlayerName().equals(ply.getName())) {

                    return Optional.of(mbData);

                }

            }

        }

        return Optional.empty();

    }


    static public Optional<XFaction> getPlayerFaction(Player player){

        XFactionContainer fContainer = XManager.getXManager().getFactionContainer();

        if(fContainer != null){

            for(XFaction faction : fContainer.getFactionList()){

                for(XFactionMemberData memberData : faction.getFactionMembers()){

                    if(memberData.getPlayerName().equals(player.getName())){

                        return Optional.of(faction);

                    }

                }

            }

        }

        return Optional.empty();

    }

    public static Optional<Player> getPlayerByName(String PlayerName){

        return Sponge.getServer().getPlayer(PlayerName);

    }

}


