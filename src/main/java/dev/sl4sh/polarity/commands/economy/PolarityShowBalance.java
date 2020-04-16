package dev.sl4sh.polarity.commands.economy;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.enums.PolarityErrors;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class PolarityShowBalance implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Shows your current balance."))
                .permission("polarity.showbalance")
                .executor(new PolarityShowBalance())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player caller = (Player) src;
            showBalance(caller);


        } else {

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void showBalance(Player caller){

        // The economy service will always be present as the command is registered only if the economy service registered
        PolarityEconomyService economyService = Polarity.getEconomyService().get();

        Optional<UniqueAccount> optCallerAccount = economyService.getOrCreateAccount(caller.getUniqueId());

        if(!optCallerAccount.isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access your account. Please try again later.")); return; }

        UniqueAccount callerAccount = optCallerAccount.get();

        PolarityCurrency dollarCurrency = new PolarityCurrency();

        caller.sendMessage(Text.of(TextColors.AQUA, "[Economy] | Your current balance is ", dollarCurrency.format(callerAccount.getBalance(dollarCurrency), 2), TextColors.AQUA, "."));
        caller.playSound(SoundTypes.BLOCK_NOTE_HARP, caller.getPosition(), 0.75);

    }

}
