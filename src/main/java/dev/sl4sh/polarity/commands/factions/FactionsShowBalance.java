package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
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
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Optional;

public class FactionsShowBalance implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Shows your current faction's balance."))
                .permission("polarity.factions.showbalance")
                .executor(new FactionsShowBalance())
                .build();

    }

    @Nonnull
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            showBalance((Player)src);

        }

        return CommandResult.success();

    }

    private void showBalance(Player caller){

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(PolarityErrors.XERROR_NOXF.getDesc()); return; }

        if(!Utilities.getPlayerFactionPermissions(caller).isPresent() || !Utilities.getPlayerFactionPermissions(caller).get().getManage()) { caller.sendMessage(Text.of(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc())); return; }

        if(!Polarity.getEconomyService().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

        PolarityEconomyService economyService = Polarity.getEconomyService().get();

        Faction callerFaction = optCallerFaction.get();

        if(!economyService.getOrCreateAccount(callerFaction.getUniqueId()).isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

        Account factionAccount = economyService.getOrCreateAccount(callerFaction.getUniqueId()).get();

        PolarityCurrency dollarCurrency = new PolarityCurrency();

        caller.sendMessage(Text.of(TextColors.AQUA, "[Economy] | Your current faction's balance is ", dollarCurrency.format(factionAccount.getBalance(dollarCurrency), 2), TextColors.AQUA, "."));
        caller.playSound(SoundTypes.BLOCK_NOTE_HARP, caller.getPosition(), 0.75);


    }

}
