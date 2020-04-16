package dev.sl4sh.polarity.commands.shopbuilder;

import dev.sl4sh.polarity.economy.shops.UI.ShopBuilderNewUI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class ShopBuilderNew implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Shopbuilder new"))
                .arguments(GenericArguments.string(Text.of("profileName")), GenericArguments.integer(Text.of("height")))
                .permission("polarity.shopbuilder.new")
                .executor(new ShopBuilderNew())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            ShopBuilderNewUI intf = new ShopBuilderNewUI((String)args.getOne("profileName").get(), (int)args.getOne("height").get());
            intf.makeShopBuilderInterface(caller);

        }

        return CommandResult.success();

    }
}
