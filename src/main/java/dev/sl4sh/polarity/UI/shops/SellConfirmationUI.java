package dev.sl4sh.polarity.UI.shops;

import dev.sl4sh.polarity.UI.UniqueUI;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.transactionidentifiers.SellIdentifier;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.key.Keys;
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
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SellConfirmationUI extends UniqueUI {

    @Nonnull
    private final List<ItemStack> invalidStacks = new ArrayList<>();
    private final List<ItemStack> stacks;
    private float totalSellPrice = 0.0f;
    private boolean confirmed = false;

    public SellConfirmationUI(Player player, List<ItemStack> stacks){

        super(player);
        this.stacks = stacks;

    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event){

        ItemStack eventStack = event.getCursorTransaction().getDefault().createStack();

        if(Utilities.isUIStack(eventStack)) {

            if(!eventStack.get(Polarity.Keys.UIStack.BUTTON_ID).isPresent()) { return; }

            int id = eventStack.get(Polarity.Keys.UIStack.BUTTON_ID).get();

            if(id == 0) {

                Optional<PolarityEconomyService> optService = Polarity.getEconomyService();

                if(optService.isPresent()){

                    PolarityEconomyService service = optService.get();

                    Optional<UniqueAccount> playerAccount = service.getOrCreateAccount(getTargetViewer().get().getUniqueId());

                    if(playerAccount.isPresent()){

                        playerAccount.get().deposit(new PolarityCurrency(), BigDecimal.valueOf(totalSellPrice), Cause.of(EventContext.empty(), new SellIdentifier()));

                        confirmed = true;

                        Utilities.delayOneTick(() -> { getTargetViewer().get().closeInventory(); });

                    }

                }

            }
            else if (id == 1) {

                Utilities.delayOneTick(() -> { getTargetViewer().get().closeInventory(); });

            }

        }

    }

    @Override
    public void setupLayout(Inventory newUI){

        int validItemCount = 0;

        for(ItemStack stack : stacks){

            float sellPrice = Polarity.getShopProfiles().getSellPrice(stack);
            
            if(sellPrice == 0.0f){

                invalidStacks.add(stack);
                continue;
                
            }

            validItemCount += stack.getQuantity();
            this.totalSellPrice += (sellPrice * stack.getQuantity());
            
        }

       if(this.totalSellPrice == 0.0f){

           createNoSalableItemsInventory();

       }
       else if(invalidStacks.size() > 0){

           createPartiallySalableInventory(validItemCount, this.totalSellPrice);

       }
       else{

           createSalableInventory(validItemCount, this.totalSellPrice);

       }

    }

    private void createSalableInventory(int validItemCount, float price){

        for(Inventory subInv : getUI().slots()){

            Slot slot = (Slot)subInv;

            SlotIndex idx = slot.getInventoryProperty(SlotIndex.class).get();
            int val = idx.getValue();

            if(val == 13){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.NETHER_STAR, 1, Text.of(TextColors.AQUA, "You are about to sell ", validItemCount, " items for ", new PolarityCurrency().format(BigDecimal.valueOf(price), 2)), new ArrayList<>(), true);
                slot.set(stack);
                continue;

            }

            if(val == 30){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.GREEN, "Confirm"), new ArrayList<>(), false);
                stack.offer(Keys.DYE_COLOR, DyeColors.LIME);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 0);
                slot.set(stack);
                continue;

            }


            if(val == 32){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.RED, "Cancel"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Keys.DYE_COLOR, DyeColors.RED);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 1);
                slot.set(stack);
                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

        }

    }

    private void createPartiallySalableInventory(int validItemCount, float price){

        for(Inventory subInv : getUI().slots()){

            Slot slot = (Slot)subInv;

            SlotIndex idx = slot.getInventoryProperty(SlotIndex.class).get();
            int val = idx.getValue();

            if(val == 13){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.NETHER_STAR, 1, Text.of(TextColors.AQUA, "You are about to sell ", validItemCount, " items for ", new PolarityCurrency().format(BigDecimal.valueOf(price), 2)), new ArrayList<>(), true);
                slot.set(stack);
                continue;

            }

            if(val == 29){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.GREEN, "Confirm"), new ArrayList<>(), false);
                stack.offer(Keys.DYE_COLOR, DyeColors.LIME);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 0);
                slot.set(stack);
                continue;

            }

            if(val == 31){

                List<Text> loreList = new ArrayList<>();
                loreList.add(Text.of(TextColors.YELLOW, "Some items you offered are not salable. They will be put back in your inventory :"));

                int it = 0;

                for(ItemStack stack : invalidStacks){

                    if(it < 4){

                        loreList.add(Text.of(TextColors.YELLOW, stack.get(Keys.DISPLAY_NAME).orElse(Text.of(stack.getTranslation()))));

                    }
                    else if(it == 4){

                        loreList.add(Text.of(TextColors.YELLOW, "and ", (invalidStacks.size()) - 4, " more."));

                    }

                    it++;

                }

                ItemStack stack = Utilities.makeUIStack(ItemTypes.SKULL, 1, Text.of(TextColors.YELLOW, "Warning!"), loreList, false);
                stack.offer(Keys.SKULL_TYPE, SkullTypes.WITHER_SKELETON);
                slot.set(stack);
                continue;

            }

            if(val == 33){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.RED, "Cancel"), new ArrayList<>(), false);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Keys.DYE_COLOR, DyeColors.RED);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 1);
                slot.set(stack);
                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

        }

    }

    private void createNoSalableItemsInventory(){

        for(Inventory subInv : getUI().slots()){

            Slot slot = (Slot)subInv;

            SlotIndex idx = slot.getInventoryProperty(SlotIndex.class).get();
            int val = idx.getValue();

            if(val == 13){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.NETHER_STAR, 1, Text.of(TextColors.RED, "None of the items you submitted can be sold."), new ArrayList<>(), true);
                slot.set(stack);
                continue;

            }

            if(val == 31){

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.GREEN, "Dismiss"), new ArrayList<>(), false);
                stack.offer(Keys.DYE_COLOR, DyeColors.WHITE);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.NAVIGATION_BUTTON);
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, 1);
                slot.set(stack);
                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);


        }

    }

    @Override
    protected void onClosed(InteractInventoryEvent.Close event) {

        List<ItemStack> select = confirmed ? invalidStacks : stacks;
        Utilities.delayOneTick(() -> { for(ItemStack stack : select){ Utilities.givePlayer(this.getTargetViewer().get(), stack, true); } });

    }

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of("Sell confirmation");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 5);
    }
}
