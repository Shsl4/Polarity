package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.function.Predicate;

public class XManagerReloadShops implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Reload the shops and trade configuration"))
                .permission("xmanager.reloadshops")
                .executor(new XManagerReloadShops())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        XManager plugin = XManager.getXManager();

        plugin.loadShopProfiles();
        plugin.loadCustomTrades();

        src.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Reloaded trades and shop profiles."));

        return CommandResult.success();

    }
}
