package io.sl4sh.xmanager.economy.shops.UI;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.XDataRegistration;
import io.sl4sh.xmanager.data.registration.shopstack.XShopStackData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.sl4sh.xmanager.economy.shops.UI.XSellConfirmationUI.isPlayerStack;

public class XSellDepositUI {

    private Inventory Inv;
    private Player playerRef;
    private boolean confirmed;

    private void onItemClick(ClickInventoryEvent event){

        ItemStack eventStack = event.getCursorTransaction().getDefault().createStack();

        if(!isPlayerStack(eventStack)) {

            event.setCancelled(true);

            Optional<DyeColor> buttonColor = eventStack.get(Keys.DYE_COLOR);

            if (buttonColor.isPresent()) {

                if(buttonColor.get().equals(DyeColors.LIME)){

                    XSellConfirmationUI confUI = new XSellConfirmationUI();

                    List<ItemStack> stacks = new ArrayList<>();

                    for (Inventory subInv : Inv.slots()) {

                        Optional<ItemStack> optStack = subInv.peek();

                        if (optStack.isPresent() && isPlayerStack(optStack.get())) {

                            stacks.add(optStack.get());

                        }

                    }

                    confirmed = true;
                    confUI.makeForPlayer(playerRef, stacks);

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
                .build(XManager.getXManager());

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
                stack.offer(new XShopStackData());
                stack.offer(XDataRegistration.Keys.SHOP_STACK, true);
                slot.set(stack);
                continue;

            }

            ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BLACK).quantity(1).build();
            stack.offer(Keys.DISPLAY_NAME, Text.of());
            stack.offer(new XShopStackData());
            stack.offer(XDataRegistration.Keys.SHOP_STACK, true);
            slot.set(stack);

        }

        player.openInventory(Inv, Text.of("Sell Items"));
        playerRef = player;

    }

    public void onInventoryClosed(InteractInventoryEvent.Close event) {

        if(confirmed) { return; }

        Task.builder().delayTicks(1L).execute(() -> {

            for(Inventory subInv : Inv.slots()){

                Optional<ItemStack> optStack = subInv.peek();

                if(optStack.isPresent() && isPlayerStack(optStack.get())){

                    InventoryTransactionResult result = playerRef.getInventory().offer(optStack.get());

                    if(result.getType().equals(InventoryTransactionResult.Type.FAILURE) ||
                            result.getType().equals(InventoryTransactionResult.Type.ERROR)){

                        Entity item = playerRef.getWorld().createEntity(EntityTypes.ITEM, playerRef.getPosition());
                        item.offer(Keys.REPRESENTED_ITEM, optStack.get().createSnapshot());

                        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
                            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
                            playerRef.getWorld().spawnEntity(item);
                        }

                    }

                }

            }

        }).submit(XManager.getXManager());



    }

}
