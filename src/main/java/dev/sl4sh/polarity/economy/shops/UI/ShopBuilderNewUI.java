package dev.sl4sh.polarity.economy.shops.UI;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.economy.ShopRecipe;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopBuilderNewUI {

    private String profileName;
    private int height;
    private Inventory Inv;
    private Player playerRef;

    public ShopBuilderNewUI(String profileName, int height) {

        this.profileName = profileName;
        this.height = height;

    }

    public void makeShopBuilderInterface(Player player){

        Inv = Inventory.builder()
                .property("title", new InventoryTitle(Text.of("Custom inventory")))
                .property("inventorydimension", new InventoryDimension(9, height))
                .listener(InteractInventoryEvent.Close.class, this::onInventoryClosed)
                .build(Polarity.getPolarity());

        player.openInventory(Inv, Text.of("Shop!"));
        playerRef = player;

    }

    public void onInventoryClosed(InteractInventoryEvent.Close event) {

        List<ShopRecipe> shopRecipeList = new ArrayList<>();

        boolean any = false;

        for(Inventory slot : Inv.slots()){

            Optional<ItemStack> itemStack = slot.poll();

            if(itemStack.isPresent()){

                shopRecipeList.add(new ShopRecipe(0.0f, itemStack.get().createSnapshot()));
                any = true;

            }
            else{

                shopRecipeList.add(new ShopRecipe(0.0f, ItemStack.builder().itemType(ItemTypes.BARRIER).quantity(1).build().createSnapshot()));

            }

        }

        if(any){

            Polarity.getShopProfiles().addShopProfile(new ShopProfile(shopRecipeList, profileName, height));
            Polarity.getPolarity().writeAllConfig();
            playerRef.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Created your shop profile named ", profileName, TextColors.AQUA, "."));

        }
        else{

            playerRef.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | No items detected. Your shop has not been created"));

        }

    }

}
