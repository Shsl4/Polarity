package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.commands.elements.XWarpCommandElement;
import io.sl4sh.xmanager.data.XWarpData;
import io.sl4sh.xmanager.data.XWorldInfo;
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

import javax.annotation.Nonnull;
import java.util.Map;

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

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {

        if(src instanceof Player) {

            Player caller = (Player) src;

            warp(caller, args.getOne("warpName").get().toString());

        }

        return CommandResult.success();

    }

    public static void warp(Player caller, String warpName){

        for(XWorldInfo worldInfo : XManager.getWorldsInfo()){

            Map<String, XWarpData> warps = worldInfo.getWarps();

            if(warps.get(warpName) != null){

                if(warps.get(warpName).getTargetWorld().isPresent()){

                    Location<World> loc = new Location<>(warps.get(warpName).getTargetWorld().get(), warps.get(warpName).getPosition());

                    if(Sponge.getTeleportHelper().getSafeLocation(loc).isPresent()){

                        caller.setLocation(Sponge.getTeleportHelper().getSafeLocation(loc).get());
                        return;

                    }

                }

                caller.sendMessage(Text.of(TextColors.RED, "Unable to warp here now."));
                return;

            }

        }

        caller.sendMessage(Text.of(TextColors.RED, "This warp does not exist."));

    }

}

class XManagerSetWarp implements CommandExecutor {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            String warpName = args.getOne("warpName").get().toString();

            for(XWorldInfo worldInfo : XManager.getWorldsInfo()){

                if(worldInfo.getWarps().get(warpName) != null){

                    caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | This warp already exist."));
                    return CommandResult.success();

                }

            }

            XWorldInfo worldInfo = XUtilities.getOrCreateWorldInfo(caller.getWorld());
            worldInfo.getWarps().put(warpName, new XWarpData(caller.getPosition(), caller.getWorld().getUniqueId()));
            caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Added a new warp named ", warpName, "."));
            XManager.getXManager().writeWorldsInfoData();

        }

        return CommandResult.success();
    }
}

class XManagerRemoveWarp implements CommandExecutor {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, CommandContext args) throws CommandException {

        String warpName = (String)args.getOne(Text.of("warpName")).get();

        for(XWorldInfo worldInfo : XManager.getWorldsInfo()){

            if(worldInfo.getWarps().get(warpName) != null){

                worldInfo.getWarps().remove(warpName);
                src.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Warp ", warpName, " removed."));
                return CommandResult.success();

            }

        }

        src.sendMessage(Text.of(TextColors.RED, "[XManager] | Failed to remove ", warpName, ". It may not exist"));
        return CommandResult.success();
    }
}