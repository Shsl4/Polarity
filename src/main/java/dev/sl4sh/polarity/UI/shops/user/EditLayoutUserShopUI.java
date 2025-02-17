package dev.sl4sh.polarity.UI.shops.user;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.UniqueUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.economy.ShopRecipe;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EditLayoutUserShopUI extends UniqueUI {

    @Nonnull
    private final MasterUserShopUI masterShop;

    public EditLayoutUserShopUI(@Nonnull UUID viewerID, @Nonnull MasterUserShopUI masterShop) {
        super(viewerID);
        this.masterShop = masterShop;
    }

    @Override
    protected void onInteract(InteractInventoryEvent event) {

        event.setCancelled(false);

    }

    @Override
    protected void onClosed(InteractInventoryEvent.Close event) {

        List<ShopRecipe> shopRecipes = new ArrayList<>();

        for(Inventory subInv : getUI().slots()){

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            Optional<ItemStack> itemStack = slot.poll();

            if(itemStack.isPresent()){

                if(Utilities.isUIStack(itemStack.get())){

                    ItemStack stack = itemStack.get();
                    stack.remove(UIStackData.class);

                    if(masterShop.getUserShopProfile().getRecipeBySnapshot(stack.createSnapshot()).isPresent()){

                        ShopRecipe recipe = masterShop.getUserShopProfile().getRecipeBySnapshot(itemStack.get().createSnapshot()).get();
                        recipe.setTargetItem(ItemStack.builder().fromItemStack(stack).quantity(recipe.getTargetItem().getQuantity()).build().createSnapshot());
                        recipe.setIndex(slotIndex);
                        shopRecipes.add(recipe);
                        continue;

                    }

                    shopRecipes.add(new ShopRecipe(0.0f, itemStack.get().createSnapshot(), slotIndex));

                }
                else{

                    Utilities.givePlayer(getTargetViewer().get(), itemStack.get(), true);

                }

            }

        }

        if(getTargetViewer().isPresent()){

            getTargetViewer().get().getInventory().clear();
            Utilities.delayOneTick(() -> Utilities.restorePlayerInventory(getTargetViewer().get()));

        }

        masterShop.getUserShopProfile().setShopRecipes(shopRecipes);
        masterShop.getUserShopProfile().setProfileName(masterShop.getOwner().get().getName() + "'s Shop");

        masterShop.saveData();

        masterShop.getManageShopUI().open();

        getUI().clear();

    }



    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event) {

        if(event.getTransactions().size() <= 0) { return; }

        ItemStack eventStack = event.getTransactions().get(0).getOriginal().createStack();

        if(Utilities.isUIStack(eventStack)) {

            if (eventStack.get(Polarity.Keys.UIStack.TYPE).isPresent() && eventStack.get(Polarity.Keys.UIStack.TYPE).get().equals(StackTypes.NAVIGATION_BUTTON)) {

                getTargetViewer().get().closeInventory();

            }

        }

    }


    @Override
    protected void setupLayout(Inventory newUI) {

        Utilities.savePlayerInventory(getTargetViewer().get());
        getTargetViewer().get().getInventory().clear();

        List<ItemStack> stackList = new ArrayList<>();
        List<ItemStack> existingStacks = new ArrayList<>();

        for(Inventory storageSubInv : masterShop.getManageShopUI().getStorage().slots()){

            Slot storageSlot = (Slot)storageSubInv;

            if(storageSlot.peek().isPresent()){

                if(!Utilities.listContainsStack(stackList, storageSlot.peek().get())){

                    ItemStack stack = ItemStack.builder().from(storageSlot.peek().get()).quantity(1).build();
                    stack.offer(new UIStackData());
                    stackList.add(stack);

                }

            }

        }

        for(Inventory subInv : newUI.slots()){

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            if(masterShop.getUserShopProfile().getRecipeWithIndex(slotIndex).isPresent()){

                ItemStack stack = ItemStack.builder().fromSnapshot(masterShop.getUserShopProfile().getRecipeWithIndex(slotIndex).get().getTargetItem()).quantity(1).build();
                stack.offer(new UIStackData());
                existingStacks.add(stack);
                slot.set(stack);

            }

        }

        for(ItemStack newStack : stackList){

            if(!Utilities.listContainsStack(existingStacks, newStack)){

                Utilities.givePlayer(getTargetViewer().get(), newStack, false);

            }

        }

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of(masterShop.getOwner().get().getName() + "'s Shop Editor");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 5);
    }
}
