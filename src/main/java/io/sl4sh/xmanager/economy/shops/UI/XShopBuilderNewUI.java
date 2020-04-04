package io.sl4sh.xmanager.economy.shops.UI;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XShopProfile;
import io.sl4sh.xmanager.economy.XShopRecipe;
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

public class XShopBuilderNewUI {

    private String profileName;
    private int height;
    private Inventory Inv;
    private Player playerRef;

    public XShopBuilderNewUI(String profileName, int height) {

        this.profileName = profileName;
        this.height = height;

    }

    public void makeShopBuilderInterface(Player player){

        Inv = Inventory.builder()
                .property("title", new InventoryTitle(Text.of("Custom inventory")))
                .property("inventorydimension", new InventoryDimension(9, height))
                .listener(InteractInventoryEvent.Close.class, this::onInventoryClosed)
                .build(XManager.getXManager());

        player.openInventory(Inv, Text.of("Shop!"));
        playerRef = player;

    }

    public void onInventoryClosed(InteractInventoryEvent.Close event) {

        List<XShopRecipe> shopRecipeList = new ArrayList<>();

        boolean any = false;

        for(Inventory slot : Inv.slots()){

            Optional<ItemStack> itemStack = slot.poll();

            if(itemStack.isPresent()){

                shopRecipeList.add(new XShopRecipe(0.0f, itemStack.get().createSnapshot()));
                any = true;

            }
            else{

                shopRecipeList.add(new XShopRecipe(0.0f, ItemStack.builder().itemType(ItemTypes.BARRIER).quantity(1).build().createSnapshot()));

            }

        }

        if(any){

            XManager.getShopProfiles().addShopProfile(new XShopProfile(shopRecipeList, profileName, height));
            XManager.getXManager().writeShopProfiles();
            playerRef.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Created your shop profile named ", profileName, TextColors.AQUA, "."));
            XManager.getXManager().writeShopProfiles();

        }
        else{

            playerRef.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | No items detected. Your shop has not been created"));

        }

    }

}
