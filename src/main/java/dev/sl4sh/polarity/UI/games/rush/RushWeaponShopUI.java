package dev.sl4sh.polarity.UI.games.rush;

import dev.sl4sh.polarity.economy.ItemShopRecipe;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RushWeaponShopUI extends RushPurchaseUI{

    public RushWeaponShopUI(@Nonnull UUID viewerID, RushShopSelectionUI selectionUI) {

        super(viewerID, selectionUI);

        ItemStack stoneSwordStack = ItemStack.builder().itemType(ItemTypes.STONE_SWORD).build();
        ItemStack ironSwordStack = ItemStack.builder().itemType(ItemTypes.IRON_SWORD).build();
        ItemStack diamondSwordStack = ItemStack.builder().itemType(ItemTypes.DIAMOND_SWORD).build();

        ItemStack stonePickStack = ItemStack.builder().itemType(ItemTypes.STONE_PICKAXE).build();
        ItemStack ironPickStack = ItemStack.builder().itemType(ItemTypes.IRON_PICKAXE).build();
        ItemStack diamondPickStack = ItemStack.builder().itemType(ItemTypes.DIAMOND_PICKAXE).build();

        ItemStack bowStack = ItemStack.builder().itemType(ItemTypes.BOW).build();
        ItemStack instantBowStack = ItemStack.builder().itemType(ItemTypes.BOW).build();

        List<Enchantment> swordEnchantments = Arrays.asList(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(1).build(), Enchantment.builder().type(EnchantmentTypes.SHARPNESS).level(1).build());
        List<Enchantment> pickEnchantments = Arrays.asList(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(1).build(), Enchantment.builder().type(EnchantmentTypes.EFFICIENCY).level(1).build());

        stoneSwordStack.offer(Keys.ITEM_ENCHANTMENTS, swordEnchantments);
        stoneSwordStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "Common Sword"));

        ironSwordStack.offer(Keys.ITEM_ENCHANTMENTS, swordEnchantments);
        ironSwordStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Reinforced Sword"));

        diamondSwordStack.offer(Keys.ITEM_ENCHANTMENTS, swordEnchantments);
        diamondSwordStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Legendary Sword"));

        stonePickStack.offer(Keys.ITEM_ENCHANTMENTS, pickEnchantments);
        stonePickStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "Common Pickaxe"));

        ironPickStack.offer(Keys.ITEM_ENCHANTMENTS, pickEnchantments);
        ironPickStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Reinforced Pickaxe"));

        diamondPickStack.offer(Keys.ITEM_ENCHANTMENTS, pickEnchantments);
        diamondPickStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Legendary Pickaxe"));

        bowStack.offer(Keys.ITEM_ENCHANTMENTS, Arrays.asList(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(1).build(), Enchantment.builder().type(EnchantmentTypes.POWER).level(1).build()));
        bowStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_PURPLE, "Rush Bow"));

        instantBowStack.offer(Keys.ITEM_ENCHANTMENTS, Collections.singletonList(Enchantment.builder().type(EnchantmentTypes.POWER).level(10000).build()));
        instantBowStack.offer(Keys.ITEM_DURABILITY, 1);
        instantBowStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Two shots, two kills"));
        instantBowStack.offer(Keys.HIDE_ENCHANTMENTS, true);

        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.BRICK).quantity(32).build().createSnapshot(), stoneSwordStack.createSnapshot(), 1));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(16).build().createSnapshot(), ironSwordStack.createSnapshot(), 2));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).quantity(16).build().createSnapshot(), diamondSwordStack.createSnapshot(), 3));

        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).quantity(8).build().createSnapshot(), bowStack.createSnapshot(), 14));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.DIAMOND).quantity(5).build().createSnapshot(), instantBowStack.createSnapshot(), 15));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(8).build().createSnapshot(), ItemStack.builder().itemType(ItemTypes.ARROW).quantity(8).build().createSnapshot(), 16));

        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.BRICK).quantity(32).build().createSnapshot(), stonePickStack.createSnapshot(), 19));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(8).build().createSnapshot(), ironPickStack.createSnapshot(), 20));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).quantity(8).build().createSnapshot(), diamondPickStack.createSnapshot(), 21));

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Weapons and Tools");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 3);
    }

}
