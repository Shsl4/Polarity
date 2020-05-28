package dev.sl4sh.polarity.UI.shops.user;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.shops.ShopUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.data.registration.npcdata.NPCData;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.economy.ShopRecipe;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.transactionidentifiers.ShopIdentifier;
import dev.sl4sh.polarity.enums.NPCTypes;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.entity.ICustomNpc;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;

public class MasterUserShopUI extends ShopUI {

    @Nullable
    private UUID ownerID;

    @Nonnull
    public ShopProfile getUserShopProfile() { return profile; }

    @Nullable
    private ManageUserShopUI manageShopUI;

    public void onPurchased(Player buyer){

        if (Utilities.getNPCsAPI().isPresent()) {

            ICustomNpc<?> customNPC = (ICustomNpc<?>) Utilities.getNPCsAPI().get().getIEntity((net.minecraft.entity.Entity)merchant);

            customNPC.getDisplay().setSkinPlayer(buyer.getName());
            customNPC.getDisplay().setName(buyer.getName() + "'s Shop");
            World spongeWorld = buyer.getWorld();

            if(!Utilities.getSpongeWorldToServerWorld(spongeWorld).isPresent()) { return; }

            IWorld npcWorld = Utilities.getNPCsAPI().get().getIWorld(Utilities.getSpongeWorldToServerWorld(spongeWorld).get());

            Entity newEntity = (Entity)npcWorld.createEntityFromNBT(customNPC.getEntityNbt()).getMCEntity();
            newEntity.offer(merchant.get(NPCData.class).orElse(new NPCData()).copy());
            newEntity.offer(Polarity.Keys.NPC.TAGS, Collections.singletonList(buyer.getName()));

            try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {

                frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
                spongeWorld.spawnEntity(newEntity);

            }

            customNPC.despawn();
            ((Entity)customNPC.getMCEntity()).remove();
            merchant = newEntity;

        } else {

            merchant.offer(Keys.SKIN_UNIQUE_ID, buyer.getUniqueId());
            merchant.offer(Keys.DISPLAY_NAME, Text.of(buyer.getName(), "'s Shop"));
            merchant.offer(Polarity.Keys.NPC.TAGS, Collections.singletonList(buyer.getUniqueId().toString()));

        }

        this.setOwner(buyer);
        manageShopUI = new ManageUserShopUI(buyer.getUniqueId(), this, merchant.get(Polarity.Keys.NPC.STORAGE).orElse(new ArrayList<>()));
        this.saveData();

    }

    public void onSold(Player caller){

        if(ownerID == null || !caller.getUniqueId().equals(ownerID)) { return; }

        this.profile = new ShopProfile();

        if(this.manageShopUI != null){

            this.manageShopUI.getStorage().clear();

        }

        merchant.offer(new NPCData());

        if (Utilities.getNPCsAPI().isPresent()) {

            ICustomNpc<?> customNPC = (ICustomNpc<?>) Utilities.getNPCsAPI().get().getIEntity((net.minecraft.entity.Entity)merchant);
            customNPC.despawn();

        }

        Location<World> location = merchant.getLocation();

        merchant.remove();
        this.merchant = null;

        Utilities.delayOneTick(() -> Polarity.getNPCManager().makeUserShopNPC(location));

        caller.closeInventory();

    }

    public void saveData(){

        Polarity.getLogger().info("Saving");

        if(ownerID != null){

            merchant.offer(Polarity.Keys.NPC.TAGS, Collections.singletonList(ownerID.toString()));

        }

        merchant.offer(Polarity.Keys.NPC.SHOP_PROFILE, this.profile);

        List<ItemStackSnapshot> storage = new ArrayList<>();

        if(getManageShopUI() != null){

            for(Inventory slot : getManageShopUI().getStorage().slots()){

                if(slot.peek().isPresent()){

                    storage.add(slot.peek().get().createSnapshot());

                }

            }

        }

        merchant.offer(Polarity.Keys.NPC.STORAGE, storage);

    }

    protected void setOwner(@Nonnull Player owner) {
        this.ownerID = owner.getUniqueId();
    }

    public Optional<Player> getOwner() {

        if(ownerID == null) { return Optional.empty(); }

        return Utilities.getPlayerByUniqueID(ownerID);

    }

    public MasterUserShopUI(@Nonnull Entity merchant) {

        super(merchant.get(Polarity.Keys.NPC.SHOP_PROFILE).get(), merchant);

        if(!merchant.supports(NPCData.class)) { throw new IllegalStateException("Tried to create a MasterUserShopUI on an entity which does not support NPCData."); }

        if(!merchant.get(Polarity.Keys.NPC.TYPE).get().equals(NPCTypes.USERSHOP_NPC)) { throw new IllegalStateException("Tried to create a MasterUserShopUI on an entity which is not a User Shop NPC."); }

        profile = merchant.get(Polarity.Keys.NPC.SHOP_PROFILE).orElse(new ShopProfile());

        try{

            ownerID = UUID.fromString(merchant.get(Polarity.Keys.NPC.TAGS).get().get(0));

        }
        catch(IndexOutOfBoundsException e){

            ownerID = null;

        }

        if(ownerID != null){

            manageShopUI = new ManageUserShopUI(ownerID, this, merchant.get(Polarity.Keys.NPC.STORAGE).orElse(new ArrayList<>()));

        }

    }

    public void forceOpen(Player player){

        super.openFor(player);

    }

    @Override
    public boolean openFor(Player player) {

        if(ownerID == null){

            new BuyUserShopUI(player.getUniqueId(), this).open();

        }
        else if(!player.getUniqueId().equals(ownerID)){

            super.openFor(player);

        }
        else{

            if(manageShopUI == null){

                manageShopUI = new ManageUserShopUI(ownerID, this, new ArrayList<>());

            }

            manageShopUI.open();

        }

        return true;

    }

    @Override
    public void setupLayout(Inventory newUI) {

        for(Inventory subInv : newUI.slots()){

            Slot slot = (Slot)subInv;
            SlotIndex property = slot.getInventoryProperty(SlotIndex.class).get();
            int slotIndex = property.getValue();

            if(getUserShopProfile().getRecipeWithIndex(slotIndex).isPresent()){

                ShopRecipe recipe = getUserShopProfile().getRecipeWithIndex(slotIndex).get();

                List<Text> loreList = new ArrayList<>();
                ItemStack stack = recipe.getTargetItem().createStack();

                if(!recipe.isValidRecipe()){

                    stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
                    stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);

                }
                else{

                    Optional<ItemStack> optStack = getManageShopUI().getStorage().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(recipe.getTargetItem().createStack())).peek(recipe.getTargetItem().getQuantity());

                    if(optStack.isPresent() && (optStack.get().getQuantity() >= recipe.getTargetItem().getQuantity())){

                        loreList.add(Text.of(TextColors.AQUA, "Price: ", TextColors.GOLD, "$", recipe.getPrice()));
                        stack.offer(new UIStackData(StackTypes.SHOP_STACK, -1, -1));
                        stack.offer(Keys.ITEM_LORE, loreList);

                    }
                    else{

                        stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(TextColors.RED, "Out of stock"), new ArrayList<>(), false);
                        stack.offer(Keys.DYE_COLOR, DyeColors.RED);

                    }

                }

                slot.set(stack);
                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.EMPTY, new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

        }

    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event) {

        if(event.getSource() instanceof Player && !((Player) event.getSource()).getUniqueId().equals(ownerID)){

            super.onPrimary(event);

        }
        else{

            if(event.getCursorTransaction().getDefault().get(Polarity.Keys.UIStack.TYPE).isPresent() && event.getCursorTransaction().getDefault().get(Polarity.Keys.UIStack.TYPE).get().equals(StackTypes.SHOP_STACK)){

                if(!getOwner().isPresent()) { return; }

                getOwner().get().sendMessage(Text.of(TextColors.RED, "You can't purchase your own items."));
                getOwner().get().playSound(SoundTypes.BLOCK_NOTE_BASS, getOwner().get().getPosition(), 0.25);

            }

        }

    }

    @Nullable
    @Override
    protected TransactionResult makeTransaction(Player player, ShopRecipe recipe) {

        Optional<ItemStack> optStack = getManageShopUI().getStorage().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(recipe.getTargetItem().createStack())).peek(recipe.getTargetItem().getQuantity());

        if(optStack.isPresent() && (optStack.get().getQuantity() >= recipe.getTargetItem().getQuantity())){

            Optional<PolarityEconomyService> optEconomyService = Polarity.getEconomyService();

            if(optEconomyService.isPresent()){

                PolarityEconomyService economyService = optEconomyService.get();

                Optional<UniqueAccount> optPlayerAccount = economyService.getOrCreateAccount(player.getUniqueId());

                if(!optPlayerAccount.isPresent() ||
                        ownerID == null ||
                        !economyService.getOrCreateAccount(ownerID).isPresent()) { player.sendMessage(Text.of(TextColors.RED, "Unable to access accounts. Please try again.")); return null; }

                UniqueAccount playerAccount = optPlayerAccount.get();
                UniqueAccount ownerAccount = economyService.getOrCreateAccount(ownerID).get();

                PolarityCurrency dollarCurrency = new PolarityCurrency();

                if(!player.getInventory().canFit(recipe.getTargetItem().createStack())) { player.sendMessage(Text.of(TextColors.RED, "You do not have space in your inventory.")); return null; }

                TransactionResult result = playerAccount.transfer(ownerAccount, dollarCurrency, BigDecimal.valueOf(recipe.getPrice()), Cause.of(EventContext.empty(), new ShopIdentifier()), new HashSet<>());

                switch(result.getResult()){

                    case ACCOUNT_NO_FUNDS:

                        player.sendMessage(Text.of(TextColors.RED, "You do not have enough money to buy that."));
                        break;

                    case SUCCESS:

                        ItemStack stack = recipe.getTargetItem().createStack();

                        Text format = Text.of(TextColors.YELLOW, stack.get(Keys.DISPLAY_NAME).orElse(Text.of(recipe.getTargetItem().getTranslation())));
                        int quantity = stack.getQuantity();

                        player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 0.25);
                        player.sendMessage(Text.of(TextColors.AQUA, "You just bought ", quantity, " ", format, " for ", dollarCurrency.format(BigDecimal.valueOf(recipe.getPrice()), 2), TextColors.AQUA, "."));

                        getOwner().ifPresent(owner -> owner.sendMessage(Text.of(TextColors.AQUA, player.getName(), " just bought ", quantity, " ", format, " for ", dollarCurrency.format(BigDecimal.valueOf(recipe.getPrice()), 2), TextColors.AQUA, " at your shop.")));

                        player.getInventory().offer(stack);

                        getManageShopUI().getStorage().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(recipe.getTargetItem().createStack())).poll(recipe.getTargetItem().getQuantity());
                        this.saveData();

                        break;

                    case FAILED:

                        player.sendMessage(Text.of(TextColors.RED, "Transaction failed."));
                        break;

                }

                return result;

            }
            else{

                player.sendMessage(Text.of(TextColors.RED, "Transaction failed."));

            }

        }

        return null;

    }

    @Nonnull
    public Text getTitle() {

        if(Utilities.getNPCsAPI().isPresent()){

            return Text.of(((ICustomNpc<?>)Utilities.getNPCsAPI().get().getIEntity((net.minecraft.entity.Entity)merchant)).getName());

        }

        return merchant.get(Keys.DISPLAY_NAME).isPresent() ? merchant.get(Keys.DISPLAY_NAME).get() : Text.of("Shop");

    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 5);
    }

    @Nullable
    public ManageUserShopUI getManageShopUI() {
        return manageShopUI;
    }
}
