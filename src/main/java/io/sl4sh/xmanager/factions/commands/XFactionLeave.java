package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XFactionLeave implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;
            leaveFaction(ply);

        }
        else{

            src.sendMessage(Text.of((XError.XERROR_PLAYERCOMMAND.getDesc())));

        }

        return CommandResult.success();

    }

    private Boolean leaveFaction(Player ply){

        XFaction targetFaction = XFactionCommandManager.getPlayerFaction(ply);

        if(targetFaction != null){

            if(targetFaction.getFactionOwner().equals(ply.getName())){

                ply.sendMessage(Text.of("\u00a7cYou can't leave the faction you own. Use disband to disband the faction or setowner to set a new owner."));
                return false;

            }
            else{

                XFactionMemberData targetMbdata = null;

                for(XFactionMemberData mbData : targetFaction.getFactionMembers()){

                    if(mbData.getPlayerName().equals(ply.getName())){

                        targetMbdata = mbData;
                        break;

                    }

                }

                if(targetFaction.getFactionMembers().contains(targetMbdata)){

                    targetFaction.getFactionMembers().remove(targetMbdata);
                    ply.sendMessage(Text.of("\u00a7aSuccessfully left faction " + targetFaction.getFactionName() + "\u00a7a!"));
                    XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(ply).setPlayerFaction("");
                    XTabListManager.refreshTabLists();
                    return true;

                }
                else{

                    ply.sendMessage(Text.of("\u00a7cFailed to leave the faction : Unknown error."));
                    return false;

                }

            }

        }
        else{

            ply.sendMessage(Text.of((XError.XERROR_NOXF.getDesc())));
            return false;

        }

    }

}
