package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class FactionsWithdraw implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Withdraws money from your faction."))
                .permission("polarity.factions.factionwithdraw")
                .arguments(GenericArguments.bigDecimal(Text.of("amount")))
                .executor(new FactionsWithdraw())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            withdraw((Player)src, (BigDecimal) args.getOne(Text.of("amount")).get());

        }

        return CommandResult.success();

    }

    public void withdraw(Player caller, BigDecimal amount){

        if(!Utilities.getPlayerFaction(caller).isPresent()) { caller.sendMessage(PolarityErrors.NOFACTION.getDesc()); }

        Faction callerFaction = Utilities.getPlayerFaction(caller).get();

        Optional<FactionMemberData> optMemberData = Utilities.getMemberDataForPlayer(caller);

        if(!optMemberData.isPresent()) { caller.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc()); return; }

        FactionMemberData memberData = optMemberData.get();

        if(!memberData.getPermissions().getManage()) { caller.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc()); return; }

        if(!Polarity.getEconomyService().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "Unable to access accounts. Please try again later.")); return; }

        PolarityEconomyService economyService = Polarity.getEconomyService().get();

        if(!economyService.getOrCreateAccount(caller.getUniqueId()).isPresent() || !economyService.getOrCreateAccount(callerFaction.getName()).isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "Unable to access accounts. Please try again later.")); return; }

        Account playerAccount = economyService.getOrCreateAccount(caller.getUniqueId()).get();
        Account factionAccount = economyService.getOrCreateAccount(callerFaction.getName()).get();

        PolarityCurrency dollarCurrency = new PolarityCurrency();

        TransactionResult result = factionAccount.transfer(playerAccount, dollarCurrency, amount, Cause.of(EventContext.empty(), caller));

        switch (result.getResult()){

            case SUCCESS:

                caller.sendMessage(Text.of(TextColors.AQUA, "Successfully withdrawn ", dollarCurrency.format(amount, 2), TextColors.AQUA, " from your faction."));
                return;

            case ACCOUNT_NO_FUNDS:

                caller.sendMessage(Text.of(TextColors.RED, "You faction does not have enough money to do that!"));
                return;

        }

        caller.sendMessage(Text.of(TextColors.RED, "Transaction Failed!"));

    }

}
