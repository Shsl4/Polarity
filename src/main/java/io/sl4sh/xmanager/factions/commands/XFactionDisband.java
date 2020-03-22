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
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionDisband implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            if(disbandFaction(ply)){

                XTabListManager.refreshTabLists();

            }
            else{

                ply.sendMessage(Text.of("\u00a7cFailed to disband the faction!"));

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private Boolean disbandFaction(Player rq){

        Optional<XFaction> optPendingDeleteFaction = XFactionCommandManager.getPlayerFaction(rq);

        if(optPendingDeleteFaction.isPresent()){

            XFaction pendingDeleteFaction = optPendingDeleteFaction.get();

            if(pendingDeleteFaction.getFactionOwner().equals(rq.getName())){

                for(XFactionMemberData memberData : pendingDeleteFaction.getFactionMembers()){

                    if(Sponge.getServer().getPlayer(memberData.getPlayerName()).isPresent()){

                        Player FacPlayer = Sponge.getServer().getPlayer(memberData.getPlayerName()).get();
                        XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(FacPlayer).setPlayerFaction("");
                        FacPlayer.sendMessage(Text.of("\u00a7c\u00a7lYour faction has been disbanded by the owner. (" + rq.getName() + ")"));

                    }

                }

                XManager.getXManager().getFactionContainer().getFactionList().remove(pendingDeleteFaction);
                XManager.getXManager().writeFactions();
                XManager.getXManager().writePlayerInfo();
                return true;

            }
            else{

                rq.sendMessage(Text.of("\u00a7cThe faction can only be disbanded by the owner."));
                return false;

            }

        }
        else{

            rq.sendMessage(Text.of(XError.XERROR_NOXF.getDesc()));
            return false;

        }

    }

}
