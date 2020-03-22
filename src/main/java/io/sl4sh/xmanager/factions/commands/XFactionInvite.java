package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XFactionInvite implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            if(args.getOne("playerName").isPresent()){

                invitePlayer(ply, (Player)args.getOne("playerName").get());

            }
            else{

                ply.sendMessage(Text.of(XError.XERROR_NULLPLAYER.getDesc()));

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void invitePlayer(Player caller, Player target){

        if(XFactionCommandManager.getPlayerFaction(caller).isPresent()){

            XFaction callerFac = XFactionCommandManager.getPlayerFaction(caller).get();

            if(XFactionCommandManager.getPlayerFactionPermissions(caller).get().getConfigure()){

                if(!XFactionCommandManager.getPlayerFaction(target).isPresent()){

                    String modDPName = callerFac.getFactionDisplayName();
                    modDPName = modDPName.replace("&", "\u00a7");

                    callerFac.getFactionInvites().add(target.getName());
                    caller.sendMessage(Text.of("\u00a7aSuccessfully invited player '" + target.getName() + "' to your faction."));
                    target.sendMessage(Text.of("\u00a7aYou've been invited to join the faction '" + modDPName + "\u00a7a' by " + caller.getName() + ". Type /factions join " + callerFac.getFactionName() + " to join the faction."));
                    XManager.getXManager().writeFactions();

                }
                else{

                    caller.sendMessage(Text.of(XError.XERROR_XFEMEMBER.getDesc()));

                }

            }
            else{

                caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc()));

            }

        }
        else{

            caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc()));

        }

    }

}
