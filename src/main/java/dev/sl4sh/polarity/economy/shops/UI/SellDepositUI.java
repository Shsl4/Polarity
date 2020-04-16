package dev.sl4sh.polarity.economy.shops.UI;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.DataRegistration;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.sl4sh.polarity.economy.shops.UI.SellConfirmationUI.isPlayerStack;

public class SellDepositUI {

    private Inventory Inv;
    private Player playerRef;
    private boolean confirmed;
    private List<ItemStack> stacks = new ArrayList<>();

    private void onItemClick(ClickInventoryEvent event){

        ItemStack eventStack = event.getCursorTransaction().getDefault().createStack();

        if(!isPlayerStack(eventStack)) {

            event.setCancelled(true);

            Optional<DyeColor> buttonColor = eventStack.get(Keys.DYE_COLOR);

            if (buttonColor.isPresent()) {

                if(buttonColor.get().equals(DyeColors.LIME)){

                    confirmed = true;

                    for (Inventory subInv : Inv.slots()) {

                        Optional<ItemStack> optStack = subInv.peek();

                        if (optStack.isPresent() && isPlayerStack(optStack.get())) {

                            stacks.add(optStack.get());

                        }

                    }

                    Task.builder().delayTicks(1L).execute(() -> { playerRef.closeInventory(); }).submit(Polarity.getPolarity());

                }

            }

        }

    }

    public void makeForPlayer(Player player){

        Inv = Inventory.builder()
                .property("title", new InventoryTitle(Text.of("Sell Items")))
                .property("inventorydimension", new InventoryDimension(9, 6))
                .listener(ClickInventoryEvent.Primary.class, this::onItemClick)
                .listener(ClickInventoryEvent.Secondary.class, this::onItemClick)
                .listener(InteractInventoryEvent.Close.class, this::onInventoryClosed)
                .listener(ClickInventoryEvent.Drag.class, this::onItemClick)
                .listener(ClickInventoryEvent.Middle.class, this::onItemClick)
                .listener(ClickInventoryEvent.Double.class, this::onItemClick)
                .listener(ClickInventoryEvent.Drop.class, this::onItemClick)
                .build(Polarity.getPolarity());

        for(Inventory subInv : Inv.slots()){

            Slot slot = (Slot)subInv;

            SlotIndex idx = slot.getInventoryProperty(SlotIndex.class).get();
            int val = idx.getValue();

            if(val >= 10 && val <= 16 || val >= 19 && val <= 25){

                continue;

            }

            if(val == 40){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.LIME).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Confirm"));
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }

            ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BLACK).quantity(1).build();
            stack.offer(Keys.DISPLAY_NAME, Text.of());
            stack.offer(new UIStackData());
            stack.offer(DataRegistration.Keys.UI_STACK, true);
            slot.set(stack);

        }

        player.openInventory(Inv, Text.of("Sell Items"));
        playerRef = player;

    }

    public void onInventoryClosed(InteractInventoryEvent.Close event) {

        if(!confirmed){

            for(Inventory subInv : Inv.slots()){

                Optional<ItemStack> optStack = subInv.peek();

                if(optStack.isPresent() && isPlayerStack(optStack.get())){

                    /* Give the player all its items back if the transaction has not been confirmed
                    this NEEDS to be delayed otherwise the give won't always work */
                    Task.builder().delayTicks(1L).execute(() -> {

                        Utilities.givePlayer(playerRef, optStack.get());

                    }).submit(Polarity.getPolarity());

                }

            }

        }
        else{

            SellConfirmationUI confUI = new SellConfirmationUI();

            Task.builder().delayTicks(1L).execute(() -> {

                confUI.makeForPlayer(playerRef, stacks);

            }).submit(Polarity.getPolarity());

        }

    }

}
