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

public class ShopBuilderEditLayoutUI {

    private Inventory Inv;
    private Player playerRef;
    private ShopProfile shopProfile;

    public void makeFromShopProfile(Player player, String profileName){

        if(!Polarity.getShopProfiles().getShopProfileByName(profileName).isPresent()) { player.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | This shop profile does not exist.")); return; }

        shopProfile = Polarity.getShopProfiles().getShopProfileByName(profileName).get();

        Inv = Inventory.builder()
                .property("title", new InventoryTitle(Text.of("Custom inventory")))
                .property("inventorydimension", new InventoryDimension(9, shopProfile.getShopPageHeight()))
                .listener(InteractInventoryEvent.Close.class, this::onInventoryClosed)
                .build(Polarity.getPolarity());

        int it = 0;

        for(ShopRecipe recipe : shopProfile.getShopRecipes()){

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

        List<ShopRecipe> shopRecipeList = new ArrayList<>();

        boolean any = false;

        for(Inventory slot : Inv.slots()){

            Optional<ItemStack> itemStack = slot.poll();

            if(itemStack.isPresent()){

                if(shopProfile.getRecipeBySnapshot(itemStack.get().createSnapshot()).isPresent()){

                    shopRecipeList.add(shopProfile.getRecipeBySnapshot(itemStack.get().createSnapshot()).get());

                }
                else{

                    shopRecipeList.add(new ShopRecipe(0.0f, itemStack.get().createSnapshot()));

                }

                any = true;

            }
            else{

                shopRecipeList.add(new ShopRecipe(0.0f, ItemStack.builder().itemType(ItemTypes.BARRIER).quantity(1).build().createSnapshot()));

            }

        }

        if(any){

            Polarity.getShopProfiles().addShopProfile(new ShopProfile(shopRecipeList, shopProfile.getProfileName(), shopProfile.getShopPageHeight()));
            Polarity.getPolarity().writeAllConfig();
            playerRef.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Successfully edited your shop named ", shopProfile.getProfileName(), TextColors.AQUA, "."));

        }
        else{

            playerRef.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Did not edit your shop."));

        }

    }

}
