package dev.sl4sh.polarity.UI.shops.admin;

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
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.UUID;

public class ManageAdminShopUI extends UniqueUI {

    @Nonnull
    private final EditLayoutAdminShopUI editLayoutUI;

    @Nonnull
    private final EditPricesAdminShopUI editPricesUI;

    private final String profileName;

    public ManageAdminShopUI(@Nonnull UUID viewerID, @Nonnull String profileName) {

        super(viewerID);

        this.profileName = profileName;
        this.editLayoutUI = new EditLayoutAdminShopUI(viewerID, profileName, this);
        this.editPricesUI = new EditPricesAdminShopUI(viewerID, profileName, this);

    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event) {

        if (!(event.getSource() instanceof Player)) { return; }

        ItemStack stack = event.getCursorTransaction().getFinal().createStack();

        if (stack.get(UIStackData.class).isPresent()) {

            StackTypes type = stack.get(Polarity.Keys.UIStack.TYPE).get();
            int buttonID = stack.get(Polarity.Keys.UIStack.BUTTON_ID).get();

            if (type.equals(StackTypes.NAVIGATION_BUTTON)) {

                if (buttonID == 0) {

                    Polarity.getNPCManager().makeAdminShopNPC(getTargetViewer().get(), profileName);
                    getTargetViewer().get().closeInventory();

                }
                else if (buttonID == 1) {

                    editLayoutUI.open();

                }
                else if(buttonID == 2){

                    editPricesUI.open();

                }
                else if(buttonID == 3){

                    Polarity.getShopProfiles().removeProfileByName(profileName);
                    Polarity.getPolarity().writeAllConfig();
                    getTargetViewer().get().closeInventory();

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

                ItemStack stack = Utilities.makeUIStack(ItemTypes.SKULL, 1, Text.of(TextColors.GRAY, "Summon Shop"), new ArrayList<>(), false);
                stack.offer(Keys.SKULL_TYPE, SkullTypes.PLAYER);
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

                ItemStack stack = Utilities.makeUIStack(ItemTypes.TNT, 1, Text.of(TextColors.RED, "Delete Profile (Irreversible)"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 3);
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
        return Text.of("Manage " + profileName);
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 3);
    }

}
