package dev.sl4sh.polarity.commands.economy;

import dev.sl4sh.polarity.commands.elements.FactionCommandElement;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.enums.PolarityErrors;
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
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class PolarityFactionTransfer implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Transfers money to a faction."))
                .permission("polarity.transfer.faction")
                .arguments(new FactionCommandElement(Text.of("factionName")), GenericArguments.bigDecimal(Text.of("amount")))
                .executor(new PolarityFactionTransfer())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            if(!transferToFaction(caller, (BigDecimal)args.getOne("amount").get(), (String)args.getOne("factionName").get())){

                caller.playSound(SoundTypes.BLOCK_NOTE_BASS, caller.getPosition(), 0.25);

            }

        }

        return CommandResult.success();

    }

    private boolean transferToFaction(Player caller, BigDecimal amount, String factionName){

        Optional<Faction> optTargetFaction = Utilities.getFactionByName(factionName);

        if(!optTargetFaction.isPresent()) { caller.sendMessage(Text.of(PolarityErrors.NULLFACTION.getDesc(), TextColors.RED, " Type /factions list to list existing factions.")); return false; }

        Faction targetFaction = optTargetFaction.get();

        if(!Polarity.getEconomyService().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "Unable to access accounts. Please try again later.")); return false; }

        PolarityEconomyService economyService = Polarity.getEconomyService().get();

        if(!economyService.getOrCreateAccount(caller.getUniqueId()).isPresent() || !economyService.getOrCreateAccount(targetFaction.getUniqueId()).isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "Unable to access accounts. Please try again later.")); return false; }

        Account playerAccount = economyService.getOrCreateAccount(caller.getUniqueId()).get();
        Account factionAccount = economyService.getOrCreateAccount(targetFaction.getUniqueId()).get();

        PolarityCurrency dollarCurrency = new PolarityCurrency();

        TransactionResult result = playerAccount.transfer(factionAccount, dollarCurrency, amount, Cause.of(EventContext.empty(), caller));

        switch (result.getResult()){

            case SUCCESS:

                caller.sendMessage(Text.of(TextColors.GREEN, "Successfully deposited ", dollarCurrency.format(amount, 2), TextColors.GREEN, " to ", factionAccount.getDisplayName(), TextColors.GREEN, "!"));
                caller.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, caller.getPosition(), .25);
                return true;

            case ACCOUNT_NO_FUNDS:

                caller.sendMessage(Text.of(TextColors.RED, "You do not have enough money to do that!"));
                return false;

        }

        caller.sendMessage(Text.of(TextColors.RED, "Transaction Failed!"));

        return false;

    }

}
