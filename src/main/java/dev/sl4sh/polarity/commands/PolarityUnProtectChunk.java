package dev.sl4sh.polarity.commands;

import com.flowpowered.math.vector.Vector3i;
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

public class PolarityUnProtectChunk implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Removes protected chunk."))
                .permission("polarity.unprotectchunk")
                .executor(new PolarityUnProtectChunk())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player ply = (Player) src;
            unProtectChunk(ply);

        } else {

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void unProtectChunk(Player caller){

        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(caller.getWorld());
        Vector3i chunkPos = caller.getLocation().getChunkPosition();

        // If the chunk is protected
        if(worldInfo.getWorldProtectedChunks().contains(chunkPos)){

            // Remove the protected chunk and save the configuration.
           worldInfo.getWorldProtectedChunks().remove(chunkPos);
           Polarity.getPolarity().writeAllConfig();
           caller.sendMessage(Text.of(TextColors.GREEN, "[Polarity] | Removed a protected chunk: ", chunkPos.toString()));

        }
        // else print a message
        else{

            caller.sendMessage(Text.of(TextColors.AQUA, "[Polarity] | This chunk is not protected."));

        }

    }

}
