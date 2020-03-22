package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.commands.XManagerCommandManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionSetOwner implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {

            Player ply = (Player) src;

            if(args.getOne("playerName").isPresent()){

                setFactionOwner(ply, (Player)args.getOne("playerName").get());

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

    private void setFactionOwner(Player caller, Player newOwner){

        if(caller == newOwner) { caller.sendMessage(Text.of("\u00a7bYou are already the owner of this faction.")); return; }

        Optional<XFaction> optCallerFaction = XFactionCommandManager.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        XFaction callerFaction = optCallerFaction.get();

        Optional<XFaction> optTargetFaction = XFactionCommandManager.getPlayerFaction(newOwner);

        if(!optTargetFaction.isPresent()) { caller.sendMessage(Text.of(XError.XERROR_NOTAMEMBER.getDesc())); return; }

        if(optTargetFaction.get() != callerFaction) { caller.sendMessage(Text.of(XError.XERROR_NOTAMEMBER.getDesc())); return; }

        if(callerFaction.isOwner(caller.getName())){

            if(callerFaction.setPermissionDataForPlayer(newOwner, new XFactionPermissionData(true, true, true))){

                callerFaction.setFactionOwner(newOwner.getName());
                XManager.getXManager().writeFactions();

                caller.sendMessage(Text.of("\u00a7aSuccessfully set " + newOwner.getName() + " as the new faction owner!"));
                newOwner.sendMessage(Text.of("\u00a7d" + caller.getName() + "\u00a7b just set you as the new owner of the faction!"));

            }
            else{

                caller.sendMessage(Text.of(XError.XERROR_UNKNOWN.getDesc()));

            }

        }
        else{

            caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc()));

        }

    }

}
