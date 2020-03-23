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
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class XFactionHome implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            factionHome(ply);

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();
    }

    private void factionHome(Player caller){

        Optional<XFaction> OptCallerFaction = XFactionCommandManager.getPlayerFaction(caller);

        if(!OptCallerFaction.isPresent()) { caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        XFaction CallerFaction = OptCallerFaction.get();

        if(CallerFaction.getFactionHome() == null) { caller.sendMessage(Text.of(XError.XERROR_NOHOME.getDesc())); return;  }

        if(caller.getWorld().getDimension().getType() != DimensionTypes.OVERWORLD) { caller.sendMessage(Text.of(XError.XERROR_WRONGDIM.getDesc())); return; }

        Optional<Location<World>> safeLoc = Sponge.getGame().getTeleportHelper().getSafeLocation(new Location<World>(caller.getWorld(), CallerFaction.getFactionHome().getPosition()));

        if(safeLoc.isPresent()){

            caller.setLocation(safeLoc.get());
            caller.sendMessage(Text.of("\u00a7a[Factions] | Successfully teleported at your faction's home!"));

        }
        else{

            caller.sendMessage(Text.of(XError.XERROR_NOSAFELOC.getDesc()));

        }

    }

}
