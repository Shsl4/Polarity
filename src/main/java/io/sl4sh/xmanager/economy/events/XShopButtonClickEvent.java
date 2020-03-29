package io.sl4sh.xmanager.economy.events;

import de.dosmike.sponge.megamenus.api.listener.OnClickListener;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.economy.XEconomyShopRecipe;
import io.sl4sh.xmanager.economy.transactionidentifiers.XShopIdentifier;
import io.sl4sh.xmanager.economy.ui.XButton;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
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

public class XShopButtonClickEvent implements OnClickListener<XButton> {

    @Override
    public void onClick(XButton button, Player player, int i, boolean b) {

        if(button.getShopRecipe() != null && button.getShopRecipe().isValidRecipe()){

            makeTransaction(player, button.getShopRecipe());

        }

    }

    public static void makeTransaction(Player player, XEconomyShopRecipe recipe){

        Optional<XEconomyService> optEconomyService = XManager.getXEconomyService();

        if(optEconomyService.isPresent()){

            XEconomyService economyService = optEconomyService.get();

            Optional<UniqueAccount> optPlayerAccount = economyService.getOrCreateAccount(player.getUniqueId());

            if(!optPlayerAccount.isPresent()) { player.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access your account. Please try again.")); return;}

            UniqueAccount playerAccount = optPlayerAccount.get();

            XDollar dollarCurrency = new XDollar();

            if(!player.getInventory().canFit(recipe.getTargetItem().createStack())) { player.sendMessage(Text.of(TextColors.RED, "[Economy] | You do not have space in your inventory.")); return; }

            TransactionResult result = playerAccount.withdraw(dollarCurrency, BigDecimal.valueOf(recipe.getPrice()), Cause.of(EventContext.empty(), new XShopIdentifier()), new HashSet<>());

            switch(result.getResult()){

                case ACCOUNT_NO_FUNDS:

                    player.sendMessage(Text.of(TextColors.RED, "[Economy] | You do not have enough money to buy that."));
                    break;

                case SUCCESS:

                    String format = recipe.getTargetItem().getTranslation().get(player.getLocale());

                    if (!format.endsWith("s") && recipe.getTargetItem().getQuantity() > 1) { format = format + "s"; }

                    player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 0.75);
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
