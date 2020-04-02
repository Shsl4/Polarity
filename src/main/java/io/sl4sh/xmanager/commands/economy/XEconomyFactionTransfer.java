package io.sl4sh.xmanager.commands.economy;

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

public class XEconomyFactionTransfer implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Transfers money to a faction."))
                .permission("xmanager.economy.factiontransfer")
                .arguments(GenericArguments.string(Text.of("factionName")), GenericArguments.bigDecimal(Text.of("amount")))
                .executor(new XEconomyFactionTransfer())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            if(!transferToFaction(caller, (BigDecimal)args.getOne("amount").get(), (String)args.getOne("factionName").get())){

                caller.playSound(SoundTypes.BLOCK_NOTE_BASS, caller.getPosition(), 0.75);

            }

        }

        return CommandResult.success();

    }

    private boolean transferToFaction(Player caller, BigDecimal amount, String factionName){

        Optional<XFaction> optTargetFaction = XUtilities.getFactionByName(factionName);

        if(!optTargetFaction.isPresent()) { caller.sendMessage(Text.of(XError.XERROR_XFNULL.getDesc(), TextColors.RED, " Type /factions list to list existing factions.")); return false; }

        XFaction targetFaction = optTargetFaction.get();

        if(!XManager.getXEconomyService().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return false; }

        XEconomyService economyService = XManager.getXEconomyService().get();

        if(!economyService.getOrCreateAccount(caller.getUniqueId()).isPresent() || !economyService.getOrCreateAccount(targetFaction.getFactionName()).isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return false; }

        Account playerAccount = economyService.getOrCreateAccount(caller.getUniqueId()).get();
        Account factionAccount = economyService.getOrCreateAccount(targetFaction.getFactionName()).get();

        XDollar dollarCurrency = new XDollar();

        TransactionResult result = playerAccount.transfer(factionAccount, dollarCurrency, amount, Cause.of(EventContext.empty(), caller));

        switch (result.getResult()){

            case SUCCESS:

                caller.sendMessage(Text.of(TextColors.GREEN, "[Economy] | Successfully deposited ", dollarCurrency.format(amount, 2), TextColors.GREEN, " to ", factionAccount.getDisplayName(), TextColors.GREEN, "!"));
                caller.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, caller.getPosition(), 0.75);
                return true;

            case ACCOUNT_NO_FUNDS:

                caller.sendMessage(Text.of(TextColors.RED, "[Economy] | You do not have enough money to do that!"));
                return false;

        }

        caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Transaction Failed!"));

        return false;

    }

}
