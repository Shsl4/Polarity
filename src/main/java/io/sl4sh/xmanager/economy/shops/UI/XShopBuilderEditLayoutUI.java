package io.sl4sh.xmanager.economy.shops.UI;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XShopProfile;
import io.sl4sh.xmanager.economy.XShopRecipe;
import org.spongepowered.api.command.args.GenericArguments;
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

public class XShopBuilderEditLayoutUI {

    private Inventory Inv;
    private Player playerRef;
    private XShopProfile shopProfile;

    public void makeFromShopProfile(Player player, String profileName){

        if(!XManager.getShopProfiles().getShopProfileByName(profileName).isPresent()) { player.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | This shop profile does not exist.")); return; }

        shopProfile = XManager.getShopProfiles().getShopProfileByName(profileName).get();

        Inv = Inventory.builder()
                .property("title", new InventoryTitle(Text.of("Custom inventory")))
                .property("inventorydimension", new InventoryDimension(9, shopProfile.getShopPageHeight()))
                .listener(InteractInventoryEvent.Close.class, this::onInventoryClosed)
                .build(XManager.getXManager());

        int it = 0;

        for(XShopRecipe recipe : shopProfile.getShopRecipes()){

            int slotIt = 0;

            for(Inventory slot : Inv.slots()){

                if(slotIt == it){

                    slot.set(recipe.getTargetItem().createStack());
                    break;

                }

                slotIt++;

            }

            it++;

        }

        player.openInventory(Inv, Text.of("Shop Editor"));
        playerRef = player;

    }

    public void onInventoryClosed(InteractInventoryEvent.Close event) {

        List<XShopRecipe> shopRecipeList = new ArrayList<>();

        boolean any = false;

        for(Inventory slot : Inv.slots()){

            Optional<ItemStack> itemStack = slot.poll();

            if(itemStack.isPresent()){

                if(shopProfile.getRecipeBySnapshot(itemStack.get().createSnapshot()).isPresent()){

                    shopRecipeList.add(shopProfile.getRecipeBySnapshot(itemStack.get().createSnapshot()).get());

                }
                else{

                    shopRecipeList.add(new XShopRecipe(0.0f, itemStack.get().createSnapshot()));

                }

                any = true;

            }
            else{

                shopRecipeList.add(new XShopRecipe(0.0f, ItemStack.builder().itemType(ItemTypes.BARRIER).quantity(1).build().createSnapshot()));

            }

        }

        if(any){

            XManager.getShopProfiles().addShopProfile(new XShopProfile(shopRecipeList, shopProfile.getProfileName(), shopProfile.getShopPageHeight()));
            XManager.getXManager().writeShopProfiles();
            playerRef.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Successfully edited your shop named ", shopProfile.getProfileName(), TextColors.AQUA, "."));
            XManager.getXManager().writeShopProfiles();

        }
        else{

            playerRef.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Did not edit your shop."));

        }

    }

}
