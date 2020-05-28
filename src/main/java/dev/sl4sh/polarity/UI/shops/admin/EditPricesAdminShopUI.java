package dev.sl4sh.polarity.UI.shops.admin;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.UniqueUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.economy.ShopRecipe;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
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
import java.util.List;
import java.util.UUID;

public class EditPricesAdminShopUI extends UniqueUI {

    @Nonnull
    private final String profileName;

    @Nonnull
    private final ManageAdminShopUI manageUI;

    int mode = 0;
    float priceStep = 0.5f;
    int quantityStep = 1;

    public EditPricesAdminShopUI(@Nonnull UUID viewerID, @Nonnull String profileName, ManageAdminShopUI manageUI) {
        super(viewerID);

        this.profileName = profileName;
        this.manageUI = manageUI;
        Sponge.getEventManager().registerListeners(Polarity.getPolarity(), this);

    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event) {

        if(event.getTransactions().size() <= 0) { return; }

        ItemStack eventStack = event.getTransactions().get(0).getOriginal().createStack();

        if(Utilities.isUIStack(eventStack)) {

            if(eventStack.get(Polarity.Keys.UIStack.TYPE).get().equals(StackTypes.SHOP_STACK)){

                ShopProfile profile = Polarity.getShopProfiles().getShopProfileByName(profileName).get();

                for(Inventory subInv : getUI().slots()){

                    Slot slot = (Slot)subInv;
                    SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
                    int slotIndex = property.getValue();

                    if(!slot.contains(eventStack)){ continue; }

                    if(mode == 0){

                        ShopRecipe recipe = profile.getRecipeWithIndex(slotIndex).get();
                        recipe.setPrice(recipe.getPrice() + priceStep);
                        getTargetViewer().get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, getTargetViewer().get().getPosition(),.25);
                        refreshUI();

                    }

                    if(mode == 1){

                        ShopRecipe recipe = profile.getRecipeWithIndex(slotIndex).get();

                        if(recipe.getTargetItem().getQuantity() + quantityStep >= 64){

                            recipe.setTargetItem(ItemStack.builder().fromSnapshot(recipe.getTargetItem()).quantity(64).build().createSnapshot());
                            getTargetViewer().get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, getTargetViewer().get().getPosition(),.25);
                            refreshUI();
                            return;

                        }

                        recipe.setTargetItem(ItemStack.builder().fromSnapshot(recipe.getTargetItem()).quantity(recipe.getTargetItem().getQuantity() + quantityStep).build().createSnapshot());
                        getTargetViewer().get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, getTargetViewer().get().getPosition(),.25);
                        refreshUI();

                    }

                }

            }
            else if(eventStack.get(Polarity.Keys.UIStack.TYPE).get().equals(StackTypes.EDITION_BUTTON)){

                int buttonID = eventStack.get(Polarity.Keys.UIStack.BUTTON_ID).get();

                if(buttonID == 5) { mode = (mode == 0) ? 1 : 0; refreshUI(); }
                else {

                    if(mode == 0){

                        priceStep = (0.5f * (float)Math.pow(10.0, buttonID));

                    }
                    if(mode == 1){

                        quantityStep = (int)Math.pow(2, buttonID);

                    }

                }

                getTargetViewer().get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, getTargetViewer().get().getPosition(),.25);
                refreshUI();

            }

        }

    }

    @Override
    protected void onSecondary(ClickInventoryEvent.Secondary event) {

        ItemStack eventStack = event.getTransactions().get(0).getOriginal().createStack();

        if(Utilities.isUIStack(eventStack)) {

            if(eventStack.get(Polarity.Keys.UIStack.TYPE).get().equals(StackTypes.SHOP_STACK)){

                ShopProfile profile = Polarity.getShopProfiles().getShopProfileByName(profileName).get();

                for(Inventory subInv : getUI().slots()){

                    Slot slot = (Slot)subInv;
                    SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
                    int slotIndex = property.getValue();

                    if(!slot.contains(eventStack)){ continue; }

                    if(mode == 0){

                        ShopRecipe recipe = profile.getRecipeWithIndex(slotIndex).get();
                        recipe.setPrice(recipe.getPrice() - priceStep);
                        getTargetViewer().get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, getTargetViewer().get().getPosition(),.25);
                        refreshUI();

                    }

                    if(mode == 1){

                        ShopRecipe recipe = profile.getRecipeWithIndex(slotIndex).get();

                        if(recipe.getTargetItem().getQuantity() - quantityStep <= 1){

                            recipe.setTargetItem(ItemStack.builder().fromSnapshot(recipe.getTargetItem()).quantity(1).build().createSnapshot());
                            getTargetViewer().get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, getTargetViewer().get().getPosition(),.25);
                            refreshUI();
                            return;

                        }

                        recipe.setTargetItem(ItemStack.builder().fromSnapshot(recipe.getTargetItem()).quantity(recipe.getTargetItem().getQuantity() - quantityStep).build().createSnapshot());
                        getTargetViewer().get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, getTargetViewer().get().getPosition(),.25);
                        refreshUI();

                    }

                }

            }

        }

    }

    @Override
    protected void setupLayout(Inventory newUI) {

        int idx = 0;

        Utilities.savePlayerInventory(getTargetViewer().get());
        getTargetViewer().get().getInventory().clear();

        for(Inventory subInv : getTargetViewer().get().getInventory().slots()){

            Slot slot = (Slot)subInv;

            if(idx < 36){

                if(idx == 0){

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.LIGHT_PURPLE, (mode == 0 ? "Add / Remove 0.5" : "Increase / Decrease 1")), new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.MAGENTA);
                    stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.EDITION_BUTTON);
                    stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 0);
                    slot.set(stack);

                }
                else if(idx == 1){

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.BLUE, (mode == 0 ? "Add / Remove 5" : "Increase / Decrease 2")), new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE);
                    stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.EDITION_BUTTON);
                    stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 1);
                    slot.set(stack);

                }
                else if(idx == 2){

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.GREEN, (mode == 0 ? "Add / Remove 50" : "Increase / Decrease 4")), new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.LIME);
                    stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.EDITION_BUTTON);
                    stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 2);
                    slot.set(stack);

                }
                else if(idx == 3){

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.GOLD, (mode == 0 ? "Add / Remove 500" : "Increase / Decrease 8")), new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.ORANGE);
                    stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.EDITION_BUTTON);
                    stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 3);
                    slot.set(stack);

                }
                else if(idx == 4){

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.RED, (mode == 0 ? "Add / Remove 5000" : "Increase / Decrease 16")), new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.RED);
                    stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.EDITION_BUTTON);
                    stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 4);
                    slot.set(stack);

                }
                else if(idx == 6){

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.WHITE, (mode == 0 ? "Edit Quantity" : "Edit Price")), new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.WHITE);
                    stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.EDITION_BUTTON);
                    stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 5);
                    slot.set(stack);

                }
                else if(idx == 8){

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.YELLOW, "Help"), new ArrayList<>(), false);
                    List<Text> loreList = new ArrayList<>();

                    if(mode == 0){

                        Text colored = Text.of(TextColors.LIGHT_PURPLE, "C", TextColors.BLUE, "o", TextColors.GREEN, "l", TextColors.GOLD, "o", TextColors.RED, "r", TextColors.WHITE, "e", TextColors.YELLOW, "d");

                        loreList.add(Text.of(TextColors.YELLOW, "Modify the price and amount of sold items in your shop."));
                        loreList.add(Text.EMPTY);
                        loreList.add(Text.of(TextColors.YELLOW, "Active mode : Price editing"));
                        loreList.add(Text.EMPTY);
                        loreList.add(Text.of(colored, TextColors.YELLOW, " Panes: Changes the price modification amount per click."));
                        loreList.add(Text.of(TextColors.YELLOW, "Active Price : Increase / Decrease ", priceStep));
                        loreList.add(Text.EMPTY);
                        loreList.add(Text.of(TextColors.YELLOW, "Left Click : Increases the item's price by the selected amount."));
                        loreList.add(Text.of(TextColors.YELLOW, "Right Click : Decreases the item's price by the selected amount."));

                    }
                    else{

                        Text colored = Text.of(TextColors.LIGHT_PURPLE, "C", TextColors.BLUE, "o", TextColors.GREEN, "l", TextColors.GOLD, "o", TextColors.RED, "r", TextColors.WHITE, "e", TextColors.YELLOW, "d");

                        loreList.add(Text.of(TextColors.YELLOW, "Modify the price and amount of sold items in your shop."));
                        loreList.add(Text.EMPTY);
                        loreList.add(Text.of(TextColors.YELLOW, "Active mode : Quantity editing"));
                        loreList.add(Text.EMPTY);
                        loreList.add(Text.of(colored, TextColors.YELLOW, " Panes: Changes quantity the modification amount per click."));
                        loreList.add(Text.of(TextColors.YELLOW, "Active Quantity : Increase / Decrease ", quantityStep));
                        loreList.add(Text.EMPTY);
                        loreList.add(Text.of(TextColors.YELLOW, "Left Click : Increases the item's quantity by the selected amount."));
                        loreList.add(Text.of(TextColors.YELLOW, "Right Click : Decreases the item's quantity by the selected amount."));

                    }

                    stack.offer(Keys.DYE_COLOR, DyeColors.YELLOW);
                    stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.PLACEHOLDER);
                    stack.offer(Keys.ITEM_LORE, loreList);
                    slot.set(stack);

                }
                else{

                    ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
                    slot.set(stack);

                }


            }
            else{

                break;

            }

            idx++;

        }

        ShopProfile profile = Polarity.getShopProfiles().getShopProfileByName(profileName).get();

        for(Inventory subInv : newUI.slots()){

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            if(profile.getRecipeWithIndex(slotIndex).isPresent()){

                ShopRecipe recipe = profile.getRecipeWithIndex(slotIndex).get();

                List<Text> loreList = new ArrayList<>();
                ItemStack stack = recipe.getTargetItem().createStack();

                loreList.add(Text.of(TextColors.AQUA, "Price: ", TextColors.GOLD, "$", recipe.getPrice()));
                stack.offer(new UIStackData(StackTypes.SHOP_STACK, -1, -1));
                stack.offer(Keys.ITEM_LORE, loreList);

                slot.set(stack);
                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

        }

    }

    @Override
    protected void onClosed(InteractInventoryEvent.Close event) {

        getTargetViewer().get().getInventory().clear();
        Polarity.getPolarity().writeAllConfig();
        manageUI.open();
        Utilities.restorePlayerInventory(getTargetViewer().get());

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Edit Items");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, Polarity.getShopProfiles().getShopProfileByName(profileName).get().getShopPageHeight());
    }

}
