package dev.sl4sh.polarity.UI.games.rush;

import dev.sl4sh.polarity.economy.ItemShopRecipe;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class RushMiscShopUI extends RushPurchaseUI{

    public RushMiscShopUI(@Nonnull Player viewer, RushShopSelectionUI selectionUI) {
        super(viewer, selectionUI);

        List<PotionEffect> healEffect = Collections.singletonList(PotionEffect.builder().particles(true).duration(1).potionType(PotionEffectTypes.INSTANT_HEALTH).amplifier(1).build());

        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.EMERALD).quantity(1).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.TNT).quantity(1).build().createSnapshot(), 10));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.EMERALD).quantity(2).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.FLINT_AND_STEEL).quantity(1).build().createSnapshot(), 11));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(4).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.COOKED_BEEF).quantity(16).build().createSnapshot(), 13));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.BRICK).quantity(32).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.BREAD).quantity(8).build().createSnapshot(), 14));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).quantity(4).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.GOLDEN_APPLE).quantity(1).build().createSnapshot(), 15));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).quantity(8).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.SPLASH_POTION).add(Keys.POTION_EFFECTS, healEffect).add(Keys.DISPLAY_NAME, Text.of(TextColors.LIGHT_PURPLE, "Healing Potion")).quantity(1).build().createSnapshot(), 16));

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Miscellaneous");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 3);
    }

}
