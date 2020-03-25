package io.sl4sh.xmanager.commands.tradebuilder;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.commands.economy.XTradeBuilder;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class XTradeBuilderSetSecondBuyingItem implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets the trade second item."))
                .permission("xmanager.tradebuilder.setseconditem")
                .arguments(GenericArguments.integer(Text.of("count")))
                .executor(new XTradeBuilderSetSecondBuyingItem())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            if(!XManager.getXManager().getTradeBuilder().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[XManager] | No trade builder.")); return CommandResult.success();}

            XTradeBuilder tradeBuilder = XManager.getXManager().getTradeBuilder().get();

            if(caller.getItemInHand(HandTypes.MAIN_HAND).isPresent() && caller.getItemInHand(HandTypes.MAIN_HAND).get() != ItemStack.empty()){

                tradeBuilder.secondBuyingItem = ItemStack.of(caller.getItemInHand(HandTypes.MAIN_HAND).get().getType(), (int)args.getOne("count").get()).createSnapshot();
                caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Value set."));

            }
            else{

                caller.sendMessage(Text.of(TextColors.RED, "[XManager] | No item held."));

            }

        }

        return CommandResult.success();

    }

}
