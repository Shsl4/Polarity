package dev.sl4sh.polarity.commands.shopbuilder;

import dev.sl4sh.polarity.commands.elements.ShopCommandElement;
import dev.sl4sh.polarity.economy.shops.UI.ShopBuilderEditPricesUI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class ShopBuilderEditPrices implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main ShopBuilder command"))
                .arguments(new ShopCommandElement(Text.of("profileName")), GenericArguments.doubleNum(Text.of("step")))
                .permission("polarity.shopbuilder.editprices")
                .executor(new ShopBuilderEditPrices())
                .build();

    }


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            ShopBuilderEditPricesUI intf = new ShopBuilderEditPricesUI();
            intf.makeFromShopProfile(caller, (String)args.getOne("profileName").get(), (double)args.getOne("step").get());

        }

        return CommandResult.success();

    }
}
