package dev.sl4sh.polarity.commands.shopbuilder;

import dev.sl4sh.polarity.commands.elements.ShopCommandElement;
import dev.sl4sh.polarity.economy.shops.UI.ShopBuilderEditLayoutUI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class ShopBuilderEditLayout implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main ShopBuilder command"))
                .arguments(new ShopCommandElement(Text.of("profileName")))
                .permission("polarity.shopbuilder.editlayout")
                .executor(new ShopBuilderEditLayout())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;
            ShopBuilderEditLayoutUI intf = new ShopBuilderEditLayoutUI();
            intf.makeFromShopProfile(caller, (String)args.getOne("profileName").get());

        }

        return CommandResult.success();

    }
}
