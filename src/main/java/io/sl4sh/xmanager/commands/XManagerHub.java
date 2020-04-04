package io.sl4sh.xmanager.commands;

import com.flowpowered.math.vector.Vector3d;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class XManagerHub implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Teleports to the hub."))
                .permission("xmanager.hub")
                .executor(new XManagerHub())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player caller = (Player) src;
            XManagerWarp.warp(caller, "Hub");

        } else {

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

}
