package dev.sl4sh.polarity.commands.shopbuilder;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class ShopBuilderMain implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main ShopBuilder command"))
                .permission("polarity.shopbuilder")
                .child(ShopBuilderSummon.getCommandSpec(), "summon")
                .child(ShopBuilderSummonBuyer.getCommandSpec(), "summonbuyer")
                .child(ShopBuilderList.getCommandSpec(), "list")
                .child(ShopBuilderNew.getCommandSpec(), "new")
                .child(ShopBuilderRemove.getCommandSpec(), "remove")
                .child(ShopBuilderEditLayout.getCommandSpec(), "editlayout")
                .child(ShopBuilderEditPrices.getCommandSpec(), "editprices")
                .executor(new ShopBuilderMain())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        return CommandResult.success();

    }
}
