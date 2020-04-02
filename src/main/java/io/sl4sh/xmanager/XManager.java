package io.sl4sh.xmanager;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.sl4sh.xmanager.commands.*;
import io.sl4sh.xmanager.data.registration.XImmutablePlaceholderShopStack;
import io.sl4sh.xmanager.data.registration.XPlaceholderShopStack;
import io.sl4sh.xmanager.data.registration.XPlaceholderShopStackManipulatorBuilder;
import io.sl4sh.xmanager.economy.merchants.XBuyerNPC;
import io.sl4sh.xmanager.economy.merchants.XShopNPC;
import noppes.npcs.api.NpcAPI;
import io.sl4sh.xmanager.commands.economy.XEconomyMainCommand;
import io.sl4sh.xmanager.commands.factions.XFactionsClaim;
import io.sl4sh.xmanager.commands.factions.XFactionsMainCommand;
import io.sl4sh.xmanager.commands.shopbuilder.XShopBuilderMain;
import io.sl4sh.xmanager.data.XConfigData;
import io.sl4sh.xmanager.data.XImmutableMerchantData;
import io.sl4sh.xmanager.data.XMerchantData;
import io.sl4sh.xmanager.data.XMerchantDataManipulatorBuilder;
import io.sl4sh.xmanager.data.containers.XAccountContainer;
import io.sl4sh.xmanager.data.containers.XFactionContainer;
import io.sl4sh.xmanager.data.containers.XShopProfilesContainer;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.economy.accounts.XPlayerAccount;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import io.sl4sh.xmanager.economy.transactionidentifiers.XPlayRewardIdentifier;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.tablist.XTabListManager;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Plugin(

        id = "xmanager",
        name = "XManager",
        description = "XManager",
        authors = {
                "Sl4sh!"
        }
)

public class XManager {

    @Inject
    private Logger xLogger;

    @Nonnull
    private final HoconConfigurationLoader factionsConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/Factions.hocon")).build();

    @Nonnull
    private final HoconConfigurationLoader accountsConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/Accounts.hocon")).build();

    @Nonnull
    private final HoconConfigurationLoader mainDataConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/MainData.hocon")).build();

    @Nonnull
    private final HoconConfigurationLoader shopProfilesConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/ShopProfiles.hocon")).build();

    @Nonnull
    private XFactionContainer factionsContainer = new XFactionContainer();

    @Nonnull
    private XConfigData configData = new XConfigData();

    @Nonnull
    private XShopProfilesContainer shopProfiles = new XShopProfilesContainer();

    @Nonnull
    private XAccountContainer accountContainer = new XAccountContainer();

    private XEconomyService economyService;

    public static Key<Value<String>> SHOP_DATA_NAME = DummyObjectProvider.createFor(Key.class, "SHOP_DATA_NAME");
    public static Key<Value<Boolean>> SHOP_STACK = DummyObjectProvider.createFor(Key.class, "SHOP_STACK");


    public static Optional<XEconomyService> getXEconomyService(){

        return getXManager().economyService == null ? Optional.empty() : Optional.of(getXManager().economyService);

    }

    @Nonnull
    public static XShopProfilesContainer getShopProfiles() { return getXManager().shopProfiles; }

    public static List<XPlayerAccount> getPlayerAccounts(){

        return getXManager().accountContainer.getPlayerAccounts();

    }

    public static XConfigData getConfigData(){

        return getXManager().configData;

    }

    public static List<XFaction> getFactions(){

        return getXManager().factionsContainer.getFactionsList();

    }

    static public XManager getXManager(){

        return (XManager)Sponge.getPluginManager().getPlugin("xmanager").get().getInstance().get();

    }

    static public Optional<NpcAPI> getNPCsAPI(){

        if(NpcAPI.Instance() == null) { return Optional.empty(); }

        return Optional.of(NpcAPI.Instance());

    }

    XManager(){

        PluginContainer plugin = Sponge.getPluginManager().getPlugin("xmanager").get();

        Sponge.getServiceManager().setProvider(plugin, XEconomyService.class, new XEconomyService());
        Optional<XEconomyService> xEconomyService = Sponge.getServiceManager().provide(XEconomyService.class);

        if (xEconomyService.isPresent()) {

            economyService = xEconomyService.get();
            Sponge.getCommandManager().register(plugin, XEconomyMainCommand.getCommandSpec(), "economy");

        }
        else{

            xLogger.warn("##############################################################");
            xLogger.warn("#                                                            #");
            xLogger.warn("#     [XManager] | Economy service failed to initialize!     #");
            xLogger.warn("#                                                            #");
            xLogger.warn("##############################################################");

        }

    }

    @Listener
    public void onRegisterKeys(GameRegistryEvent.Register<Key<?>> event) {

        SHOP_DATA_NAME = Key.builder()
                .type(new TypeToken<Value<String>>() {})
                .query(DataQuery.of("ShopDataName"))
                .name("ShopDataName")
                .id("shopdataname")
                .build();

        SHOP_STACK = Key.builder()
                .type(new TypeToken<Value<Boolean>>() {})
                .query(DataQuery.of("ShopStackIdentifier"))
                .name("ShopStackIdentifier")
                .id("shopstackidentifier")
                .build();

        event.register(SHOP_DATA_NAME);
        event.register(SHOP_STACK);

    }

    @Listener
    public void onServerPreInit(GamePreInitializationEvent event){

        PluginContainer plugin = Sponge.getPluginManager().getPlugin("xmanager").get();

        DataRegistration.builder()
                .dataClass(XMerchantData.class)
                .immutableClass(XImmutableMerchantData.class)
                .builder(new XMerchantDataManipulatorBuilder())
                .manipulatorId("xmerchant_dr")
                .dataName("XMerchantData Registration")
                .buildAndRegister(plugin);

        DataRegistration.builder()
                .dataClass(XPlaceholderShopStack.class)
                .immutableClass(XImmutablePlaceholderShopStack.class)
                .builder(new XPlaceholderShopStackManipulatorBuilder())
                .manipulatorId("xshopstack_dr")
                .dataName("XShopStack Registration")
                .buildAndRegister(plugin);

        Sponge.getEventManager().registerListeners(plugin, new XConfigData());

    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {

        initLoadConfig();

        Sponge.getCommandManager().register(getXManager(), XFactionsMainCommand.getCommandSpec(), "factions");
        Sponge.getCommandManager().register(getXManager(), XManagerProtectChunk.getCommandSpec(), "protectchunk");
        Sponge.getCommandManager().register(getXManager(), XManagerUnProtectChunk.getCommandSpec(), "unprotectchunk");
        Sponge.getCommandManager().register(getXManager(), XManagerSetHub.getCommandSpec(), "sethub");
        Sponge.getCommandManager().register(getXManager(), XManagerHub.getCommandSpec(), "hub");
        Sponge.getCommandManager().register(getXManager(), XManagerReloadShops.getCommandSpec(), "reloadshops");
        Sponge.getCommandManager().register(getXManager(), XShopBuilderMain.getCommandSpec(), "shopbuilder");
        Sponge.getCommandManager().register(getXManager(), XManagerReloadMainData.getCommandSpec(), "reloadmaindata");
        Sponge.getCommandManager().register(getXManager(), XManagerSetInitialSpawnLocation.getCommandSpec(), "setinitialspawnlocation");
        Sponge.getCommandManager().register(getXManager(), XManagerProtectDimension.getCommandSpec(), "protectdimension");
        Sponge.getCommandManager().register(getXManager(), XManagerWarp.getCommandSpec(), "warp");
        Sponge.getCommandManager().register(getXManager(), XManagerWarp.getRemoveCommandSpec(), "removewarp");
        Sponge.getCommandManager().register(getXManager(), XManagerWarp.getSetCommandSpec(), "setwarp");

        xLogger.info("\u00a7a##################################################");
        xLogger.info("\u00a7a#                                                #");
        xLogger.info("\u00a7a#     [XManager] | Successfully Initialized!     #");
        xLogger.info("\u00a7a#                                                #");
        xLogger.info("\u00a7a##################################################");

    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event){

        if(!getXEconomyService().isPresent()) { return; }

        NpcAPI.Instance().events().register(new XShopNPC());
        NpcAPI.Instance().events().register(new XBuyerNPC());

        Task.builder().delay(15, TimeUnit.MINUTES).execute(this::giveMoneyRewards).submit(getXManager());

    }

    public void giveMoneyRewards(){

        XEconomyService economyService = getXEconomyService().get();

        for(Player player : Sponge.getServer().getOnlinePlayers()){

            if(economyService.getOrCreateAccount(player.getUniqueId()).isPresent()){

                economyService.getOrCreateAccount(player.getUniqueId()).get().deposit(new XDollar(), BigDecimal.valueOf(50.0), Cause.of(EventContext.empty(), new XPlayRewardIdentifier()));

            }

        }

        Task.builder().delay(15, TimeUnit.MINUTES).execute(this::giveMoneyRewards).submit(getXManager());

    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event){

        writeFactionsConfigurationFile();
        writeMainDataConfigurationFile();
        writeFactionsConfigurationFile();
        writeShopProfiles();

    }

    @Listener
    public void onBlockPlaced(ChangeBlockEvent.Place event){

        // Ignore all restrictions if player has * permission (Admin)
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getLocation().isPresent())
            {

                World world = Sponge.getServer().getWorld(snap.getFinal().getWorldUniqueId()).get();

                // Prevent block placement in protected areas
                if(XUtilities.isLocationProtected(snap.getFinal().getLocation().get())) { event.setCancelled(true); return; }

                String worldName = snap.getFinal().getLocation().get().getExtent().getName();
                Vector3i chunkPosition = snap.getFinal().getLocation().get().getChunkPosition();

                if(XFactionsClaim.getClaimedChunkFaction(worldName, chunkPosition).isPresent()){

                    if(event.getSource() instanceof Player){

                        Player ply = (Player)event.getSource();

                        Optional<XFaction> owningFaction = XFactionsClaim.getClaimedChunkFaction(worldName, chunkPosition);

                        if(!owningFaction.isPresent()) { return; }

                        Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(ply);

                        if(optTargetFaction.isPresent() && owningFaction.get() == optTargetFaction.get()) {

                            Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

                            if(optPermData.isPresent()) {

                                if(optPermData.get().getInteract()){

                                    return;

                                }

                            }

                        }
                        else if(optTargetFaction.isPresent() && optTargetFaction.get().isFactionAllied(owningFaction.get())){

                            return;

                        }

                        ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc() , " Chunk owned by " , owningFaction.get().getFactionDisplayName() , TextColors.RESET , TextColors.RED , "."));

                    }

                    event.setCancelled(true);
                    return;

                }

            }

        }

    }

    @Listener
    public void preBlockBroken(ChangeBlockEvent.Break.Pre event){

        // Ignore all restrictions if player has * permission (Admin)
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        for(Location<World> location : event.getLocations()){

            // Prevent block breaking in protected areas
            if(XUtilities.isLocationProtected(location)) { event.setCancelled(true); return; }

            String worldName = location.getExtent().getName();
            Vector3i chunkPosition = location.getChunkPosition();

            if(XFactionsClaim.getClaimedChunkFaction(worldName, chunkPosition).isPresent()){

                if(event.getSource() instanceof Player){

                    Player ply = (Player)event.getSource();

                    Optional<XFaction> optOwningFaction = XFactionsClaim.getClaimedChunkFaction(worldName, chunkPosition);

                    if(!optOwningFaction.isPresent()) { return; }

                    Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(ply);

                    if(optTargetFaction.isPresent() && optOwningFaction.get() == optTargetFaction.get()) {

                        Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

                        if(optPermData.isPresent()) {

                            if(optPermData.get().getInteract()){

                                return;

                            }

                        }

                    }
                    else if(optTargetFaction.isPresent() && optTargetFaction.get().isFactionAllied(optOwningFaction.get())){

                        return;

                    }

                    ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc() , " Chunk owned by " , optOwningFaction.get().getFactionDisplayName() , TextColors.RESET , TextColors.RED , "."));

                }

                event.setCancelled(true);
                return;

            }

        }

    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event){

        // Ignore all restrictions if player has * permission (Admin)
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        if(event.getTargetBlock().getLocation().isPresent()){

            String worldName = event.getTargetBlock().getLocation().get().getExtent().getName();
            Vector3i chunkPosition = event.getTargetBlock().getLocation().get().getChunkPosition();

            if(XFactionsClaim.getClaimedChunkFaction(worldName, chunkPosition).isPresent()){

                if(event.getSource() instanceof Player){

                    Player ply = (Player)event.getSource();

                    Optional<XFaction> owningFaction = XFactionsClaim.getClaimedChunkFaction(worldName, chunkPosition);

                    if(!owningFaction.isPresent()) { return; }

                    Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(ply);

                    if(optTargetFaction.isPresent() && owningFaction.get() == optTargetFaction.get()) {

                        Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

                        if(optPermData.isPresent()) {

                            if(optPermData.get().getInteract()){

                                return;

                            }

                        }

                    }
                    else if(optTargetFaction.isPresent() && optTargetFaction.get().isFactionAllied(owningFaction.get())){

                        return;

                    }

                    ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc() , " Chunk owned by " , owningFaction.get().getFactionDisplayName() , TextColors.RESET , TextColors.RED , "."));

                }

                event.setCancelled(true);

            }

        }

    }

    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event){

        // Respawn the player to the hub if it has been set
        if(XUtilities.getStringAsVector3d(configData.getHubData().getLocation()) != Vector3d.ZERO){

            Vector3d location = XUtilities.getStringAsVector3d(configData.getHubData().getLocation());
            Optional<World> optWorld = Sponge.getServer().getWorld(configData.getHubData().getDimensionName());

            optWorld.ifPresent(world -> event.setToTransform(new Transform<>(world, location)));

        }

        TabList playerTabList = event.getTargetEntity().getTabList();

        for(TabListEntry tlEntry : event.getOriginalPlayer().getTabList().getEntries()){

            TabListEntry nEntry = TabListEntry.builder()
                    .gameMode(tlEntry.getGameMode())
                    .displayName(tlEntry.getDisplayName().get())
                    .latency(tlEntry.getLatency())
                    .list(playerTabList)
                    .profile(tlEntry.getProfile())
                    .build();

            playerTabList.addEntry(nEntry);

        }

    }

    @Listener
    public void onMobSpawn(ConstructEntityEvent.Pre event){

        // Ignore all restrictions if player has * permission (Admin)
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        // Prevent mob spawning in protected areas
        if(XUtilities.isLocationProtected(event.getTransform().getLocation())) { event.setCancelled(true); }

    }

    @Listener
    public void onDamageDealt(DamageEntityEvent event){

        // Ignore all restrictions if player has * permission (Admin)
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        // Prevent damage in protected areas
        if(XUtilities.isLocationProtected(event.getTargetEntity().getLocation())) { event.setCancelled(true); }

    }

    @Listener
    public void onMessageSent(MessageChannelEvent.Chat event){

        if(event.getSource() instanceof Player){

            Player ply = (Player)event.getSource();
            Optional<XFaction> optXFac = XUtilities.getPlayerFaction(ply);

            String message = event.getRawMessage().toPlain().replace("&", "\u00a7");

            if(optXFac.isPresent() && !optXFac.get().getFactionPrefix().equals("")){

                String nicePrefix = optXFac.get().getFactionPrefix();
                event.setMessage(Text.of(nicePrefix, "\u00a7r <" , ply.getName() , "> " , message));

            }
            else{

                event.setMessage(Text.of("<" , ply.getName() , "> " , message));

            }

        }

    }


    @Listener
    public void onPlayerJoined(ClientConnectionEvent.Join event){

        if (!event.getTargetEntity().hasPlayedBefore()) {

            for (Player ply : Sponge.getGame().getServer().getOnlinePlayers()) {

                ply.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "Please welcome ", TextColors.YELLOW, event.getTargetEntity().getName(), TextColors.LIGHT_PURPLE, " to the server!"));

            }

        }

        if(economyService != null){

            economyService.getOrCreateAccount(event.getTargetEntity().getUniqueId());

        }

        XTabListManager.refreshTabLists();

    }

    private void initLoadConfig() {

        File configDir = new File("config/XManager/");
        configDir.mkdirs();

        loadFactions();
        loadMainData();
        loadPlayerAccounts();
        loadShopProfiles();

    }

    public Boolean loadFactions(){

        try {
            XFactionContainer newData = factionsConfigLoader.load().getValue(TypeToken.of(XFactionContainer.class));
            if(newData != null) {factionsContainer = newData; return true;}
        } catch (ObjectMappingException | IOException e) {

        }

        this.xLogger.warn("[XManager] | Failed to load factions config. Using new data.");
        return false;

    }

    public Boolean loadMainData(){

        try {
            XConfigData newData = mainDataConfigLoader.load().getValue(TypeToken.of(XConfigData.class));
            if(newData != null) {configData = newData; return true;}
        } catch (ObjectMappingException | IOException ignored) {

        }

        this.xLogger.warn("[XManager] | Failed to load main config. Using new data.");
        return false;

    }

    public Boolean loadPlayerAccounts(){

        try {
            XAccountContainer newData = accountsConfigLoader.load().getValue(TypeToken.of(XAccountContainer.class));
            if(newData != null) {accountContainer = newData; return true;}
        } catch (ObjectMappingException | IOException ignored) {

        }

        this.xLogger.warn("[XManager] | Failed to load player accounts config. Using new data.");
        return false;

    }

    public Boolean loadShopProfiles(){

        try {
            XShopProfilesContainer newData = shopProfilesConfigLoader.load().getValue(TypeToken.of(XShopProfilesContainer.class));
            if(newData != null) {shopProfiles = newData; return true;}
        } catch (ObjectMappingException | IOException ignored) {

        }

        this.xLogger.warn("[XManager] | Failed to load trades config. Using new data.");
        return false;

    }


    public Boolean writeFactionsConfigurationFile(){

        if(getFactions().size() > 0){

            try {
                factionsConfigLoader.save(factionsConfigLoader.createEmptyNode().setValue(TypeToken.of(XFactionContainer.class), factionsContainer));
                return true;
            } catch (IOException | ObjectMappingException e) {
                e.printStackTrace();
                return false;
            }

        }

        return false;

    }

    public boolean writeMainDataConfigurationFile(){

        try {

            mainDataConfigLoader.save(mainDataConfigLoader.createEmptyNode().setValue(TypeToken.of(XConfigData.class), configData));
            return true;

        } catch (IOException | ObjectMappingException e) {

            return false;

        }

    }

    public boolean writeAccountsConfigurationFile(){

        try {

            accountsConfigLoader.save(accountsConfigLoader.createEmptyNode().setValue(TypeToken.of(XAccountContainer.class), accountContainer));
            return true;

        } catch (IOException | ObjectMappingException e) {

            return false;

        }

    }

    public boolean writeShopProfiles(){

        try {

            shopProfilesConfigLoader.save(shopProfilesConfigLoader.createEmptyNode().setValue(TypeToken.of(XShopProfilesContainer.class), shopProfiles));
            return true;

        } catch (IOException | ObjectMappingException e) {

            e.printStackTrace();

            return false;

        }


    }

}

