package io.sl4sh.xmanager.commands.shopbuilder;

import io.sl4sh.xmanager.economy.shops.UI.XShopBuilderNewUI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XShopBuilderNew implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Shopbuilder new"))
                .arguments(GenericArguments.string(Text.of("profileName")), GenericArguments.integer(Text.of("height")))
                .permission("xmanager.shopbuilder.new")
                .executor(new XShopBuilderNew())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            XShopBuilderNewUI intf = new XShopBuilderNewUI((String)args.getOne("profileName").get(), (int)args.getOne("height").get());
            intf.makeShopBuilderInterface(caller);

        }

        return CommandResult.success();

    }
}
