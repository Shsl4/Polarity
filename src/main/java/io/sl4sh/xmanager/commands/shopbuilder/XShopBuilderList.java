package io.sl4sh.xmanager.commands.shopbuilder;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.economy.XEconomyShopRecipe;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class XShopBuilderList implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists the available shop profiles."))
                .permission("xmanager.shopbuilder.list")
                .executor(new XShopBuilderList())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        XManager.getShopProfiles().listShopProfiles(src);

        return CommandResult.success();

    }
}
