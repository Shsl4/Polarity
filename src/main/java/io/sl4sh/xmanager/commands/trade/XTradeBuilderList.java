package io.sl4sh.xmanager.commands.trade;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XTradeProfile;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class XTradeBuilderList implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists all existing trades."))
                .permission("xmanager.tradebuilder.list")
                .executor(new XTradeBuilderList())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        XManager.getTradeProfiles().listTradesProfiles(src);

        return CommandResult.success();

    }
}
