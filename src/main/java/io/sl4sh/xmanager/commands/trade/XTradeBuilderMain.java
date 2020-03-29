package io.sl4sh.xmanager.commands.trade;

import io.sl4sh.xmanager.economy.XTradeProfile;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class XTradeBuilderMain implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Tradebuilder main command"))
                .permission("xmanager.trade")
                .child(XTradeBuilderList.getCommandSpec(), "list")
                .child(XTradeBuilderSummon.getCommandSpec(), "summon")
                .executor(new XTradeBuilderMain())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        src.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Use child commands."));

        return CommandResult.success();
    }
}
