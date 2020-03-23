package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XManagerUnProtectChunk implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;
            unProtectChunk(ply);

        } else {

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void unProtectChunk(Player caller){

        if(XManager.getXManager().getConfigData().getServerProtectedChunks().contains(caller.getLocation().getChunkPosition().toString())){

            XManager.getXManager().getConfigData().getServerProtectedChunks().remove(caller.getLocation().getChunkPosition().toString());
            XManager.getXManager().writeDataConfigFile();
            caller.sendMessage(Text.of("\u00a7a[XManager] | Removed a protected chunk: " + caller.getLocation().getChunkPosition().toString()));

        }
        else{

            caller.sendMessage(Text.of("\u00a7c[XManager] | This chunk is not protected."));

        }


    }

}
