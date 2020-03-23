package io.sl4sh.xmanager.commands;

import com.flowpowered.math.vector.Vector3d;
import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class XManagerHub implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;
            teleportToHub(ply);

        } else {

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();
    }

    private void teleportToHub(Player caller){

        Vector3d hubLocation = XManager.getStringAsVector3d(XManager.getXManager().getConfigData().getHubLocation());
        Optional<Location<World>> safeLoc = Sponge.getGame().getTeleportHelper().getSafeLocation(new Location<World>(caller.getWorld(), hubLocation));

        if(safeLoc.isPresent()){

            caller.setLocation(safeLoc.get());
            return;

        }

        caller.sendMessage(Text.of("\u00a7cUnable to teleport to the Hub now."));

    }

}
