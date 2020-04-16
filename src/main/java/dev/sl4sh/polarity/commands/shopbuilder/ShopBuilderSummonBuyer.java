package dev.sl4sh.polarity.commands.shopbuilder;

import dev.sl4sh.polarity.economy.shops.merchants.BuyerNPC;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class ShopBuilderSummonBuyer implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Summons a buyer"))
                .permission("polarity.summonbuyer")
                .executor(new ShopBuilderSummonBuyer())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            BuyerNPC.summonNPC(caller.getWorld(), caller);

        }

        return CommandResult.success();

    }
}
