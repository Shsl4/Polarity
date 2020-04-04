package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class XFactionsWithdraw implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Withdraws money from your faction."))
                .permission("xmanager.factions.factionwithdraw")
                .arguments(GenericArguments.bigDecimal(Text.of("amount")))
                .executor(new XFactionsWithdraw())
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

        if(!XUtilities.getPlayerFaction(caller).isPresent()) { caller.sendMessage(XError.XERROR_NOXF.getDesc()); }

        XFaction callerFaction = XUtilities.getPlayerFaction(caller).get();

        Optional<XFactionMemberData> optMemberData = XUtilities.getMemberDataForPlayer(caller);

        if(!optMemberData.isPresent()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

        XFactionMemberData memberData = optMemberData.get();

        if(!memberData.getPermissions().getManage()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

        if(!XManager.getEconomyService().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

        XEconomyService economyService = XManager.getEconomyService().get();

        if(!economyService.getOrCreateAccount(caller.getUniqueId()).isPresent() || !economyService.getOrCreateAccount(callerFaction.getName()).isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

        Account playerAccount = economyService.getOrCreateAccount(caller.getUniqueId()).get();
        Account factionAccount = economyService.getOrCreateAccount(callerFaction.getName()).get();

        XDollar dollarCurrency = new XDollar();

        TransactionResult result = factionAccount.transfer(playerAccount, dollarCurrency, amount, Cause.of(EventContext.empty(), caller));

        switch (result.getResult()){

            case SUCCESS:

                caller.sendMessage(Text.of(TextColors.AQUA, "[Economy] | Successfully withdrawn ", dollarCurrency.format(amount, 2), TextColors.AQUA, " from your faction."));
                return;

            case ACCOUNT_NO_FUNDS:

                caller.sendMessage(Text.of(TextColors.RED, "[Economy] | You faction does not have enough money to do that!"));
                return;

        }

        caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Transaction Failed!"));

    }

}
