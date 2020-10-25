package dev.sl4sh.polarity.commands;

import com.flowpowered.math.vector.Vector3d;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.WarpCommandElement;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.events.PlayerWarpEvent;
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
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class PolarityWarp implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Teleports to a warp."))
                .arguments(new WarpCommandElement(Text.of("warpName")))
                .permission("polarity.warp")
                .executor(new PolarityWarp())
                .build();

    }

    public static CommandSpec getSetCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets a warp."))
                .arguments(GenericArguments.string(Text.of("warpName")))
                .permission("polarity.warp.set")
                .executor(new PolaritySetWarp())
                .build();

    }

    public static CommandSpec getRemoveCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Removes a warp."))
                .arguments(new WarpCommandElement(Text.of("warpName")))
                .permission("polarity.warp.remove")
                .executor(new PolarityRemoveWarp())
                .build();

    }

    public static CommandSpec getListCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists existing warps."))
                .permission("polarity.warp.list")
                .executor(new PolarityListWarp())
                .build();

    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {

        if(src instanceof Player) {

            Player caller = (Player) src;

            warp(caller, args.getOne("warpName").get().toString(), caller);

        }

        return CommandResult.success();

    }

    public static boolean warp(Player caller, String warpName, Object source){

        for(WorldInfo worldInfo : Polarity.getWorldsInfo().getList()){

            Map<String, Vector3d> warps = worldInfo.getWarps();

            if(warps.get(warpName) != null){

                if(worldInfo.getTargetWorld().isPresent()){

                    Location<World> loc = new Location<>(worldInfo.getTargetWorld().get(), warps.get(warpName));

                    PlayerWarpEvent.Pre preWarpEvent = new PlayerWarpEvent.Pre(caller, warpName, source);

                    Sponge.getEventManager().post(preWarpEvent);

                    if(!preWarpEvent.isCancelled()){

                        caller.setLocation(loc);
                        Sponge.getEventManager().post(new PlayerWarpEvent.Post(caller, warpName, source));
                        return true;

                    }
                    else{

                        return false;

                    }

                }

                if(source instanceof Player) { caller.sendMessage(Text.of(TextColors.RED, "Unable to warp here now.")); return false; }

            }

        }

        if(source instanceof Player) { caller.sendMessage(Text.of(TextColors.RED, "This warp does not exist.")); return false; }

        return false;

    }

}

class PolaritySetWarp implements CommandExecutor {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            String warpName = args.getOne("warpName").get().toString();

            for(WorldInfo worldInfo : Polarity.getWorldsInfo().getList()){

                if(worldInfo.getWarps().get(warpName) != null){

                    caller.sendMessage(Text.of(TextColors.AQUA, "This warp already exist."));
                    return CommandResult.success();

                }

            }

            WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(caller.getWorld());
            worldInfo.getWarps().put(warpName, caller.getPosition());
            caller.sendMessage(Text.of(TextColors.AQUA, "Added a new warp named ", warpName, "."));
            Polarity.getPolarity().writeAllConfig();

        }

        return CommandResult.success();
    }
}

class PolarityRemoveWarp implements CommandExecutor {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, CommandContext args) throws CommandException {

        String warpName = (String)args.getOne(Text.of("warpName")).get();

        for(WorldInfo worldInfo : Polarity.getWorldsInfo().getList()){

            if(worldInfo.getWarps().get(warpName) != null){

                worldInfo.getWarps().remove(warpName);
                src.sendMessage(Text.of(TextColors.AQUA, "Warp ", warpName, " removed."));
                return CommandResult.success();

            }

        }

        src.sendMessage(Text.of(TextColors.RED, "Failed to remove ", warpName, ". It may not exist"));
        return CommandResult.success();
    }
}


class PolarityListWarp implements CommandExecutor {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, CommandContext args) throws CommandException {

        TextColor listTintColor = TextColors.GREEN;

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Warp list ============"));

        List<String> warpNames = Utilities.getExistingWarpNames();

        if(warpNames.size() <= 0){

            src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!"));

        }
        else{

            int it = 1;

            for(String warp : warpNames){

                src.sendMessage(Text.of(listTintColor , "#" , it , ". " , TextColors.WHITE , warp));
                it++;

            }

        }
        return CommandResult.success();
    }
}