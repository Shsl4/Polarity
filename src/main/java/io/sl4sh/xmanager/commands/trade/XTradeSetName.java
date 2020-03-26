package io.sl4sh.xmanager.commands.trade;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XTradeBuilder;
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

public class XTradeSetName implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets the trade name."))
                .arguments(GenericArguments.string(Text.of("tradeName")))
                .permission("xmanager.trade.setname")
                .executor(new XTradeSetName())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player) {

            Player caller = (Player) src;

            if (!XManager.getXManager().getTradeBuilder().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[XManager] | No trade builder.")); return CommandResult.success(); }

            XTradeBuilder tradeBuilder = XManager.getXManager().getTradeBuilder().get();
            tradeBuilder.tradeName = (String)args.getOne(Text.of("tradeName")).get();
            caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Trade name set."));

        }

        return CommandResult.success();

    }
}
