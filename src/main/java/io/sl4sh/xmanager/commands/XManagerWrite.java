package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XManagerWrite implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof ConsoleSource) {

            ConsoleSource ply = (ConsoleSource) src;

            XManager.getXManager().writePlayerInfo();
            XManager.getXManager().writeFactions();

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_SERVERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }
}
