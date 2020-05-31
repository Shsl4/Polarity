package dev.sl4sh.polarity.UI.shops.user;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.UniqueUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.transactionidentifiers.ShopIdentifier;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
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
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class BuyUserShopUI extends UniqueUI {

    @Nonnull
    private final MasterUserShopUI masterShop;

    public BuyUserShopUI(@Nonnull UUID viewerID, @Nonnull MasterUserShopUI masterShop) {
        super(viewerID);
        this.masterShop = masterShop;
    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Purchase shop");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 5);
    }

    @Override
    protected void setupLayout(Inventory newUI) {

        for (Inventory subInv : newUI.slots()) {

            Slot slot = (Slot) subInv;

            SlotIndex idx = slot.getInventoryProperty(SlotIndex.class).get();
            int val = idx.getValue();

            if (val == 13) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.SKULL, 1, Text.of(TextColors.AQUA, "Purchase this shop and sell items to other players"), new ArrayList<>(), true);
                stack.offer(Keys.SKULL_TYPE, SkullTypes.PLAYER);
                stack.offer(Keys.REPRESENTED_PLAYER, GameProfile.of(getTargetViewer().get().getUniqueId()));
                slot.set(stack);
                continue;

            }

            if (val == 30) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.GREEN, "Purchase (Costs ", TextColors.GOLD, masterShop.getPrice(), TextColors.GREEN, ")"), new ArrayList<>(), false);
                stack.offer(Keys.DYE_COLOR, DyeColors.LIME);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 0);
                slot.set(stack);
                continue;

            }

            if (val == 32) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.RED, "Cancel"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Keys.DYE_COLOR, DyeColors.RED);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 1);
                slot.set(stack);
                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

        }

    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event) {

        if (!(event.getSource() instanceof Player)) { return; }

        Player player = (Player) event.getSource();
        ItemStack stack = event.getCursorTransaction().getFinal().createStack();

        if (stack.get(UIStackData.class).isPresent()) {

            StackTypes type = stack.get(Polarity.Keys.UIStack.TYPE).get();
            int buttonID = stack.get(Polarity.Keys.UIStack.BUTTON_ID).get();

            if (type.equals(StackTypes.NAVIGATION_BUTTON)) {

                if (buttonID == 0) {

                    if(masterShop.getOwnerID() != null) {

                        player.sendMessage(Text.of(TextColors.RED, "This shop is already owned by ", masterShop.getOwner().get().getName()));
                        player.closeInventory();
                        return;

                    }

                    makeTransaction(player);

                } else if (buttonID == 1) {

                    player.closeInventory();

                }

            }

        }

    }

    private void makeTransaction(Player player) {

        Optional<PolarityEconomyService> optEconomyService = Polarity.getEconomyService();

        if (optEconomyService.isPresent()) {

            PolarityEconomyService economyService = optEconomyService.get();

            Optional<UniqueAccount> optPlayerAccount = economyService.getOrCreateAccount(player.getUniqueId());

            if (!optPlayerAccount.isPresent()) {
                player.sendMessage(Text.of(TextColors.RED, "Unable to access your account. Please try again."));
                return;
            }

            UniqueAccount playerAccount = optPlayerAccount.get();

            PolarityCurrency dollarCurrency = new PolarityCurrency();

            TransactionResult result = playerAccount.withdraw(dollarCurrency, BigDecimal.valueOf(masterShop.getPrice()), Cause.of(EventContext.empty(), new ShopIdentifier()), new HashSet<>());

            switch (result.getResult()) {

                case ACCOUNT_NO_FUNDS:

                    player.sendMessage(Text.of(TextColors.RED, "You do not have enough money to buy that."));
                    break;

                case SUCCESS:

                    player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 0.25);
                    player.sendMessage(Text.of(TextColors.AQUA, "You just bought a shop."));

                    Utilities.delayOneTick(() -> masterShop.onPurchased(player));

                    player.closeInventory();
                    break;

                case FAILED:

                    player.sendMessage(Text.of(TextColors.RED, "Transaction failed."));
                    break;

            }


        } else {

            player.sendMessage(Text.of(TextColors.RED, "Transaction failed."));

        }

    }

}
