package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import io.sl4sh.xmanager.factions.XFactionMemberRank;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XFactionJoin implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;
            joinFaction(ply, args.getOne("factionName").get().toString());

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void joinFaction(Player ply, String factionName){

        if(XFactionCommandManager.doesFactionExist(factionName)){

            XFaction fac = XFactionCommandManager.getFaction(factionName);

            if(fac.getFactionInvites().contains(ply.getName())){

                fac.getFactionMembers().add(new XFactionMemberData(ply.getName(), new XFactionPermissionData(false, true, false, XFactionMemberRank.Member)));
                XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(ply).setPlayerFaction(factionName);
                XManager.getXManager().writeFactions();
                XManager.getXManager().writePlayerInfo();

                String modDPName = fac.getFactionDisplayName();
                modDPName = modDPName.replace("&", "\u00a7");

                ply.sendMessage(Text.of("\u00a7aSuccessfully joined the faction " + modDPName + "\u00a7a!"));

                XTabListManager.refreshTabLists();

            }
            else{

                ply.sendMessage(Text.of(XError.XERROR_NOTINVITED.getDesc()));

            }

        }
        else{

            ply.sendMessage(Text.of(XError.XERROR_XFNULL.getDesc()));

        }

    }

}
