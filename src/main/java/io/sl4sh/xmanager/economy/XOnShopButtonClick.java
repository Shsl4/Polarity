package io.sl4sh.xmanager.economy;

import de.dosmike.sponge.megamenus.api.listener.OnClickListener;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.ui.XButton;
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

public class XOnShopButtonClick implements OnClickListener<XButton> {

    @Override
    public void onClick(XButton button, Player player, int i, boolean b) {

        if(button.getShopRecipe() != null && button.getShopRecipe().isValidRecipe()){

            makeTransaction(player, button.getShopRecipe(), button);

        }

    }

    public static void makeTransaction(Player player, XEconomyShopRecipe recipe, XButton button){

        Optional<XEconomyService> optEconomyService = XManager.getXManager().getXEconomyService();

        if(optEconomyService.isPresent()){

            XEconomyService economyService = optEconomyService.get();

            Optional<UniqueAccount> optPlayerAccount = economyService.getOrCreateAccount(player.getUniqueId());

            if(!optPlayerAccount.isPresent()) { player.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access your account. Please try again.")); return;}

            UniqueAccount playerAccount = optPlayerAccount.get();

            XDollar dollarCurrency = new XDollar();

            TransactionResult result = playerAccount.withdraw(dollarCurrency, BigDecimal.valueOf(recipe.getPrice()), Cause.of(EventContext.empty(), new XShopIdentifier()), new HashSet<>());

            switch(result.getResult()){

                case ACCOUNT_NO_FUNDS:

                    player.sendMessage(Text.of(TextColors.RED, "[Economy] | You do not have enough money to buy that."));
                    break;

                case SUCCESS:

                    String format = recipe.getTargetItem().getTranslation().get(player.getLocale());

                    if (!format.endsWith("s") && recipe.getTargetItem().getQuantity() > 1) { format = format + "s"; }

                    player.sendMessage(Text.of(TextColors.AQUA, "[Economy] | You just bought ", recipe.getTargetItem().getQuantity(), " ", format, " for ", dollarCurrency.format(BigDecimal.valueOf(recipe.getPrice()), 2), TextColors.AQUA, "."));
                    player.getInventory().offer(recipe.getTargetItem().createStack());

                    break;

                case FAILED:

                    player.sendMessage(Text.of(TextColors.RED, "[Economy] | Transaction failed."));
                    break;

            }


        }
        else{

            player.sendMessage(Text.of(TextColors.RED, "[Economy] | Failed to get economy service. It may have been disabled by your administrator."));

        }

    }

}
