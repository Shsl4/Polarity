package dev.sl4sh.polarity.UI.shops;

import dev.sl4sh.polarity.UI.SharedUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.transactionidentifiers.ShopIdentifier;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.economy.ShopRecipe;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ShopUI extends SharedUI {

    protected Entity merchant;

    @Nonnull
    protected ShopProfile profile;

    @Nonnull
    @Override
    public Text getTitle() { return merchant.get(Keys.DISPLAY_NAME).isPresent() ? Text.of(merchant.get(Keys.DISPLAY_NAME).get(), "'s Shop") : Text.of("Shop"); }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, profile.getShopPageHeight());
    }

    public ShopUI(@Nonnull ShopProfile profile, @Nonnull Entity merchant) {
        this.profile = profile;
        this.merchant = merchant;
    }

    @Override
    public void setupLayout(Inventory newUI){

        for(Inventory subInv : newUI.slots()){

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            if(profile.getRecipeWithIndex(slotIndex).isPresent()){

                ShopRecipe recipe = profile.getRecipeWithIndex(slotIndex).get();

                List<Text> loreList = new ArrayList<>();
                ItemStack stack = recipe.getTargetItem().createStack();

                if(!recipe.isValidRecipe()){

                    stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);

                }
                else{

                    loreList.add(Text.of(TextColors.AQUA, "Price: ", TextColors.GOLD, "$", recipe.getPrice()));
                    stack.offer(new UIStackData(StackTypes.SHOP_STACK, -1, -1));
                    stack.offer(Keys.ITEM_LORE, loreList);

                }

                slot.set(stack);
                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

        }

    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event){

        if(!(event.getSource() instanceof Player)) { return; }

        Player player = (Player)event.getSource();

        if(event.getCursorTransaction().getDefault().get(Polarity.Keys.UIStack.TYPE).isPresent() && event.getCursorTransaction().getDefault().get(Polarity.Keys.UIStack.TYPE).get().equals(StackTypes.SHOP_STACK)){

            Optional<ShopRecipe> optRecipe = profile.getRecipeBySnapshot(event.getCursorTransaction().getDefault());

            if(optRecipe.isPresent() && optRecipe.get().isValidRecipe()){

                if(event.getCursorTransaction().getDefault().get(Polarity.Keys.UIStack.TYPE).get().equals(StackTypes.SHOP_STACK)){

                    makeTransaction(player, optRecipe.get());
                    refreshUI();

                }

            }

        }

    }

    @Nullable
    protected TransactionResult makeTransaction(Player player, ShopRecipe recipe){

        Optional<PolarityEconomyService> optEconomyService = Polarity.getEconomyService();

        if(optEconomyService.isPresent()){

            PolarityEconomyService economyService = optEconomyService.get();

            Optional<UniqueAccount> optPlayerAccount = economyService.getOrCreateAccount(player.getUniqueId());

            if(!optPlayerAccount.isPresent()) { player.sendMessage(Text.of(TextColors.RED, "Unable to access your account. Please try again.")); return null; }

            UniqueAccount playerAccount = optPlayerAccount.get();

            PolarityCurrency dollarCurrency = new PolarityCurrency();

            if(!player.getInventory().canFit(recipe.getTargetItem().createStack())) { player.sendMessage(Text.of(TextColors.RED, "You do not have space in your inventory.")); return null; }

            TransactionResult result = playerAccount.withdraw(dollarCurrency, BigDecimal.valueOf(recipe.getPrice()), Cause.of(EventContext.empty(), new ShopIdentifier()), new HashSet<>());

            switch(result.getResult()){

                case ACCOUNT_NO_FUNDS:

                    player.sendMessage(Text.of(TextColors.RED, "You do not have enough money to buy that."));
                    break;

                case SUCCESS:

                    ItemStack stack = recipe.getTargetItem().createStack();
                    Text format = Text.of(TextColors.YELLOW, stack.get(Keys.DISPLAY_NAME).orElse(Text.of(recipe.getTargetItem().getTranslation())));
                    player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 0.25);
                    player.sendMessage(Text.of(TextColors.AQUA, "You just bought ", stack.getQuantity(), " ", format, " for ", dollarCurrency.format(BigDecimal.valueOf(recipe.getPrice()), 2), TextColors.AQUA, "."));
                    player.getInventory().offer(stack);

                    break;

                case FAILED:

                    player.sendMessage(Text.of(TextColors.RED, "Transaction failed."));
                    break;

            }

            return result;

        }
        else{

            player.sendMessage(Text.of(TextColors.RED, "Transaction failed."));

        }

        return null;

    }

}
