package dev.sl4sh.polarity.UI.shops.user;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.UniqueUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ManageUserShopUI extends UniqueUI {

    @Nonnull
    private final MasterUserShopUI masterShop;

    @Nonnull
    private final EditLayoutUserShopUI editLayoutUI;

    @Nonnull
    private final EditPricesUserShopUI editPricesUI;

    @Nonnull
    private final SellUserShopUI sellShopUI;

    @Nonnull
    private final Inventory shopStorage = Inventory.builder()
            .property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(getTargetViewer().get().getName(), "'s Storage")))
            .property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 6))
            .listener(InteractInventoryEvent.Close.class, this::onStorageClosed)
            .build(Polarity.getPolarity());

    public Inventory getStorage(){

        return shopStorage;

    }

    private void onStorageClosed(InteractInventoryEvent.Close event){

        masterShop.saveData();
        this.open();

    }

    public ManageUserShopUI(@Nonnull Player viewer, @Nonnull MasterUserShopUI masterShop, @Nonnull List<ItemStackSnapshot> stock) {

        super(viewer);
        this.masterShop = masterShop;

        for(ItemStackSnapshot snap : stock){

            shopStorage.offer(snap.createStack());

        }

        this.editLayoutUI = new EditLayoutUserShopUI(viewer, masterShop);
        this.editPricesUI = new EditPricesUserShopUI(viewer, masterShop);
        this.sellShopUI = new SellUserShopUI(viewer, masterShop);

    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event) {

        if (!(event.getSource() instanceof Player)) { return; }

        if(!masterShop.getOwner().isPresent()) { return; }

        Player player = (Player) event.getSource();

        if(masterShop.getOwner().get() != player) { return; }

        ItemStack stack = event.getCursorTransaction().getFinal().createStack();

        if (stack.get(UIStackData.class).isPresent()) {

            StackTypes type = stack.get(Polarity.Keys.UIStack.TYPE).get();
            int buttonID = stack.get(Polarity.Keys.UIStack.BUTTON_ID).get();

            if (type.equals(StackTypes.NAVIGATION_BUTTON)) {

                if (buttonID == 0) {

                    masterShop.forceOpen(getTargetViewer().get());

                }
                else if (buttonID == 1) {

                    editLayoutUI.open();

                }
                else if(buttonID == 2){

                    editPricesUI.open();

                }
                else if(buttonID == 3){

                    getTargetViewer().get().openInventory(shopStorage);

                }
                else if(buttonID == 4){

                    sellShopUI.open();

                }

            }

        }

    }

    @Override
    protected void setupLayout(Inventory newUI) {

        for (Inventory subInv : newUI.slots()) {

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            if (slotIndex == 10) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.SKULL, 1, Text.of(TextColors.GRAY, "Preview Shop"), new ArrayList<>(), false);
                stack.offer(Keys.SKULL_TYPE, SkullTypes.PLAYER);
                stack.offer(Keys.REPRESENTED_PLAYER, GameProfile.of(getTargetViewer().get().getUniqueId()));
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 0);
                slot.set(stack);
                continue;

            }

            if (slotIndex == 12) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.IRON_AXE, 1, Text.of(TextColors.WHITE, "Edit Layout"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 1);
                slot.set(stack);
                continue;

            }

            if (slotIndex == 14) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.DIAMOND, 1, Text.of(TextColors.AQUA, "Set Prices / Quantities"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 2);
                slot.set(stack);
                continue;

            }

            if (slotIndex == 16) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.CHEST, 1, Text.of(TextColors.GOLD, "Open Storage"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 3);
                slot.set(stack);
                continue;

            }

            if (slotIndex == 31) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.TNT, 1, Text.of(TextColors.RED, "Sell shop"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 4);
                slot.set(stack);
                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

        }

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Manage Shop");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 5);
    }

}
