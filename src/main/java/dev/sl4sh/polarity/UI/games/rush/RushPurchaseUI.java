package dev.sl4sh.polarity.UI.games.rush;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.UniqueUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.economy.ItemShopRecipe;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class RushPurchaseUI extends UniqueUI {

    protected final RushShopSelectionUI selectionUI;

    protected final List<ItemShopRecipe> recipes = new ArrayList<>();

    public RushPurchaseUI(@Nonnull Player viewer, RushShopSelectionUI selectionUI) {
        super(viewer);
        this.selectionUI = selectionUI;
    }

    @Override
    protected void setupLayout(Inventory newUI) {

        for(Inventory subInv : newUI.slots()){

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            if(getIndexedRecipe(slotIndex).isPresent()){

                ItemShopRecipe recipe = getIndexedRecipe(slotIndex).get();

                if(recipe.isValidRecipe()){

                    ItemStack stack = ItemStack.builder().fromSnapshot(recipe.getTargetItem()).build();
                    stack.offer(new UIStackData(StackTypes.SHOP_STACK, -1, -1));

                    TextColor color = TextColors.DARK_PURPLE;

                    if (recipe.getPrice().getType().equals(ItemTypes.BRICK)) { color = TextColors.DARK_PURPLE; }
                    else if (recipe.getPrice().getType().equals(ItemTypes.IRON_INGOT)) { color = TextColors.GRAY; }
                    else if (recipe.getPrice().getType().equals(ItemTypes.GOLD_INGOT)) { color = TextColors.GOLD; }
                    else if (recipe.getPrice().getType().equals(ItemTypes.EMERALD)) { color = TextColors.GREEN; }
                    else if (recipe.getPrice().getType().equals(ItemTypes.DIAMOND)) { color = TextColors.AQUA; }

                    List<Text> loreList = new ArrayList<>();
                    loreList.add(Text.of(TextColors.AQUA, "Price: ", color, recipe.getPrice().getQuantity(), " ", recipe.getPrice().getTranslation()));
                    stack.offer(Keys.ITEM_LORE, loreList);
                    slot.set(stack);
                    continue;

                }

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

        }

    }

    private Optional<ItemShopRecipe> getIndexedRecipe(int index){

        for(ItemShopRecipe recipe : recipes){

            if(recipe.getIndex() == index){

                return Optional.of(recipe);

            }

        }

        return Optional.empty();

    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event) {

        if(!(event.getSource() instanceof Player)) { return; }

        Player player = (Player)event.getSource();

        if(event.getCursorTransaction().getDefault().get(Polarity.Keys.UIStack.TYPE).isPresent() && event.getCursorTransaction().getDefault().get(Polarity.Keys.UIStack.TYPE).get().equals(StackTypes.SHOP_STACK)){

            Optional<ItemShopRecipe> optRecipe = getRecipeBySnapshot(event.getCursorTransaction().getDefault());

            if(optRecipe.isPresent() && optRecipe.get().isValidRecipe()){

                makeTransaction(player, optRecipe.get());

            }

        }

    }

    public Optional<ItemShopRecipe> getRecipeBySnapshot(ItemStackSnapshot snap){

        ItemStack editedSnap = snap.createStack();
        editedSnap.offer(Keys.ITEM_LORE, new ArrayList<>());

        for(ItemShopRecipe recipe : recipes){

            DataContainer snapDamage = snap.toContainer();
            DataContainer testDamage = recipe.getTargetItem().toContainer();

            int snapVal = (int)snapDamage.get(DataQuery.of("UnsafeDamage")).get();
            int testVal = (int)testDamage.get(DataQuery.of("UnsafeDamage")).get();

            if(ItemStackComparators.TYPE.compare(recipe.getTargetItem().createStack(), editedSnap) == 0 &&
                    ItemStackComparators.PROPERTIES.compare(recipe.getTargetItem().createStack(), editedSnap) == 0 &&
                    snapVal == testVal){

                return Optional.of(recipe);

            }

        }

        return Optional.empty();

    }

    protected void makeTransaction(Player player, ItemShopRecipe recipe){

        if(player.getInventory().contains(recipe.getPrice().createStack())){

            player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(recipe.getPrice().createStack())).poll(recipe.getPrice().getQuantity());
            Utilities.givePlayer(player, recipe.getTargetItem().createStack(), true);
            player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 0.25);

        }
        else{

            player.sendMessage(Text.of(TextColors.RED, "You do not have enough items to purchase this."));
            player.playSound(SoundTypes.BLOCK_NOTE_BASS, player.getPosition(), 0.25);

        }

    }

    @Override
    protected void onClosed(InteractInventoryEvent.Close event) {

        selectionUI.openFor(getTargetViewer().get());

    }

    @Nonnull
    @Override
    public abstract Text getTitle();

    @Nonnull
    @Override
    public abstract InventoryDimension getUIDimensions();
}
