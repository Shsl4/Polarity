package io.sl4sh.xmanager.economy.shops.UI;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XShopProfile;
import io.sl4sh.xmanager.economy.XShopRecipe;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class XShopBuilderEditPricesUI {

    private Inventory Inv;
    private Player playerRef;
    private XShopProfile shopProfile;
    private float step;

    private void onItemClick(ClickInventoryEvent event){

        event.setCancelled(true);

    }

    public void makeFromShopProfile(Player player, String profileName, double step){

        if(!XManager.getShopProfiles().getShopProfileByName(profileName).isPresent()) { player.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | This shop profile does not exist.")); return; }

        shopProfile = XManager.getShopProfiles().getShopProfileByName(profileName).get();

        Inv = Inventory.builder()
                .property("title", new InventoryTitle(Text.of("Custom inventory")))
                .property("inventorydimension", new InventoryDimension(9, shopProfile.getShopPageHeight()))
                .listener(ClickInventoryEvent.Primary.class, this::onItemLeftClick)
                .listener(ClickInventoryEvent.Secondary.class, this::onItemRightClick)
                .listener(InteractInventoryEvent.Close.class, this::onInventoryClosed)
                .listener(ClickInventoryEvent.Drag.class, this::onItemClick)
                .listener(ClickInventoryEvent.Middle.class, this::onItemClick)
                .listener(ClickInventoryEvent.Double.class, this::onItemClick)
                .listener(ClickInventoryEvent.Drop.class, this::onItemClick)
                .build(XManager.getXManager());


        playerRef = player;
        this.step = (float)step;

        int it = 0;

        for(XShopRecipe recipe : shopProfile.getShopRecipes()){

            int slotIt = 0;

            for(Inventory slot : Inv.slots()){

                if(slotIt == it){

                    ItemStack targetStack = recipe.getTargetItem().createStack();

                    if(!targetStack.getType().equals(ItemTypes.BARRIER)){

                        List<Text> loreList = new ArrayList<>();
                        loreList.add(Text.of(TextColors.AQUA, "Price: ", TextColors.GOLD, "$", recipe.getPrice()));
                        targetStack.offer(Keys.ITEM_LORE, loreList);

                    }

                    slot.set(targetStack);
                    break;

                }

                slotIt++;

            }

            it++;

        }

        playerRef.openInventory(Inv, Text.of("Shop editor"));

    }

    public void onItemLeftClick(ClickInventoryEvent.Primary event){

        for(SlotTransaction transaction : event.getTransactions()){

            transaction.getSlot().clear();
            int it = 0;

            for(Inventory slot : Inv.slots()){

                ItemStack targetStack = transaction.getOriginal().createStack();

                if(slot.contains(transaction.getFinal().createStack())){

                    if(!targetStack.getType().equals(ItemTypes.BARRIER) && !targetStack.getType().equals(ItemTypes.AIR)){

                        shopProfile.getShopRecipes().get(it).setPrice(shopProfile.getShopRecipes().get(it).getPrice() + step);
                        playerRef.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, playerRef.getPosition(),.75);

                        final int index = it;

                        Task.builder().delayTicks(1L).execute(() -> {
                            List<Text> loreList = new ArrayList<>();
                            loreList.add(Text.of(TextColors.AQUA, "Price: ", TextColors.GOLD, "$", shopProfile.getShopRecipes().get(index).getPrice()));
                            targetStack.offer(Keys.ITEM_LORE, loreList);
                            slot.set(targetStack);
                        }).submit(XManager.getXManager());

                    }

                }

                it++;

            }

        }

        event.setCancelled(true);

    }


    public void onItemRightClick(ClickInventoryEvent.Secondary event){

        /*System.out.println("Original: " + event.getCursorTransaction().getOriginal().toString());
        System.out.println("Final: " + event.getCursorTransaction().getFinal().toString());
        System.out.println("Default: " + event.getCursorTransaction().getDefault().toString());
        System.out.println("Custom: " + (event.getCursorTransaction().getCustom().isPresent() ? event.getCursorTransaction().getCustom().toString() : null));*/

        for(SlotTransaction transaction : event.getTransactions()){

            int it = 0;

            for(Inventory slot : Inv.slots()){

                ItemStack targetStack = transaction.getOriginal().createStack();

                if(slot.contains(transaction.getFinal().createStack())){

                    if(!targetStack.getType().equals(ItemTypes.BARRIER) && !targetStack.getType().equals(ItemTypes.AIR)){

                        shopProfile.getShopRecipes().get(it).setPrice(shopProfile.getShopRecipes().get(it).getPrice() - step);
                        playerRef.playSound(SoundTypes.BLOCK_NOTE_BASS, playerRef.getPosition(),.75);

                        final int index = it;

                        Task.builder().delayTicks(1L).execute(() -> {
                            List<Text> loreList = new ArrayList<>();
                            loreList.add(Text.of(TextColors.AQUA, "Price: ", TextColors.GOLD, "$", shopProfile.getShopRecipes().get(index).getPrice()));
                            targetStack.offer(Keys.ITEM_LORE, loreList);
                            slot.set(targetStack);
                        }).submit(XManager.getXManager());

                    }

                }

                it++;

            }

        }

        event.setCancelled(true);

    }

    public void onInventoryClosed(InteractInventoryEvent.Close event) {

        XManager.getShopProfiles().addShopProfile(shopProfile);
        XManager.getXManager().writeShopProfiles();
        playerRef.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Successfully edited your shop named ", shopProfile.getProfileName(), TextColors.AQUA, "."));

    }

}
