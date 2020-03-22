package io.sl4sh.xmanager.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class XManagerCommandManager implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        printXManagerHelp(src);
        return CommandResult.success();

    }

    private void printXManagerHelp(CommandSource src){

        src.sendMessage(Text.of("\u00a72============ XManager Help ============"));
        src.sendMessage(Text.of("\u00a7a/xm factions \u00a7fCreate and manage factions."));

    }

}
