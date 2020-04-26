package dev.sl4sh.polarity;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import dev.sl4sh.polarity.UI.SharedUI;
import dev.sl4sh.polarity.UI.games.GameSelectionUI;
import dev.sl4sh.polarity.chat.GeneralChannel;
import dev.sl4sh.polarity.commands.*;
import dev.sl4sh.polarity.commands.economy.PolarityShowBalance;
import dev.sl4sh.polarity.commands.economy.PolarityTransfer;
import dev.sl4sh.polarity.commands.factions.FactionsMainCommand;
import dev.sl4sh.polarity.commands.shopbuilder.ShopBuilderMain;
import dev.sl4sh.polarity.data.registration.PolarityDataRegistration;
import dev.sl4sh.polarity.data.containers.*;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.transactionidentifiers.PlayRewardIdentifier;
import dev.sl4sh.polarity.enums.NPCTypes;
import dev.sl4sh.polarity.enums.PolarityColors;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import dev.sl4sh.polarity.enums.games.ChannelTypes;
import dev.sl4sh.polarity.events.PlayerChangeDimensionEvent;
import dev.sl4sh.polarity.games.*;
import dev.sl4sh.polarity.games.party.GamePartyManager;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import noppes.npcs.api.NpcAPI;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Plugin(id = "polarity", name = "Polarity", description = "Polarity", authors = { "Sl4sh!" })
public class Polarity {

    public static class Keys{

        public static class NPC{

            public static Key<Value<NPCTypes>> TYPE;
            public static Key<ListValue<String>> TAGS;
            public static Key<OptionalValue<SharedUI>> SHARED_UI;
            public static Key<ListValue<ItemStackSnapshot>> STORAGE;
            public static Key<Value<ShopProfile>> SHOP_PROFILE;

        }

        public static class UIStack{

            public static Key<Value<StackTypes>> TYPE;
            public static Key<Value<Integer>> BUTTON_ID;
            public static Key<Value<Integer>> DATA_ID;

        }

        public static Key<Value<ChannelTypes>> PREFERRED_CHANNEL;

    }

    @Listener
    public void onNPCWand(HandInteractEvent event, @First Player player)  {

        event.setCancelled(banForNPCWand(player));

    }

    private boolean banForNPCWand(Player player){

        if(player.hasPermission("*")) { return false; }

        if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getType().getName().equals("customnpcs:npcwand")){

            player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.empty());

            BanService banService = Sponge.getServiceManager().provide(BanService.class).get();

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);

            GameProfile playerProfile = player.getProfile();

            banService.addBan(Ban.builder().type(BanTypes.PROFILE).profile(playerProfile)
                    .startDate(new Date().toInstant())
                    .expirationDate(calendar.getTime().toInstant())
                    .reason(Text.of(TextColors.RED, "Using NPC Wands is strictly prohibited, you've been banned for two days. Please read the rules."))
                    .source(Text.of(TextColors.RED, "Server"))
                    .build());

            player.kick(Text.of(TextColors.RED, "Using NPC Wands is strictly prohibited, you've been banned for two days. Please read the rules."));

            return true;

        }

        return false;

    }

    @Inject
    private Logger logger;

    @Nonnull
    private final HoconConfigurationLoader factionsConfigLoader = HoconConfigurationLoader.builder().setFile(new File("Polarity/data/Factions.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader accountsConfigLoader = HoconConfigurationLoader.builder().setFile(new File("Polarity/data/Accounts.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader worldsInfoConfigLoader = HoconConfigurationLoader.builder().setFile(new File("Polarity/data/WorldsInfo.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader shopProfilesConfigLoader = HoconConfigurationLoader.builder().setFile(new File("Polarity/data/ShopProfiles.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader inventoryBackupsLoader = HoconConfigurationLoader.builder().setFile(new File("Polarity/data/InventoryBackups.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader gamePresetsLoader = HoconConfigurationLoader.builder().setFile(new File("Polarity/data/GamePresets.hocon")).build();

    @Nonnull
    private FactionContainer factionsContainer = new FactionContainer();
    @Nonnull
    private WorldsInfoContainer worldsInfo = new WorldsInfoContainer();
    @Nonnull
    private ShopProfilesContainer shopProfiles = new ShopProfilesContainer();
    @Nonnull
    private PolarityAccountContainer accountContainer = new PolarityAccountContainer();
    @Nonnull
    private InventoryBackupsContainer inventoryBackups = new InventoryBackupsContainer();
    @Nonnull
    private GamePresetContainer gamePresets = new GamePresetContainer();
    @Nullable
    private final PolarityEconomyService economyService;
    @Nonnull
    private final GameManager gameManager = new GameManager();
    @Nonnull
    private final GeneralChannel generalChannel = new GeneralChannel();
    @Nonnull
    private final NPCManager npcManager = new NPCManager();
    @Nonnull
    private final GamePartyManager partyManager = new GamePartyManager();

    @Nonnull
    public static Optional<PolarityEconomyService> getEconomyService(){ return getPolarity().economyService == null ? Optional.empty() : Optional.of(getPolarity().economyService); }
    @Nonnull
    public static ShopProfilesContainer getShopProfiles() { return getPolarity().shopProfiles; }
    @Nonnull
    public static PolarityAccountContainer getAccounts(){ return getPolarity().accountContainer; }
    @Nonnull
    public static FactionContainer getFactions(){ return getPolarity().factionsContainer; }
    @Nonnull
    public static InventoryBackupsContainer getInventoryBackups(){ return getPolarity().inventoryBackups; }
    @Nonnull
    public static WorldsInfoContainer getWorldsInfo() { return getPolarity().worldsInfo; }
    @Nonnull
    public static Polarity getPolarity(){ return (Polarity)Sponge.getPluginManager().getPlugin("polarity").get().getInstance().get(); }
    @Nonnull
    public static GameManager getGameManager() { return getPolarity().gameManager; }
    @Nonnull
    public static GamePresetContainer getGamePresets() { return getPolarity().gamePresets; }
    @Nonnull
    public static Logger getLogger() { return getPolarity().logger; }
    @Nonnull
    public static GeneralChannel getGeneralChannel() { return getPolarity().generalChannel; }
    @Nonnull
    public static NPCManager getNPCManager(){ return getPolarity().npcManager; }
    @Nonnull
    public static GamePartyManager getPartyManager() { return getPolarity().partyManager; }

    Polarity(){

        PluginContainer plugin = Sponge.getPluginManager().getPlugin("polarity").get();

        // Register the economy service
        Sponge.getServiceManager().setProvider(plugin, PolarityEconomyService.class, new PolarityEconomyService());
        economyService = Sponge.getServiceManager().provide(PolarityEconomyService.class).get();

    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event){

        PolarityDataRegistration.register();

    }
    @Listener
    public void onEntitySpawn(DropItemEvent.Dispense.Pre event){

        for(ItemStackSnapshot snap : event.getOriginalDroppedItems()){

            if(snap.createStack().get(UIStackData.class).isPresent()){

                event.setCancelled(true);
                return;

            }

        }

    }

    /**
     * This method listens to the server initialization event. Loads configuration, registers commands and listeners for other objects
     * @param event The {@link GameInitializationEvent} event
     */
    @Listener
    public void onServerInit(GameInitializationEvent event) {

        // Creates rhe specified path
        new File("Polarity/data/").mkdirs();
        loadAllConfig();

        // Register the listeners for the faction's container
        Sponge.getEventManager().registerListeners(this, factionsContainer);
        Sponge.getEventManager().registerListeners(this, worldsInfo);
        Sponge.getEventManager().registerListeners(this, new TabListManager());

        // Register all of the plugin's commands
        Sponge.getCommandManager().register(this, PolarityMainCommand.getCommandSpec(), "polarity");
        Sponge.getCommandManager().register(this, PolarityMainCommand.getCommandSpec(), "economy");
        Sponge.getCommandManager().register(this, FactionsMainCommand.getCommandSpec(), "factions");
        Sponge.getCommandManager().register(this, PolarityHub.getCommandSpec(), "hub");
        Sponge.getCommandManager().register(this, PolarityWarp.getCommandSpec(), "warp");
        Sponge.getCommandManager().register(this, PolarityChannelMain.getCommandSpec(), "channel");
        Sponge.getCommandManager().register(this, PolarityRenameItem.getCommandSpec(), "renameitem");
        Sponge.getCommandManager().register(this, PolarityCosmeticEnchant.getCommandSpec(), "cosmeticenchant");
        Sponge.getCommandManager().register(this, PolarityEnchantItem.getCommandSpec(), "enchantitem");
        Sponge.getCommandManager().register(this, PolarityRemoveEnchantment.getCommandSpec(), "disenchant");
        Sponge.getCommandManager().register(this, PolarityMakeUnbreakable.getCommandSpec(), "makeunbreakable");
        Sponge.getCommandManager().register(this, ShopBuilderMain.getCommandSpec(), "shopbuilder");
        Sponge.getCommandManager().register(this, PolarityShowBalance.getCommandSpec(), "showbalance");
        Sponge.getCommandManager().register(this, PolarityTransfer.getCommandSpec(), "transfer");



        // Prints a cool status message (such cool)
        logger.info("\u00a7aSuccessfully Initialized!");

    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {

        // Initiate the 15 minutes reward system
        Task.builder().delay(15, TimeUnit.MINUTES).execute(this::giveMoneyRewards).submit(this);
        
        if(Utilities.getNPCsAPI().isPresent()) {

            Utilities.getNPCsAPI().get().events().register(npcManager);

        }
        else{

            Sponge.getEventManager().registerListeners(this, npcManager);

        }

    }

    @Listener
    public void onMessageSent(MessageChannelEvent.Chat event){

        if(event.getSource() instanceof Player){

            Player player = (Player)event.getSource();
            event.setChannel(Utilities.getChannelForPlayer(player));

        }

    }

    /**
     * This methods listens to the server stop event. Just saves the plugin's configuration / data
     * @param event The {@link GameStoppingServerEvent} event
     */
    @Listener
    public void onServerStopping(GameStoppingServerEvent event){

        writeAllConfig();

        for(GameSession<?> session : gameManager.getGameSessions()){

            session.endSession(null);

        }

    }

    /**
     * This method listens to player teleportation events. If the world changes between the source and target location, calls a new {@link PlayerChangeDimensionEvent}
     * @param event The {@link MoveEntityEvent.Teleport} event
     */
    @Listener
    public void onTeleport(MoveEntityEvent.Teleport event){

        if(event.getTargetEntity() instanceof Player && !event.getTargetEntity().getWorld().equals(event.getToTransform().getExtent())){

            World from = event.getTargetEntity().getWorld();
            World to = event.getToTransform().getExtent();
            Player teleportedPlayer = (Player)event.getTargetEntity();

            PlayerChangeDimensionEvent.Pre preTpEvent =  new PlayerChangeDimensionEvent.Pre(teleportedPlayer, from, to, event.getSource());

            Sponge.getEventManager().post(preTpEvent);

            if(!preTpEvent.isCancelled()){

                event.setCancelled(false);
                PlayerChangeDimensionEvent.Post postTpEvent =  new PlayerChangeDimensionEvent.Post(teleportedPlayer, from, to, event.getSource());
                Utilities.setGameMode(teleportedPlayer, to.getProperties().getGameMode());
                Sponge.getEventManager().post(postTpEvent);

            }
            else{

                event.setCancelled(true);

                if(!preTpEvent.getCancelReason().isEmpty()){

                    teleportedPlayer.sendMessage(preTpEvent.getCancelReason());

                }

            }

        }

    }

    /**
     * This method handles when a player enters or leaves the Hub dimension
     * @param event The {@link PlayerChangeDimensionEvent.Post} event
     */
    @Listener
    public void onDimensionChanged(PlayerChangeDimensionEvent.Post event){

        TabListManager.refreshTabLists();

        // If the player teleports to the Hub dimension
        if(event.getToWorld().getName().equals("Hub")){

            // Restore the player's max health and give it new potion effects
            Utilities.restoreMaxHealth(event.getTargetEntity());
            Utilities.removePotionEffects(event.getTargetEntity());

            List<PotionEffect> effects = new ArrayList<>();

            effects.add(PotionEffect.builder().potionType(PotionEffectTypes.SPEED).amplifier(1).duration(1000000000).particles(false).build());

            Utilities.setPotionEffects(event.getTargetEntity(), effects);

        }
        // Else if the player teleports from the Hub dimension
        else if(event.getFromWorld().getName().equals("Hub")){

            // Clear the player's potion effects
            Utilities.removePotionEffects(event.getTargetEntity());

        }

    }

    /**
     * This method gives a reward of 50.0$ to every online players every 15 minutes
     */
    private void giveMoneyRewards(){

        for(Player player : Sponge.getServer().getOnlinePlayers()){

            if(NucleusAPI.getAFKService().isPresent()){

                if(NucleusAPI.getAFKService().get().isAFK(player)) { return; }

            }

            assert economyService != null;

            if(economyService.getOrCreateAccount(player.getUniqueId()).isPresent()){

                economyService.getOrCreateAccount(player.getUniqueId()).get().deposit(new PolarityCurrency(), BigDecimal.valueOf(50.0), Cause.of(EventContext.empty(), new PlayRewardIdentifier()));

            }

        }

        Task.builder().delay(15, TimeUnit.MINUTES).execute(this::giveMoneyRewards).submit(this);

    }

    /**
     * This method deserializes and loads an object extending {@link PolarityContainer}.
     * @param objectClass The class the desired object should load as
     * @param loader The {@link HoconConfigurationLoader} to use for loading
     * @param <Y> A class extending {@link Serializable} (required for {@link PolarityContainer})
     * @param <T> A class extending {@link PolarityContainer}
     * @return The loaded {@link PolarityContainer} object
     */
    private static <Y extends Serializable, T extends PolarityContainer<Y>> Optional<T> loadConfig(Class<T> objectClass, HoconConfigurationLoader loader){

        try {

            T newData = loader.load().getValue(TypeToken.of(objectClass));
            return Optional.ofNullable(newData);

        } catch (ObjectMappingException | IOException | NullPointerException e) {

            getLogger().info(PolarityColors.RED.getStringColor() + "Load error for class " + objectClass.getName());
            return Optional.empty();

        }

    }

    /**
     * This method serializes and saves an object extending {@link PolarityContainer}.
     * @param objectToSave The object to save.
     * @param loader The {@link HoconConfigurationLoader} to use for saving
     * @param <Y> A class extending {@link Serializable} (required for {@link PolarityContainer})
     * @param <T> A class extending {@link PolarityContainer}
     */
    private static <Y extends Serializable, T extends PolarityContainer<Y>> void writeConfig(T objectToSave, HoconConfigurationLoader loader){

        try {

            if(objectToSave.shouldSave()){

                Class<T> objectClass = (Class<T>)objectToSave.getClass();
                loader.save(loader.createEmptyNode().setValue(TypeToken.of(objectClass), objectToSave));

            }

        } catch (ObjectMappingException | IOException | NullPointerException e) {

            e.printStackTrace();

        }

    }

    public void writeAllConfig(){

        writeConfig(factionsContainer, factionsConfigLoader);
        writeConfig(accountContainer, accountsConfigLoader);
        writeConfig(shopProfiles, shopProfilesConfigLoader);
        writeConfig(worldsInfo, worldsInfoConfigLoader);
        writeConfig(inventoryBackups, inventoryBackupsLoader);
        writeConfig(gamePresets, gamePresetsLoader);


    }

    public void loadAllConfig(){

        // Load the plugin's config if existing.
        loadConfig(inventoryBackups.getClass(), inventoryBackupsLoader).ifPresent(container -> inventoryBackups = container);
        loadConfig(factionsContainer.getClass(), factionsConfigLoader).ifPresent(container -> factionsContainer = container);
        loadConfig(accountContainer.getClass(), accountsConfigLoader).ifPresent(container -> accountContainer = container);
        loadConfig(worldsInfo.getClass(), worldsInfoConfigLoader).ifPresent(container -> worldsInfo = container);
        loadConfig(shopProfiles.getClass(), shopProfilesConfigLoader).ifPresent(container -> shopProfiles = container);
        loadConfig(gamePresets.getClass(), gamePresetsLoader).ifPresent(container -> gamePresets = container);

    }

}
