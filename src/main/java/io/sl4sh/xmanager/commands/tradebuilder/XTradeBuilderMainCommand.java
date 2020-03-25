package io.sl4sh.xmanager.commands.tradebuilder;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class XTradeBuilderMainCommand implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Tradebuilder main command"))
                .permission("xmanager.tradebuilder")
                .child(XTradeBuilderSetFirstBuyingItem.getCommandSpec(), "setfirstitem")
                .child(XTradeBuilderSetSecondBuyingItem.getCommandSpec(), "setseconditem")
                .child(XTradeBuilderNew.getCommandSpec(), "new")
                .child(XTradeBuilderSellingItem.getCommandSpec(), "setsellingitem")
                .child(XTradeBuilderBuild.getCommandSpec(), "build")
                .executor(new XTradeBuilderMainCommand())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        src.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Use child commands."));

        return CommandResult.success();
    }
}
