package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Utilities;
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

public class PolarityProtectChunk implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Add protected chunk."))
                .permission("polarity.protectchunk")
                .executor(new PolarityProtectChunk())
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

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void protectChunk(Player caller){

        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(caller.getWorld());

        // Check if the target chunk is already protected
        if(!worldInfo.isWorldProtected() || !worldInfo.getWorldProtectedChunks().contains(caller.getLocation().getChunkPosition())){

            // If not set the hub's location and write the configuration file.
            worldInfo.getWorldProtectedChunks().add(caller.getLocation().getChunkPosition());
            Polarity.getPolarity().writeAllConfig();
            caller.sendMessage(Text.of(TextColors.GREEN, "[Polarity] | Added a protected chunk: " , caller.getLocation().getChunkPosition().toString()));

        }
        // If true print a message.
        else{

            caller.sendMessage(Text.of(TextColors.AQUA, "[Polarity] | This chunk already protected."));

        }

    }

}
