package dev.sl4sh.polarity.UI.shops.user;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.UniqueUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.economy.ShopRecipe;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.*;

public class EditLayoutUserShopUI extends UniqueUI {

    @Nonnull
    private final MasterUserShopUI masterShop;

    private boolean canEdit = false;

    public EditLayoutUserShopUI(@Nonnull Player viewer, @Nonnull MasterUserShopUI masterShop) {
        super(viewer);
        this.masterShop = masterShop;
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

        if (canEdit){

            getTargetViewer().get().getInventory().clear();
            Utilities.restorePlayerInventory(getTargetViewer().get());

        }

        masterShop.getUserShopProfile().setShopRecipes(shopRecipes);
        masterShop.getUserShopProfile().setProfileName(masterShop.getOwner().get().getName() + "'s Shop");

        masterShop.saveData();

        masterShop.getManageShopUI().open();

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
    protected void onInteract(InteractInventoryEvent event) {

        event.setCancelled(false);

    }

    @Override
    protected void setupLayout(Inventory newUI) {

        if(masterShop.getManageShopUI().getStorage().size() <= 0){

            for(Inventory subInv : getUI().slots()){

                Slot slot = (Slot)subInv;
                SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
                int slotIndex = property.getValue();

                if(slotIndex == 13){

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.NETHER_STAR, 1, Text.of(TextColors.RED, "You need to fill your shop's storage with items in order to be able to sell."), new ArrayList<>(), true);
                    slot.set(stack);
                    continue;

                }

                if(slotIndex == 31){

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.GREEN, "Dismiss"), new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.WHITE);
                    stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                    stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 0);
                    slot.set(stack);
                    continue;

                }

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
                stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
                slot.set(stack);

            }

            canEdit = false;
            return;

        }

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

        canEdit = true;

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
