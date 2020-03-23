package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.*;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class XFactionCreate implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            if(createFaction(ply, args.getOne("factionName").get().toString().toLowerCase())){

                XManager.xLogSuccess(ply.getName() + " created a faction named " + args.getOne("factionName").get().toString().toLowerCase());
                XManager.getXManager().writePlayerInfo();

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private Boolean createFaction(Player creator, String newFactionName) {

        String factionName = XManager.getStringWithoutModifiers(newFactionName);

        if(!XFactionCommandManager.getPlayerFaction(creator).isPresent()){

            if(!XFactionCommandManager.doesFactionExist(factionName)){

                List<XFactionMemberData> factionMembers = new ArrayList<>();
                XFactionMemberData mbData = new XFactionMemberData(creator.getName(), new XFactionPermissionData(true, true, true));
                factionMembers.add(mbData);
                XFaction faction = new XFaction(factionName, "", "", creator.getName(), factionMembers, new ArrayList<>(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

                XFactionContainer factions = XManager.getXManager().getFactionContainer();

                if(factions != null){

                    factions.addFaction(faction);
                    XManager.getXManager().writeFactions();
                    XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(creator).setPlayerFaction(faction.getFactionName());
                    XManager.getXManager().writePlayerInfo();
                    creator.sendMessage(Text.of("\u00a7a[Factions] | Successfully created your faction named " + factionName + "\u00a7a!"));
                    XTabListManager.refreshTabLists();
                    return true;


                }
                else{

                    creator.sendMessage(Text.of(XError.XERROR_FILEREADFAIL.getDesc()));

                }

            }
            else{

                creator.sendMessage(Text.of("\u00a7c[Factions] | A faction named " + factionName + " already exists!"));

            }

        }
        else{

            creator.sendMessage(Text.of(XError.XERROR_XFMEMBER.getDesc()));

        }

        return false;

    }



}
