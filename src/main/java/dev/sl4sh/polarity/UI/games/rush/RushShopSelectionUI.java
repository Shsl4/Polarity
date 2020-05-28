package dev.sl4sh.polarity.UI.games.rush;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.SharedUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.sound.SoundTypes;
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

public class RushShopSelectionUI extends SharedUI {

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

                    player.playSound(SoundTypes.UI_BUTTON_CLICK, player.getPosition(), 0.25);
                    new RushBlockShopUI(player.getUniqueId(), this).open();

                }
                else if (buttonID == 1) {

                    player.playSound(SoundTypes.UI_BUTTON_CLICK, player.getPosition(), 0.25);
                    new RushWeaponShopUI(player.getUniqueId(), this).open();

                }
                else if(buttonID == 2){

                    player.playSound(SoundTypes.UI_BUTTON_CLICK, player.getPosition(), 0.25);
                    new RushArmorShopUI(player.getUniqueId(), this).open();

                }
                else if(buttonID == 3){

                    player.playSound(SoundTypes.UI_BUTTON_CLICK, player.getPosition(), 0.25);
                    new RushMiscShopUI(player.getUniqueId(), this).open();

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

                ItemStack stack = Utilities.makeUIStack(ItemTypes.SANDSTONE, 1, Text.of(TextColors.YELLOW, "Blocks"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 0);
                slot.set(stack);
                continue;

            }

            if (slotIndex == 12) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.IRON_SWORD, 1, Text.of(TextColors.RED, "Weapons and Tools"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 1);
                slot.set(stack);
                continue;

            }

            if (slotIndex == 14) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.DIAMOND_CHESTPLATE, 1, Text.of(TextColors.AQUA, "Armors"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 2);
                slot.set(stack);
                continue;

            }

            if (slotIndex == 16) {

                ItemStack stack = Utilities.makeUIStack(ItemTypes.FLINT_AND_STEEL, 1, Text.of(TextColors.GOLD, "Miscellaneous"), new ArrayList<>(), false);
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
        return Text.of("Shop Selection");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 3);
    }

}
