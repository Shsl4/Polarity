package io.sl4sh.xmanager.commands.shopbuilder;

import io.sl4sh.xmanager.economy.shops.merchants.XBuyerNPC;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XShopBuilderSummonBuyer implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Summons a buyer"))
                .permission("xmanager.summonbuyer")
                .executor(new XShopBuilderSummonBuyer())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            XBuyerNPC.summonNPC(caller.getWorld(), caller);

        }

        return CommandResult.success();

    }
}
