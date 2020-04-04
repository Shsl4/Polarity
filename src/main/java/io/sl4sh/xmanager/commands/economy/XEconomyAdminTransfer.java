package io.sl4sh.xmanager.commands.economy;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.transactionidentifiers.XAdminIdentifier;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

public class XEconomyAdminTransfer implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Deposits money to a player's account."))
                .arguments(GenericArguments.player(Text.of("targetPlayer")), GenericArguments.bigDecimal(Text.of("amount")))
                .permission("xmanager.economy.admindeposit")
                .executor(new XEconomyAdminTransfer())
                .build();

    }


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(args.getOne("targetPlayer").isPresent() && args.getOne("amount").isPresent()){

            depositToPlayer(src, (BigDecimal)(args.getOne("amount").get()), (Player)(args.getOne("targetPlayer").get()));

        }

        return CommandResult.success();

    }

    private void depositToPlayer(CommandSource caller, BigDecimal amount, Player player){

        // The economy service will always be present as the command is registered only if the economy service registered
        Optional<UniqueAccount> optPlayerAccount = XManager.getXManager().getEconomyService().get().getOrCreateAccount(player.getUniqueId());

        if(!optPlayerAccount.isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to find player account.")); return; }

        XDollar dollarCurrency = new XDollar();

        TransactionResult result = optPlayerAccount.get().deposit(dollarCurrency, amount, Cause.of(EventContext.empty(), new XAdminIdentifier()), new HashSet<>());

        if (result.getResult() == ResultType.SUCCESS) { caller.sendMessage(Text.of(TextColors.GREEN, "[Economy] | Successfully deposited ", dollarCurrency.format(amount, 2), TextColors.GREEN, " to ", optPlayerAccount.get().getDisplayName(), TextColors.GREEN, "!")); return; }

        caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Transaction Failed."));

    }

}
