package dev.sl4sh.polarity.UI.shops.user;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.UniqueUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SellUserShopUI extends UniqueUI {

    @Nonnull
    private final MasterUserShopUI masterShop;

    public SellUserShopUI(@Nonnull UUID viewerID, @Nonnull MasterUserShopUI masterShop) {

        super(viewerID);
        this.masterShop = masterShop;

    }

    @Override
    protected void setupLayout(Inventory newUI) {

        for(Inventory subInv : getUI().slots()){

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            if(slotIndex == 13){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.SKULL, 1, Text.of(TextColors.RED, "You will earn ", new PolarityCurrency().format(BigDecimal.valueOf(masterShop.getPrice() / 2)), TextColors.RED, " by selling your shop."), new ArrayList<>(), true);
                List<Text> loreList = new ArrayList<>();
                loreList.add(Text.EMPTY);
                loreList.add(Text.of(TextStyles.BOLD, TextColors.RED, "WARNING!"));
                loreList.add(Text.EMPTY);
                loreList.add(Text.of(TextStyles.BOLD, TextColors.RED, "Selling your shop will vanish the items in your shop's storage!"));
                loreList.add(Text.of(TextStyles.BOLD, TextColors.RED, "Make sure to empty your shop's storage before proceeding if you wish to keep your items."));
                stack.offer(Keys.ITEM_LORE, loreList);
                stack.offer(Keys.SKULL_TYPE, SkullTypes.WITHER_SKELETON);
                slot.set(stack);
                continue;

            }

            if(slotIndex == 30){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.TNT, 1, Text.of(TextColors.RED, "Proceed"), new ArrayList<>(), true);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 0);
                slot.set(stack);
                continue;

            }

            if(slotIndex == 32){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.WHITE, "Cancel"), new ArrayList<>(), false);
                stack.offer(Keys.DYE_COLOR, DyeColors.WHITE);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
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

        if(event.getTransactions().size() <= 0) { return; }

        ItemStack eventStack = event.getTransactions().get(0).getOriginal().createStack();

        if(Utilities.isUIStack(eventStack)) {

            int buttonID = eventStack.get(Polarity.Keys.UIStack.BUTTON_ID).get();

            if(buttonID == 0){

                masterShop.onSold(getTargetViewer().get());

            }

            if(buttonID == 1){

                masterShop.getManageShopUI().open();

            }

        }

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Sell shop");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 5);
    }

}
