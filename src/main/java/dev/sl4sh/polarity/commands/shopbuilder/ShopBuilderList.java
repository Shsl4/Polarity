package dev.sl4sh.polarity.commands.shopbuilder;

import dev.sl4sh.polarity.Polarity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class ShopBuilderList implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists the available shop profiles."))
                .permission("polarity.shopbuilder.list")
                .executor(new ShopBuilderList())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Polarity.getShopProfiles().listShopProfiles(src);

        return CommandResult.success();

    }
}
