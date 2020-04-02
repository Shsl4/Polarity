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
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class XFactionsShowBalance implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Shows your current faction's balance."))
                .permission("xmanager.factions.showbalance")
                .executor(new XFactionsShowBalance())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            showBalance((Player)src);

        }

        return CommandResult.success();

    }

    private void showBalance(Player caller){

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

        if(!XUtilities.getPlayerFactionPermissions(caller).isPresent() || !XUtilities.getPlayerFactionPermissions(caller).get().getManage()) { caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

        if(!XManager.getXEconomyService().isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

        XEconomyService economyService = XManager.getXEconomyService().get();

        XFaction callerFaction = optCallerFaction.get();

        if(!economyService.getOrCreateAccount(callerFaction.getFactionName()).isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

        Account factionAccount = economyService.getOrCreateAccount(callerFaction.getFactionName()).get();

        XDollar dollarCurrency = new XDollar();

        caller.sendMessage(Text.of(TextColors.AQUA, "[Economy] | Your current faction's balance is ", dollarCurrency.format(factionAccount.getBalance(dollarCurrency), 2), TextColors.AQUA, "."));
        caller.playSound(SoundTypes.BLOCK_NOTE_HARP, caller.getPosition(), 0.75);


    }

}
