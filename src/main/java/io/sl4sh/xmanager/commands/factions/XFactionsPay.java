package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
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

public class XFactionsPay implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Pays a member of your faction."))
                .permission("xmanager.factions.pay")
                .arguments(GenericArguments.player(Text.of("playerName")), GenericArguments.bigDecimal(Text.of("amount")))
                .executor(new XFactionsPay())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            payPlayer((Player)src, (Player)args.getOne("playerName").get(), (BigDecimal)args.getOne("amount").get());

        }

        return CommandResult.success();

    }

    private void payPlayer(Player caller, Player target, BigDecimal amount){

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);
        Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(target);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        if(!optTargetFaction.isPresent() || !optCallerFaction.get().equals(optTargetFaction.get())) { caller.sendMessage(Text.of(XError.XERROR_NOTAMEMBER.getDesc())); return; }

        if(!XUtilities.getPlayerFactionPermissions(caller).isPresent() || !XUtilities.getPlayerFactionPermissions(caller).get().getManage()) {caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        if(caller.equals(target)) { caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You cannot pay yourself. Use /factions withdraw instead")); return; }

        XFaction faction = optCallerFaction.get();

        if(!XManager.getXEconomyService().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

        XEconomyService economyService = XManager.getXEconomyService().get();

        if(!economyService.getOrCreateAccount(target.getUniqueId()).isPresent() || !economyService.getOrCreateAccount(faction.getFactionName()).isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

        Account targetAccount = economyService.getOrCreateAccount(target.getUniqueId()).get();
        Account factionAccount = economyService.getOrCreateAccount(faction.getFactionName()).get();

        XDollar dollarCurrency = new XDollar();

        TransactionResult result = factionAccount.transfer(targetAccount, dollarCurrency, amount, Cause.of(EventContext.empty(), faction));

        switch (result.getResult()){

            case SUCCESS:

                caller.sendMessage(Text.of(TextColors.GREEN, "[Economy] | Successfully transferred ", dollarCurrency.format(amount, 2), TextColors.GREEN, " to ", target.getName(), TextColors.GREEN, "!"));
                return;

            case ACCOUNT_NO_FUNDS:

                caller.sendMessage(Text.of(TextColors.RED, "[Economy] | You faction does not have enough money to do that!"));
                return;

        }

        caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Transaction Failed!"));

    }

}