package io.sl4sh.xmanager.commands.trade;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XTradeBuilder;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class XTradeList implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists all existing trades."))
                .permission("xmanager.trade.list")
                .executor(new XTradeList())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player) {

            Player caller = (Player) src;

            List<XTradeBuilder> existingTrades = XManager.getXManager().getTradesContainer().getTradeList();

            src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Trades List ============"));

            if(existingTrades.size() <= 0){

                src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!"));
                return CommandResult.success();

            }

            int it = 1;

            for(XTradeBuilder tradeBuilder : existingTrades){

                src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , tradeBuilder.tradeName, TextColors.RESET,
                        TextColors.GREEN, " | ", TextColors.GOLD, "First item : ", TextColors.AQUA, tradeBuilder.firstBuyingItem.getQuantity(), " ", tradeBuilder.firstBuyingItem.getType().getName(),
                        TextColors.GREEN, " | ", TextColors.GOLD, "Second item : ", TextColors.AQUA, tradeBuilder.secondBuyingItem.getQuantity(), " ", tradeBuilder.secondBuyingItem.getType().getName(),
                        TextColors.GREEN, " | ", TextColors.GOLD, "Selling item : ", TextColors.AQUA, tradeBuilder.sellingItem.getQuantity(), " ", tradeBuilder.sellingItem.getType().getName()));

                it++;

            }

        }

        return CommandResult.success();

    }
}
