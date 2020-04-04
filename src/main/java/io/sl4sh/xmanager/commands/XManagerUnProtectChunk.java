package io.sl4sh.xmanager.commands;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.data.XWorldInfo;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import org.apache.commons.lang3.mutable.MutableInt;
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

public class XManagerUnProtectChunk implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Removes protected chunk."))
                .permission("xmanager.unprotectchunk")
                .executor(new XManagerUnProtectChunk())
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

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void unProtectChunk(Player caller){

        XWorldInfo worldInfo = XUtilities.getOrCreateWorldInfo(caller.getWorld());
        Vector3i chunkPos = caller.getLocation().getChunkPosition();

        // If the chunk is protected
        if(worldInfo.getWorldProtectedChunks().contains(chunkPos)){

            // Remove the protected chunk and save the configuration.
           worldInfo.getWorldProtectedChunks().remove(chunkPos);
           XManager.getXManager().writeWorldsInfoData();
           caller.sendMessage(Text.of(TextColors.GREEN, "[XManager] | Removed a protected chunk: ", chunkPos.toString()));

        }
        // else print a message
        else{

            caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | This chunk is not protected."));

        }

    }

}
