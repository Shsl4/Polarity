package dev.sl4sh.polarity.economy.shops.UI;

import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.transactionidentifiers.SellIdentifier;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.DataRegistration;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SellConfirmationUI {

    private Inventory Inv;
    private Player playerRef;
    private List<ItemStack> stacks;
    @Nonnull
    private List<ItemStack> invalidStacks = new ArrayList<>();
    private float totalSellPrice = 0.0f;
    private boolean confirmed = false;

    private void onItemClick(ClickInventoryEvent event){

        ItemStack eventStack = event.getCursorTransaction().getDefault().createStack();

        if(!isPlayerStack(eventStack)) {

            event.setCancelled(true);

            Optional<DyeColor> buttonColor = eventStack.get(Keys.DYE_COLOR);

            if (buttonColor.isPresent()) {

                if(buttonColor.get().equals(DyeColors.LIME)){

                    Optional<PolarityEconomyService> optService = Polarity.getEconomyService();

                    if(optService.isPresent()){

                        PolarityEconomyService service = optService.get();

                        Optional<UniqueAccount> playerAccount = service.getOrCreateAccount(playerRef.getUniqueId());

                        if(playerAccount.isPresent()){

                            playerAccount.get().deposit(new PolarityCurrency(), BigDecimal.valueOf(totalSellPrice), Cause.of(EventContext.empty(), new SellIdentifier()));
                            confirmed = true;

                            Task.builder().delayTicks(1L).execute(() -> {

                                playerRef.closeInventory();

                            }).submit(Polarity.getPolarity());


                        }

                    }

                }

                if(buttonColor.get().equals(DyeColors.WHITE) || buttonColor.get().equals(DyeColors.RED)){

                    Task.builder().delayTicks(1L).execute(() -> {

                        playerRef.closeInventory();

                    }).submit(Polarity.getPolarity());

                }

            }

        }

    }

    public void makeForPlayer(Player player, List<ItemStack> stacks){

        this.Inv = Inventory.builder()
                .property("title", new InventoryTitle(Text.of("Sell Items")))
                .property("inventorydimension", new InventoryDimension(9, 5))
                .listener(ClickInventoryEvent.Primary.class, this::onItemClick)
                .listener(ClickInventoryEvent.Secondary.class, this::onItemClick)
                .listener(InteractInventoryEvent.Close.class, this::onInventoryClosed)
                .listener(ClickInventoryEvent.Drag.class, this::onItemClick)
                .listener(ClickInventoryEvent.Middle.class, this::onItemClick)
                .listener(ClickInventoryEvent.Double.class, this::onItemClick)
                .listener(ClickInventoryEvent.Drop.class, this::onItemClick)
                .build(Polarity.getPolarity());

        this.stacks = stacks;
        this.playerRef = player;
        int validItemCount = 0;

        List<Text> unsalableItemNames = new ArrayList<>();

        for(ItemStack stack : stacks){

            float sellPrice = Polarity.getShopProfiles().getSellPrice(stack);
            
            if(sellPrice == 0.0f){

                if(!unsalableItemNames.contains(Text.of(TextColors.YELLOW, stack.getType().getTranslation().get(playerRef.getLocale())))){

                    unsalableItemNames.add(Text.of(TextColors.YELLOW, stack.getType().getTranslation().get(playerRef.getLocale())));
                    invalidStacks.add(stack);

                }

                continue;
                
            }

            validItemCount += stack.getQuantity();
            this.totalSellPrice += (sellPrice * stack.getQuantity());
            
        }

       if(this.totalSellPrice == 0.0f){

           createNoSalableItemsInventory();

       }
       else if(unsalableItemNames.size() > 0){

           createPartiallySalableInventory(validItemCount, this.totalSellPrice, unsalableItemNames);

       }
       else{

           createSalableInventory(validItemCount, this.totalSellPrice);

       }

        player.openInventory(this.Inv, Text.of("Confirmation"));

    }

    private void createSalableInventory(int validItemCount, float price){

        for(Inventory subInv : Inv.slots()){

            Slot slot = (Slot)subInv;

            SlotIndex idx = slot.getInventoryProperty(SlotIndex.class).get();
            int val = idx.getValue();

            if(val == 13){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.NETHER_STAR).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "You are about to sell ", validItemCount, " items for ", new PolarityCurrency().format(BigDecimal.valueOf(price), 2)));
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }

            if(val == 30){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.LIME).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Confirm"));
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }


            if(val == 32){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.RED).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Cancel"));
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }

            ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BLACK).quantity(1).build();
            stack.offer(Keys.DISPLAY_NAME, Text.of());
            stack.offer(new UIStackData());
            stack.offer(DataRegistration.Keys.UI_STACK, true);
            slot.set(stack);

        }

    }

    private void createPartiallySalableInventory(int validItemCount, float price, List<Text> unsalableItemNames){

        for(Inventory subInv : Inv.slots()){

            Slot slot = (Slot)subInv;

            SlotIndex idx = slot.getInventoryProperty(SlotIndex.class).get();
            int val = idx.getValue();

            if(val == 13){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.NETHER_STAR).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "You are about to sell ", validItemCount, " items for ", new PolarityCurrency().format(BigDecimal.valueOf(price), 2)));
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }

            if(val == 29){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.LIME).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Confirm"));
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }

            if(val == 31){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.SKULL).add(Keys.SKULL_TYPE, SkullTypes.WITHER_SKELETON).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, "Warning!"));
                List<Text> loreList = new ArrayList<>();
                loreList.add(Text.of(TextColors.YELLOW, "Some items you offered are not salable. They will be put back in your inventory :"));
                loreList.addAll(unsalableItemNames);
                stack.offer(Keys.ITEM_LORE, loreList);
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }


            if(val == 33){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.RED).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Cancel"));
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }

            ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BLACK).quantity(1).build();
            stack.offer(Keys.DISPLAY_NAME, Text.of());
            stack.offer(new UIStackData());
            stack.offer(DataRegistration.Keys.UI_STACK, true);
            slot.set(stack);

        }

    }

    private void createNoSalableItemsInventory(){

        for(Inventory subInv : Inv.slots()){

            Slot slot = (Slot)subInv;

            SlotIndex idx = slot.getInventoryProperty(SlotIndex.class).get();
            int val = idx.getValue();

            if(val == 13){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.NETHER_STAR).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "None of the items you submitted can be sold."));
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }

            if(val == 31){

                ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.WHITE).quantity(1).build();
                stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Dismiss"));
                stack.offer(new UIStackData());
                stack.offer(DataRegistration.Keys.UI_STACK, true);
                slot.set(stack);
                continue;

            }

            ItemStack stack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BLACK).quantity(1).build();
            stack.offer(Keys.DISPLAY_NAME, Text.of());
            stack.offer(new UIStackData());
            stack.offer(DataRegistration.Keys.UI_STACK, true);
            slot.set(stack);

        }

    }

    private void onInventoryClosed(InteractInventoryEvent.Close event) {

        if(confirmed) {

            // Give the player its unsalable items back if the transaction has been confirmed
            Task.builder().delayTicks(1L).execute(() -> {

                for(ItemStack stack : invalidStacks){

                    Utilities.givePlayer(this.playerRef, stack);

                }

            }).submit(Polarity.getPolarity());

            return;

        }

        // Give the player all its items back if the transaction has not been confirmed
        Task.builder().delayTicks(1L).execute(() -> {

            for(ItemStack stack : stacks){

                Utilities.givePlayer(playerRef, stack);

            }

        }).submit(Polarity.getPolarity());

    }

    public static boolean isPlayerStack(ItemStack stack){

        return !stack.get(UIStackData.class).isPresent();

    }

}
