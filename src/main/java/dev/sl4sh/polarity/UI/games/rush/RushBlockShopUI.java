package dev.sl4sh.polarity.UI.games.rush;

import dev.sl4sh.polarity.economy.ItemShopRecipe;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.UUID;

public class RushBlockShopUI extends RushPurchaseUI{


    public RushBlockShopUI(@Nonnull UUID viewerID, RushShopSelectionUI selectionUI) {
        super(viewerID, selectionUI);

        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.BRICK).quantity(16).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.SANDSTONE).quantity(16).build().createSnapshot(), 10));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.BRICK).quantity(16).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.STAINED_HARDENED_CLAY).add(Keys.DYE_COLOR, DyeColors.WHITE).quantity(16).build().createSnapshot(), 11));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.BRICK).quantity(64).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.END_STONE).quantity(16).build().createSnapshot(), 12));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(4).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.SLIME).quantity(1).build().createSnapshot(), 13));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(16).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.SOUL_SAND).quantity(8).build().createSnapshot(), 14));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).quantity(4).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.MAGMA).quantity(8).build().createSnapshot(), 15));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.DIAMOND).quantity(1).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.OBSIDIAN).quantity(1).build().createSnapshot(), 16));

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Blocks");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 3);
    }

}
