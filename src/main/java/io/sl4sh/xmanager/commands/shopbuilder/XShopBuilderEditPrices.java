package io.sl4sh.xmanager.commands.shopbuilder;

import io.sl4sh.xmanager.commands.elements.XShopCommandElement;
import io.sl4sh.xmanager.economy.XShopBuilderEditPricesUI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XShopBuilderEditPrices implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main ShopBuilder command"))
                .arguments(new XShopCommandElement(Text.of("profileName")), GenericArguments.doubleNum(Text.of("step")))
                .permission("xmanager.shopbuilder.editprices")
                .executor(new XShopBuilderEditPrices())
                .build();

    }


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            XShopBuilderEditPricesUI intf = new XShopBuilderEditPricesUI();
            intf.makeFromShopProfile(caller, (String)args.getOne("profileName").get(), (double)args.getOne("step").get());

        }

        return CommandResult.success();

    }
}
