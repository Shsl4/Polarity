package dev.sl4sh.polarity.commands.shopbuilder;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.economy.ShopProfile;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;

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

            String name = (String)args.getOne("profileName").get();
            Integer height = (Integer)args.getOne("height").get();

            if(Polarity.getShopProfiles().getExistingShopProfilesNames().contains(name)) { throw new CommandException(Text.of("This shop profile already exists")); }

            Polarity.getShopProfiles().addShopProfile(new ShopProfile(new ArrayList<>(), name, height));

            caller.sendMessage(Text.of(TextColors.GREEN, "Added a shop profile named " + name));

        }

        return CommandResult.success();

    }
}
