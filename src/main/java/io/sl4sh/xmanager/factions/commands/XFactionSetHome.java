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
import org.spongepowered.api.world.*;

import java.util.Optional;

public class XFactionSetHome implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            setFactionHome(ply);

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();
    }

    private void setFactionHome(Player caller){

        Optional<XFaction> optCallerFaction = XFactionCommandManager.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        XFaction callerFaction = optCallerFaction.get();

        if(!XFactionCommandManager.getPlayerFactionPermissions(caller).isPresent() || !XFactionCommandManager.getPlayerFactionPermissions(caller).get().getConfigure()) { caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        if(caller.getWorld().getDimension().getType() != DimensionTypes.OVERWORLD) { caller.sendMessage(Text.of("\u00a7c[Factions] | Your faction home must be located in the overworld.")); return; }

        Optional<Location<World>> safeLoc = Sponge.getGame().getTeleportHelper().getSafeLocation(new Location<World>(caller.getWorld(), caller.getPosition()));

        if(safeLoc.isPresent()){

            callerFaction.setFactionHome(safeLoc.get());
            XManager.getXManager().writeFactions();
            XManager.getXManager().writePlayerInfo();
            caller.sendMessage(Text.of("\u00a7a[Factions] | Successfully set your faction's home!"));

        }
        else{

            caller.sendMessage(Text.of(XError.XERROR_NOSAFELOC.getDesc()));

        }

    }

}
