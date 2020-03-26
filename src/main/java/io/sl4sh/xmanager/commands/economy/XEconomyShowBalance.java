package io.sl4sh.xmanager.commands.economy;

import de.dosmike.sponge.megamenus.MegaMenus;
import de.dosmike.sponge.megamenus.api.MenuRenderer;
import de.dosmike.sponge.megamenus.impl.BaseMenuImpl;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XEconomyShopRecipe;
import io.sl4sh.xmanager.economy.XOnShopButtonClick;
import io.sl4sh.xmanager.economy.ui.XButton;
import io.sl4sh.xmanager.economy.XDollar;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.enums.XError;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class XEconomyShowBalance implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Shows your current balance."))
                .permission("xmanager.economy.showbalance")
                .executor(new XEconomyShowBalance())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player caller = (Player) src;

            showBalance(caller);



        } else {

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();
    }

    private void showBalance(Player caller){

        // The economy service will always be present as the command is registered only if the economy service registered
        XEconomyService economyService = XManager.getXManager().getXEconomyService().get();

        Optional<UniqueAccount> optCallerAccount = economyService.getOrCreateAccount(caller.getUniqueId());

        if(!optCallerAccount.isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access your account. Please try again later.")); return; }

        UniqueAccount callerAccount = optCallerAccount.get();

        XDollar dollarCurrency = new XDollar();

        caller.sendMessage(Text.of(TextColors.AQUA, "[Economy] | Your current balance is ", dollarCurrency.format(callerAccount.getBalance(dollarCurrency), 2), TextColors.AQUA, "."));

    }

}
