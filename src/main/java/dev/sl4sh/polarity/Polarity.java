package dev.sl4sh.polarity;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import dev.sl4sh.polarity.commands.PolarityHub;
import dev.sl4sh.polarity.commands.PolarityMainCommand;
import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.commands.factions.FactionsMainCommand;
import dev.sl4sh.polarity.data.registration.DataRegistration;
import dev.sl4sh.polarity.data.containers.*;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.shops.merchants.BuyerNPC;
import dev.sl4sh.polarity.economy.shops.merchants.ShopNPC;
import dev.sl4sh.polarity.economy.transactionidentifiers.PlayRewardIdentifier;
import dev.sl4sh.polarity.events.PlayerChangeDimensionEvent;
import dev.sl4sh.polarity.games.GameBase;
import dev.sl4sh.polarity.games.GameManager;
import dev.sl4sh.polarity.tablist.TabListManager;
import dev.sl4sh.polarity.games.GameLobbyBase;
import noppes.npcs.api.NpcAPI;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Plugin(id = "polarity", name = "Polarity", description = "Polarity", authors = { "Sl4sh!" })
public class Polarity {

    @Inject
    private Logger logger;

    @Nonnull
    private final HoconConfigurationLoader factionsConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/Polarity/Factions.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader accountsConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/Polarity/Accounts.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader worldsInfoConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/Polarity/WorldsInfo.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader shopProfilesConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/Polarity/ShopProfiles.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader inventoryBackupsLoader = HoconConfigurationLoader.builder().setFile(new File("config/Polarity/InventoryBackups.hocon")).build();

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
    @Nullable
    private final PolarityEconomyService economyService;
    @Nonnull
    private final GameManager gameManager = new GameManager();

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
    public static Logger getLogger() { return getPolarity().logger; }

    Polarity(){

        PluginContainer plugin = Sponge.getPluginManager().getPlugin("polarity").get();

        // Register the economy service
        Sponge.getServiceManager().setProvider(plugin, PolarityEconomyService.class, new PolarityEconomyService());
        economyService = Sponge.getServiceManager().provide(PolarityEconomyService.class).get();

        // Register the required listeners on objects
        Sponge.getEventManager().registerListeners(plugin, new DataRegistration());

    }

    /**
     * This method listens to the server initialization event. Loads configuration, registers commands and listeners for other objects
     * @param event The {@link GameInitializationEvent} event
     */
    @Listener
    public void onServerInit(GameInitializationEvent event) {

        // Creates rhe specified path
        new File("config/Polarity/").mkdirs();
        loadAllConfig();

        // Register the listeners for the faction's container
        Sponge.getEventManager().registerListeners(getPolarity(), factionsContainer);
        Sponge.getEventManager().registerListeners(getPolarity(), worldsInfo);
        Sponge.getEventManager().registerListeners(getPolarity(), new TabListManager());

        // Register all of the plugin's commands
        Sponge.getCommandManager().register(getPolarity(), PolarityMainCommand.getCommandSpec(), "polarity");
        Sponge.getCommandManager().register(getPolarity(), PolarityMainCommand.getCommandSpec(), "economy");
        Sponge.getCommandManager().register(getPolarity(), FactionsMainCommand.getCommandSpec(), "factions");
        Sponge.getCommandManager().register(getPolarity(), PolarityHub.getCommandSpec(), "hub");
        Sponge.getCommandManager().register(getPolarity(), PolarityWarp.getCommandSpec(), "warp");

        // Prints a cool status message (such cool)
        logger.info("\u00a7a##################################################");
        logger.info("\u00a7a#                                                #");
        logger.info("\u00a7a#     [Polarity] | Successfully Initialized!     #");
        logger.info("\u00a7a#                                                #");
        logger.info("\u00a7a##################################################");

    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {

        // Initiate the 15 minutes reward system
        Task.builder().delay(15, TimeUnit.MINUTES).execute(this::giveMoneyRewards).submit(getPolarity());

        if(!NpcAPI.IsAvailable()) { return; }

        // Register the listeners for custom NPCs
        NpcAPI.Instance().events().register(new ShopNPC());
        NpcAPI.Instance().events().register(new BuyerNPC());

    }

    /**
     * This methods listens to the server stop event. Just saves the plugin's configuration / data
     * @param event The {@link GameStoppingServerEvent} event
     */
    @Listener
    public void onServerStopping(GameStoppingServerEvent event){

        writeAllConfig();

        for(GameBase game : gameManager.getGameBases()){

            game.destroyGame();

        }

        for(GameLobbyBase<?> lobby : gameManager.getGameLobbies()){

            lobby.destroyLobby();

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
                teleportedPlayer.offer(Keys.GAME_MODE, to.getProperties().getGameMode());
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

        // If the player teleports to the Hub dimension
        if(event.getToWorld().getName().equals("Hub")){

            // Restore the player's max health and give it new potion effects
            Utilities.restoreMaxHealth(event.getTargetEntity());
            Utilities.removePotionEffects(event.getTargetEntity());

            List<PotionEffect> effects = new ArrayList<>();

            effects.add(PotionEffect.builder().potionType(PotionEffectTypes.SPEED).amplifier(1).duration(1000000000).particles(false).build());

            event.getTargetEntity().offer(Keys.POTION_EFFECTS, effects);

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

            assert economyService != null;

            if(economyService.getOrCreateAccount(player.getUniqueId()).isPresent()){

                economyService.getOrCreateAccount(player.getUniqueId()).get().deposit(new PolarityCurrency(), BigDecimal.valueOf(50.0), Cause.of(EventContext.empty(), new PlayRewardIdentifier()));

            }

        }

        Task.builder().delay(15, TimeUnit.MINUTES).execute(this::giveMoneyRewards).submit(getPolarity());

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

            e.printStackTrace();
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

    }

    public void loadAllConfig(){

        // Load the plugin's config if existing.
        loadConfig(inventoryBackups.getClass(), inventoryBackupsLoader).ifPresent(container -> inventoryBackups = container);
        loadConfig(factionsContainer.getClass(), factionsConfigLoader).ifPresent(container -> factionsContainer = container);
        loadConfig(accountContainer.getClass(), accountsConfigLoader).ifPresent(container -> accountContainer = container);
        loadConfig(worldsInfo.getClass(), worldsInfoConfigLoader).ifPresent(container -> worldsInfo = container);
        loadConfig(shopProfiles.getClass(), shopProfilesConfigLoader).ifPresent(container -> shopProfiles = container);

    }

}
