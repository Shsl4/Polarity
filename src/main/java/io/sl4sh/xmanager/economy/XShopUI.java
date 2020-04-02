package io.sl4sh.xmanager.economy;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import io.sl4sh.xmanager.economy.transactionidentifiers.XShopIdentifier;
import noppes.npcs.api.entity.ICustomNpc;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.property.SlotSide;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class XShopUI {

    private Inventory Inv;
    private Player playerRef;
    private XShopProfile shopProfile;

    private void onItemClick(ClickInventoryEvent event){

        event.setCancelled(true);

    }

    public void makeFromShopProfile(Player player, XShopProfile profile, ICustomNpc NPC){

        shopProfile = profile;

        Inv = Inventory.builder()
                .property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NPC.getName(), "'s Shop")))
                .property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, shopProfile.getShopPageHeight()))
                .listener(ClickInventoryEvent.Primary.class, this::onItemLeftClick)
                .listener(ClickInventoryEvent.Secondary.class, this::onItemClick)
                .listener(ClickInventoryEvent.Drag.class, this::onItemClick)
                .listener(ClickInventoryEvent.Middle.class, this::onItemClick)
                .listener(ClickInventoryEvent.Double.class, this::onItemClick)
                .listener(ClickInventoryEvent.Drop.class, this::onItemClick)
                .build(XManager.getXManager());

        playerRef = player;
        int it = 0;

        for(XShopRecipe recipe : shopProfile.getShopRecipes()){

            int slotIt = 0;

            for(Inventory slot : Inv.slots()){

                if(slotIt == it){

                    ItemStack targetStack = recipe.getTargetItem().createStack();
                    List<Text> loreList = new ArrayList<>();

                    if(!recipe.isValidRecipe()){

                        targetStack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BLACK).quantity(1).build();
                        targetStack.offer(Keys.DISPLAY_NAME, Text.of());

                    }
                    else{

                        loreList.add(Text.of(TextColors.AQUA, "Price: ", TextColors.GOLD, "$", recipe.getPrice()));

                    }

                    targetStack.offer(Keys.ITEM_LORE, loreList);
                    slot.set(targetStack);
                    break;

                }

                slotIt++;

            }

            it++;

        }

        playerRef.openInventory(Inv, Text.of(NPC.getName(), "'s Shop"));

    }


    public void onItemLeftClick(ClickInventoryEvent.Primary event){

        if(event.getCursorTransaction().getDefault().get(Keys.ITEM_LORE).isPresent()){

            Optional<XShopRecipe> optRecipe = shopProfile.getRecipeBySnapshot(event.getCursorTransaction().getDefault());

               if(optRecipe.isPresent()){

                   if(event.getCursorTransaction().getDefault().get(Keys.ITEM_LORE).get().get(0).toPlain()
                           .equals(Text.of(TextColors.AQUA, "Price: ", TextColors.GOLD, "$", optRecipe.get().getPrice()).toPlain())){

                       if(optRecipe.get().isValidRecipe()){

                           makeTransaction(playerRef, optRecipe.get());

                       }

                   }



               }

        }

        event.setCancelled(true);

    }

    public static void makeTransaction(Player player, XShopRecipe recipe){

        Optional<XEconomyService> optEconomyService = XManager.getXEconomyService();

        if(optEconomyService.isPresent()){

            XEconomyService economyService = optEconomyService.get();

            Optional<UniqueAccount> optPlayerAccount = economyService.getOrCreateAccount(player.getUniqueId());

            if(!optPlayerAccount.isPresent()) { player.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access your account. Please try again.")); return; }

            UniqueAccount playerAccount = optPlayerAccount.get();

            XDollar dollarCurrency = new XDollar();

            if(!player.getInventory().canFit(recipe.getTargetItem().createStack())) { player.sendMessage(Text.of(TextColors.RED, "[Economy] | You do not have space in your inventory.")); return; }

            TransactionResult result = playerAccount.withdraw(dollarCurrency, BigDecimal.valueOf(recipe.getPrice()), Cause.of(EventContext.empty(), new XShopIdentifier()), new HashSet<>());

            switch(result.getResult()){

                case ACCOUNT_NO_FUNDS:

                    player.sendMessage(Text.of(TextColors.RED, "[Economy] | You do not have enough money to buy that."));
                    break;

                case SUCCESS:

                    String format = recipe.getTargetItem().getTranslation().get(player.getLocale());

                    if (!format.endsWith("s") && recipe.getTargetItem().getQuantity() > 1) { format = format + "s"; }

                    player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 0.75);
                    player.sendMessage(Text.of(TextColors.AQUA, "[Economy] | You just bought ", recipe.getTargetItem().getQuantity(), " ", format, " for ", dollarCurrency.format(BigDecimal.valueOf(recipe.getPrice()), 2), TextColors.AQUA, "."));
                    player.getInventory().offer(recipe.getTargetItem().createStack());

                    break;

                case FAILED:

                    player.sendMessage(Text.of(TextColors.RED, "[Economy] | Transaction failed."));
                    break;

            }


        }
        else{

            player.sendMessage(Text.of(TextColors.RED, "[Economy] | Failed to get economy service. It may have been disabled by your administrator."));

        }

    }

    public void onInventoryClosed(InteractInventoryEvent.Close event) {

    }

}
