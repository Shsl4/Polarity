package dev.sl4sh.polarity.UI.shops.admin;

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
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditLayoutAdminShopUI extends UniqueUI {

    @Nonnull
    private final String profileName;

    @Nonnull
    private final ManageAdminShopUI manageUI;

    public EditLayoutAdminShopUI(@Nonnull Player viewer, @Nonnull String profileName, ManageAdminShopUI manageUI) {
        super(viewer);
        this.profileName = profileName;
        this.manageUI = manageUI;
    }

    @Override
    protected void onClosed(InteractInventoryEvent.Close event) {

        ShopProfile profile = Polarity.getShopProfiles().getShopProfileByName(profileName).get();
        List<ShopRecipe> shopRecipes = new ArrayList<>();

        for(Inventory subInv : getUI().slots()){

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            Optional<ItemStack> itemStack = slot.poll();

            if(itemStack.isPresent()){

                ItemStack stack = itemStack.get();
                stack.remove(UIStackData.class);

                if(profile.getRecipeBySnapshot(stack.createSnapshot()).isPresent()){

                    ShopRecipe recipe = profile.getRecipeBySnapshot(itemStack.get().createSnapshot()).get();
                    recipe.setTargetItem(ItemStack.builder().fromItemStack(stack).quantity(recipe.getTargetItem().getQuantity()).build().createSnapshot());
                    recipe.setIndex(slotIndex);
                    shopRecipes.add(recipe);
                    continue;

                }

                shopRecipes.add(new ShopRecipe(0.0f, stack.createSnapshot(), slotIndex));

            }

        }

        profile.setShopRecipes(shopRecipes);
        Polarity.getShopProfiles().addShopProfile(profile);
        Polarity.getPolarity().writeAllConfig();
        manageUI.open();

    }

    @Override
    protected void onInteract(InteractInventoryEvent event) {

        event.setCancelled(false);

    }

    @Override
    protected void setupLayout(Inventory newUI) {

        ShopProfile profile = Polarity.getShopProfiles().getShopProfileByName(profileName).get();

        for(Inventory subInv : newUI.slots()){

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            if(profile.getRecipeWithIndex(slotIndex).isPresent()){

                ItemStack stack = ItemStack.builder().fromSnapshot(profile.getRecipeWithIndex(slotIndex).get().getTargetItem()).quantity(1).build();
                stack.offer(new UIStackData());
                slot.set(stack);

            }

        }

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Edit " + profileName);
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, Polarity.getShopProfiles().getShopProfileByName(profileName).get().getShopPageHeight());
    }
}
