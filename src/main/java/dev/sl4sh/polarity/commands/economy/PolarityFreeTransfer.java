package dev.sl4sh.polarity.commands.economy;

import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.economy.transactionidentifiers.AdminIdentifier;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;

public class PolarityFreeTransfer implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Deposits money to a player's account."))
                .arguments(GenericArguments.player(Text.of("targetPlayer")), GenericArguments.bigDecimal(Text.of("amount")))
                .permission("polarity.admindeposit")
                .executor(new PolarityFreeTransfer())
                .build();

    }


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(args.getOne("targetPlayer").isPresent() && args.getOne("amount").isPresent()){

           Utilities.depositToPlayer((Player)(args.getOne("targetPlayer").get()), (BigDecimal)(args.getOne("amount").get()), new AdminIdentifier());

        }

        return CommandResult.success();

    }

}
