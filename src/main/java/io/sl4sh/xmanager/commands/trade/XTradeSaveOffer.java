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
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class XTradeSaveOffer implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Saves the trade."))
                .permission("xmanager.trade.save")
                .executor(new XTradeSaveOffer())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            if(!XManager.getXManager().getTradeBuilder().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[XManager] | No trade builder.")); return CommandResult.success();}

            XTradeBuilder tradeBuilder = XManager.getXManager().getTradeBuilder().get();

            if(!tradeBuilder.tradeName.equals("") && tradeBuilder.firstBuyingItem != ItemStackSnapshot.NONE && tradeBuilder.sellingItem != ItemStackSnapshot.NONE){

                boolean bRemoved = false;

                for(XTradeBuilder existingTrade : XManager.getXManager().getTradesContainer().getTradeList()){

                    if(existingTrade.tradeName.equals(tradeBuilder.tradeName)){

                        XManager.getXManager().getTradesContainer().getTradeList().remove(existingTrade);
                        bRemoved = true;
                        break;

                    }

                }

                if(bRemoved){

                    caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | A trade with this name already existed. It has been overwritten."));

                }
                else{

                    caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Trade recipe saved."));

                }

                tradeBuilder.saveTradeBuilder();

            }
            else{

                caller.sendMessage(Text.of(TextColors.RED, "[XManager] | You need to name, set the first item and selling item of your recipe before saving it"));

            }

        }

        return CommandResult.success();

    }
}
