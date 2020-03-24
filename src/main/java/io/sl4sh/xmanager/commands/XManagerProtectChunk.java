package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.XUtilities;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class XManagerProtectChunk implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Add protected chunk."))
                .permission("xmanager.protectchunk")
                .executor(new XManagerProtectChunk())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player ply = (Player) src;
            protectChunk(ply);

        } else {

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void protectChunk(Player caller){

        // Check if the target chunk is already protected
        if(!XUtilities.isLocationProtected(caller.getLocation())){

            // If not set the hub's location and write the configuration file.
            XManager.getXManager().getConfigData().getServerProtectedChunks().add(new XManagerLocationData(caller.getWorld().getName(), caller.getLocation().getChunkPosition().toString()));
            XManager.getXManager().writeMainDataConfigurationFile();
            caller.sendMessage(Text.of(TextColors.GREEN, "[XManager] | Added a protected chunk: " , caller.getLocation().getChunkPosition().toString()));

        }
        // If true print a message.
        else{

            caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | This chunk already protected."));

        }

    }

}
