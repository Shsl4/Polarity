package io.sl4sh.xmanager.commands.tradebuilder;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.commands.economy.XTradeBuilder;
import io.sl4sh.xmanager.commands.economy.XVillager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class XTradeBuilderBuild implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Builds the trade."))
                .permission("xmanager.tradebuilder.build")
                .executor(new XTradeBuilderBuild())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            if(!XManager.getXManager().getTradeBuilder().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[XManager] | No trade builder.")); return CommandResult.success();}

            XTradeBuilder tradeBuilder = XManager.getXManager().getTradeBuilder().get();

            Optional<TradeOffer> optTradeOffer = tradeBuilder.makeTradeOffer();

            if(optTradeOffer.isPresent()){

                List<TradeOffer> tradeOfferList = new ArrayList<>();
                tradeOfferList.add(optTradeOffer.get());
                XVillager.spawnEntity(caller.getLocation(), Text.of(TextColors.GOLD, "Mitroglou"), tradeOfferList, caller);

            }
            else{

                caller.sendMessage(Text.of(TextColors.RED, "[XManager] | Trade offer is absent."));

            }

        }

        return CommandResult.success();

    }
}
