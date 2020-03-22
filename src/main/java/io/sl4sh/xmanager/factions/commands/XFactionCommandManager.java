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

    static public XFaction getFaction(String factionName){

        XFactionContainer factionsContainer = XManager.getXManager().getFactionContainer();

        if(factionsContainer != null) {

            for(XFaction faction : factionsContainer.getFactionList()){

                if(faction.getFactionName().equals(factionName)){

                    return faction;

                }

            }

        }

        return null;

    }


    static public XFactionPermissionData getPlayerFactionPermissions(Player ply) {

        if (getPlayerFaction(ply) != null) {

            for (XFactionMemberData mbData : getPlayerFaction(ply).getFactionMembers()) {

                if (mbData.getPlayerName().equals(ply.getName())) {

                    return mbData.getPermissions();

                }

            }

        }

        return null;

    }

    static public XFactionMemberData getMemberDataForPlayer(Player ply) {

        if (getPlayerFaction(ply) != null) {

            for (XFactionMemberData mbData : getPlayerFaction(ply).getFactionMembers()) {

                if (mbData.getPlayerName().equals(ply.getName())) {

                    return mbData;

                }

            }

        }

        return null;

    }


    static public XFaction getPlayerFaction(Player player){

        XFactionContainer fContainer = XManager.getXManager().getFactionContainer();

        if(fContainer != null){

            for(XFaction faction : fContainer.getFactionList()){

                for(XFactionMemberData memberData : faction.getFactionMembers()){

                    if(memberData.getPlayerName().equals(player.getName())){

                        return faction;

                    }

                }

            }

        }

        return null;

    }

    public static Optional<Player> getPlayerByName(String PlayerName){

        return Sponge.getServer().getPlayer(PlayerName);

    }

}


