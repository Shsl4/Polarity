package io.sl4sh.xmanager.commands.economy;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.enums.XError;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

public class XEconomyPlayerTransfer implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Transfer money to another player's account."))
                .permission("xmanager.economy.playertransfer")
                .arguments(GenericArguments.player(Text.of("targetPlayer")), GenericArguments.bigDecimal(Text.of("amount")))
                .executor(new XEconomyPlayerTransfer())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player caller = (Player) src;

            if(args.getOne("targetPlayer").isPresent() && args.getOne("amount").isPresent()){

                depositToPlayer(caller, (Player)(args.getOne("targetPlayer").get()), (BigDecimal)(args.getOne("amount").get()));

            }


        } else {

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void depositToPlayer(Player caller, Player target, BigDecimal amount){

        if(caller.equals(target)) { caller.sendMessage(Text.of(TextColors.AQUA, "[Economy] | You can't transfer money to yourself.")); return; }

        // The economy service will always be present as the command is registered only if the economy service registered
        XEconomyService economyService = XManager.getXEconomyService().get();

        Optional<UniqueAccount> optCallerAccount = economyService.getOrCreateAccount(caller.getUniqueId());
        Optional<UniqueAccount> optTargetAccount = economyService.getOrCreateAccount(target.getUniqueId());

        if(!optCallerAccount.isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access your account. Please try again later.")); return; }

        if(!optTargetAccount.isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access your target's account. Please try again later.")); return; }

        UniqueAccount callerAccount = optCallerAccount.get();
        UniqueAccount targetAccount = optTargetAccount.get();

        XDollar dollarCurrency = new XDollar();

        TransactionResult result = callerAccount.transfer(targetAccount, dollarCurrency, amount, Cause.of(EventContext.empty(), caller), new HashSet<>());

        switch (result.getResult()){

            case SUCCESS:

                caller.sendMessage(Text.of(TextColors.GREEN, "[Economy] | Successfully deposited ", dollarCurrency.format(amount, 2), TextColors.GREEN, " to ", targetAccount.getDisplayName(), TextColors.GREEN, "!"));
                caller.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, caller.getPosition(), 0.75);
                return;

            case ACCOUNT_NO_FUNDS:

                caller.sendMessage(Text.of(TextColors.RED, "[Economy] | You do not have enough money to do that!"));
                return;

        }

        caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Transaction Failed!"));

    }

}
