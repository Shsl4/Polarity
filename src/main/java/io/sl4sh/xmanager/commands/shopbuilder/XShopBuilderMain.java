package io.sl4sh.xmanager.commands.shopbuilder;

import io.sl4sh.xmanager.commands.economy.XEconomyShowBalance;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class XShopBuilderMain implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main ShopBuilder command"))
                .permission("xmanager.shopbuilder")
                .child(XShopBuilderSummon.getCommandSpec(), "summon")
                .child(XShopBuilderList.getCommandSpec(), "list")
                .executor(new XShopBuilderMain())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        return CommandResult.success();

    }
}
