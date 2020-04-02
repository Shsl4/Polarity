package io.sl4sh.xmanager.commands;

import com.flowpowered.math.vector.Vector3d;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.commands.elements.XWarpCommandElement;
import io.sl4sh.xmanager.data.XManagerLocationData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class XManagerWarp implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Teleports to a warp."))
                .arguments(new XWarpCommandElement(Text.of("warpName")))
                .permission("xmanager.warp")
                .executor(new XManagerWarp())
                .build();

    }

    public static CommandSpec getSetCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets a warp."))
                .arguments(GenericArguments.string(Text.of("warpName")))
                .permission("xmanager.warp.set")
                .executor(new XManagerSetWarp())
                .build();

    }

    public static CommandSpec getRemoveCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Removes a warp."))
                .arguments(new XWarpCommandElement(Text.of("warpName")))
                .permission("xmanager.warp.remove")
                .executor(new XManagerRemoveWarp())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player) {

            Player caller = (Player) src;

            XManagerLocationData data = XManager.getConfigData().getWarpsData().get(args.getOne("warpName").get().toString());

            if(data != null){

                Optional<World> world = Sponge.getServer().getWorld(data.getDimensionName());
                Vector3d pos = XUtilities.getStringAsVector3d(data.getLocation());

                if(world.isPresent()){

                    Location<World> loc = new Location<World>(world.get(), pos);

                    if(Sponge.getTeleportHelper().getSafeLocation(loc).isPresent()){

                        caller.setLocation(Sponge.getTeleportHelper().getSafeLocation(loc).get());
                        return CommandResult.success();


                    }


                }

                src.sendMessage(Text.of(TextColors.RED, "Unable to warp here now."));
                return CommandResult.success();

            }

            src.sendMessage(Text.of(TextColors.RED, "This warp does not exist."));

        }

        return CommandResult.success();

    }

}

class XManagerSetWarp implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            if(!XManager.getConfigData().getWarpNames().contains(args.getOne("warpName").get().toString())) {

                XManager.getConfigData().getWarpsData().put((String)args.getOne("warpName").get(), new XManagerLocationData(caller.getWorld().getName(), caller.getPosition().toString()));

                caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Added a new warp named ", (String)args.getOne("warpName").get(), "."));

            }
            else{

                caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | This warp already exist."));

            }

        }

        return CommandResult.success();
    }
}

class XManagerRemoveWarp implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        String warpName = (String)args.getOne(Text.of("warpName")).get();

        if(XManager.getConfigData().getWarpsData().remove(warpName) != null){

            src.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Warp ", warpName, " removed."));

        }
        else{

            src.sendMessage(Text.of(TextColors.RED, "[XManager] | Failed to remove ", warpName, ". It may not exist"));

        }

        return CommandResult.success();
    }
}