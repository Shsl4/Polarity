package dev.sl4sh.polarity.UI.games.rush;

import dev.sl4sh.polarity.economy.ItemShopRecipe;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class RushArmorShopUI extends RushPurchaseUI{

    public RushArmorShopUI(@Nonnull Player viewer, RushShopSelectionUI selectionUI) {

        super(viewer, selectionUI);

        ItemStack chainHelmetStack = ItemStack.builder().itemType(ItemTypes.CHAINMAIL_HELMET).build();
        ItemStack chainChestStack = ItemStack.builder().itemType(ItemTypes.CHAINMAIL_CHESTPLATE).build();
        ItemStack chainLegsStack = ItemStack.builder().itemType(ItemTypes.CHAINMAIL_LEGGINGS).build();
        ItemStack chainBootsStack = ItemStack.builder().itemType(ItemTypes.CHAINMAIL_BOOTS).build();
        
        ItemStack ironHelmStack = ItemStack.builder().itemType(ItemTypes.IRON_HELMET).build();
        ItemStack ironChestStack = ItemStack.builder().itemType(ItemTypes.IRON_CHESTPLATE).build();
        ItemStack ironLegsStack = ItemStack.builder().itemType(ItemTypes.IRON_LEGGINGS).build();
        ItemStack ironBootsStack = ItemStack.builder().itemType(ItemTypes.IRON_BOOTS).build();
        
        ItemStack diamondHelmStack = ItemStack.builder().itemType(ItemTypes.DIAMOND_HELMET).build();
        ItemStack diamondChestStack = ItemStack.builder().itemType(ItemTypes.DIAMOND_CHESTPLATE).build();
        ItemStack diamondLegsStack = ItemStack.builder().itemType(ItemTypes.DIAMOND_LEGGINGS).build();
        ItemStack diamondBootsStack = ItemStack.builder().itemType(ItemTypes.DIAMOND_BOOTS).build();

        List<Enchantment> armorEnchantments = Arrays.asList(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(1).build(), Enchantment.builder().type(EnchantmentTypes.PROTECTION).level(1).build());

        chainHelmetStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        chainChestStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        chainLegsStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        chainBootsStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);

        ironHelmStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        ironChestStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        ironLegsStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        ironBootsStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);

        diamondHelmStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        diamondChestStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        diamondLegsStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        diamondBootsStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);

        chainHelmetStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "Basic Helmet"));
        chainChestStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "Basic Chestplate"));
        chainLegsStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "Basic Leggings"));
        chainBootsStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "Basic Boots"));

        ironHelmStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Reinforced Helmet"));
        ironChestStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Reinforced Chestplate"));
        ironLegsStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Reinforced Leggings"));
        ironBootsStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Reinforced Boots"));

        diamondHelmStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Legendary Helmet"));
        diamondChestStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Legendary Chestplate"));
        diamondLegsStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Legendary Leggings"));
        diamondBootsStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Legendary Boots"));

        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.BRICK).quantity(32).build().createSnapshot(), chainHelmetStack.createSnapshot(), 1));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(8).build().createSnapshot(), chainChestStack.createSnapshot(), 3));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.BRICK).quantity(64).build().createSnapshot(), chainLegsStack.createSnapshot(), 5));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.BRICK).quantity(32).build().createSnapshot(), chainBootsStack.createSnapshot(), 7));

        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(8).build().createSnapshot(), ironHelmStack.createSnapshot(), 10));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.EMERALD).quantity(2).build().createSnapshot(), ironChestStack.createSnapshot(), 12));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(16).build().createSnapshot(), ironLegsStack.createSnapshot(), 14));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.IRON_INGOT).quantity(8).build().createSnapshot(), ironBootsStack.createSnapshot(), 16));

        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.EMERALD).quantity(2).build().createSnapshot(), diamondHelmStack.createSnapshot(), 19));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.DIAMOND).quantity(4).build().createSnapshot(), diamondChestStack.createSnapshot(), 21));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.EMERALD).quantity(6).build().createSnapshot(), diamondLegsStack.createSnapshot(), 23));
        recipes.add(new ItemShopRecipe(ItemStack.builder().itemType(ItemTypes.EMERALD).quantity(2).build().createSnapshot(), diamondBootsStack.createSnapshot(), 25));

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Armors and Tools");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 3);
    }

}
