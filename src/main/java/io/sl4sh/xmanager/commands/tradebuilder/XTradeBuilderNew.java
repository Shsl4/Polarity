package io.sl4sh.xmanager.commands.tradebuilder;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.commands.economy.XEconomyAdminTransfer;
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

public class XTradeBuilderNew implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Creates a new tradebuilder."))
                .permission("xmanager.tradebuilder.new")
                .executor(new XTradeBuilderNew())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            XManager.getXManager().newTradeBuilder();
            src.sendMessage(Text.of(TextColors.GREEN, "[XManager] | Created a new TradeBuilder"));

        }

        return CommandResult.success();

    }
}
