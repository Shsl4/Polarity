package dev.sl4sh.polarity.UI.shops;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.UniqueUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SellDepositUI extends UniqueUI {

    private boolean confirmed;
    private List<ItemStack> stacks = new ArrayList<>();

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Sell Items");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 6);
    }

    public SellDepositUI(@Nonnull UUID viewerID){

        super(viewerID);

    }

    @Override
    protected void onInteract(InteractInventoryEvent event) {

        if(event instanceof ClickInventoryEvent.Primary){

            ClickInventoryEvent.Primary primaryEvent = (ClickInventoryEvent.Primary)event;

            if(primaryEvent.getTransactions().size() <= 0) { return; }

            Polarity.getLogger().info("Inventory: " + event.getTargetInventory());
            Polarity.getLogger().info("Event: " + event.toString());

            for(Transaction<ItemStackSnapshot> transaction : primaryEvent.getTransactions()){

                Polarity.getLogger().info("Original: " + transaction.getOriginal() + " Value: " + Utilities.isUIStack(transaction.getOriginal().createStack()));
                Polarity.getLogger().info("Default: " + transaction.getDefault() + " Value: " + Utilities.isUIStack(transaction.getDefault().createStack()));
                Polarity.getLogger().info("Final: " + transaction.getFinal() + " Value: " + Utilities.isUIStack(transaction.getFinal().createStack()));

                if(Utilities.isUIStack(transaction.getOriginal().createStack()) || Utilities.isUIStack(transaction.getFinal().createStack())){

                    event.setCancelled(true);

                    if (transaction.getOriginal().get(Polarity.Keys.UIStack.TYPE).isPresent() && transaction.getOriginal().get(Polarity.Keys.UIStack.TYPE).get().equals(StackTypes.NAVIGATION_BUTTON)) {

                        confirmed = true;

                        for (Inventory subInv : getUI().slots()) {

                            Optional<ItemStack> optStack = subInv.peek();

                            if (optStack.isPresent() && !Utilities.isUIStack(optStack.get())) {

                                stacks.add(optStack.get());

                            }

                            Task.builder().delayTicks(1L).execute(() -> getTargetViewer().get().closeInventory()).submit(Polarity.getPolarity());

                        }

                    }

                }
                else{

                    event.setCancelled(false);

                }

                return;

            }


        }

        event.setCancelled(true);

    }

    @Override
    public void setupLayout(Inventory newUI){

        for(Inventory subInv : newUI.slots()){

            Slot slot = (Slot)subInv;

            SlotIndex idx = slot.getInventoryProperty(SlotIndex.class).get();
            int val = idx.getValue();

            if(val >= 10 && val <= 16 || val >= 19 && val <= 25){

                continue;

            }

            if(val == 40){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.GREEN, "Confirm"), new ArrayList<>(), false);
                stack.offer(Keys.DYE_COLOR, DyeColors.LIME);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                slot.set(stack);
                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

        }

    }

    @Override
    public void onClosed(InteractInventoryEvent.Close event) {

        if(!confirmed){

            for(Inventory subInv : getUI().slots()){

                Optional<ItemStack> optStack = subInv.peek();

                if(optStack.isPresent() && !Utilities.isUIStack(optStack.get())){

                    /* Give the player all its items back if the transaction has not been confirmed
                    this NEEDS to be delayed otherwise the give won't always work ()*/
                    Utilities.delayOneTick(() -> Utilities.givePlayer(getTargetViewer().get(), optStack.get(), true));

                }

            }

        }
        else{

            Utilities.delayOneTick(new SellConfirmationUI(getTargetViewer().get().getUniqueId(), stacks)::open);

        }

    }

}
