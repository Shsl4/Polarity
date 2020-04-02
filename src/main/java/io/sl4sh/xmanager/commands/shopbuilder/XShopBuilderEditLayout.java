package io.sl4sh.xmanager.commands.shopbuilder;

import io.sl4sh.xmanager.commands.elements.XShopCommandElement;
import io.sl4sh.xmanager.economy.XShopBuilderEditLayoutUI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XShopBuilderEditLayout implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main ShopBuilder command"))
                .arguments(new XShopCommandElement(Text.of("profileName")))
                .permission("xmanager.shopbuilder.editlayout")
                .executor(new XShopBuilderEditLayout())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;
            XShopBuilderEditLayoutUI intf = new XShopBuilderEditLayoutUI();
            intf.makeFromShopProfile(caller, (String)args.getOne("profileName").get());

        }

        return CommandResult.success();

    }
}
