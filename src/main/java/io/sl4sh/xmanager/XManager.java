package io.sl4sh.xmanager;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.sl4sh.xmanager.commands.*;
import io.sl4sh.xmanager.commands.economy.XEconomyMainCommand;
import io.sl4sh.xmanager.economy.XTradeBuilder;
import io.sl4sh.xmanager.economy.XTradeContainer;
import io.sl4sh.xmanager.commands.factions.XFactionsClaim;
import io.sl4sh.xmanager.commands.factions.XFactionsMainCommand;
import io.sl4sh.xmanager.commands.trade.XTradeMainCommand;
import io.sl4sh.xmanager.data.XAccountContainer;
import io.sl4sh.xmanager.data.XConfigData;
import io.sl4sh.xmanager.data.XManagerKnownUserData;
import io.sl4sh.xmanager.data.factions.XFactionContainer;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.economy.XPlayerAccount;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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

    @Inject
    private Game game;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    @Nonnull
    private final HoconConfigurationLoader factionsConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/Factions.hocon")).build();

    @Nonnull
    private final HoconConfigurationLoader knownUsersConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/KnownUsers.hocon")).build();

    @Nonnull
    private final HoconConfigurationLoader accountsConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/Accounts.hocon")).build();

    @Nonnull
    private final HoconConfigurationLoader mainDataConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/MainData.hocon")).build();

    @Nonnull
    private final HoconConfigurationLoader tradesDataConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/Trades.hocon")).build();

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    @Nonnull
    private XFactionContainer factionsContainer = new XFactionContainer();

    @Nonnull
    private XConfigData configData = new XConfigData();

    @Nonnull
    private XManagerKnownUserData knownUsers = new XManagerKnownUserData();

    @Nonnull
    private XTradeContainer tradesContainer = new XTradeContainer();

    private XEconomyService economyService;

    @Nonnull
    private XAccountContainer accountContainer = new XAccountContainer();

    private XTradeBuilder tradeBuilder;

    @Nonnull
    public XTradeContainer getTradesContainer(){

        return tradesContainer;

    }

    public XTradeBuilder newTradeBuilder(){

        tradeBuilder = new XTradeBuilder();
        return tradeBuilder;

    }

    public Optional<XTradeBuilder> getTradeBuilder(){

        if(tradeBuilder == null) { return Optional.empty(); }

        return Optional.of(tradeBuilder);

    }

    public Optional<XEconomyService> getXEconomyService(){

        return economyService == null ? Optional.empty() : Optional.of(economyService);

    }

    public List<XPlayerAccount> getPlayerAccounts(){

        return this.accountContainer.getPlayerAccounts();

    }

    public XConfigData getConfigData(){

        return this.configData;

    }

    public List<XFaction> getFactions(){

        return this.factionsContainer.getFactionsList();

    }

    static public XManager getXManager(){

        return (XManager)Sponge.getPluginManager().getPlugin("xmanager").get().getInstance().get();

    }

    XManager(){

        // Register the plugin's commands.
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

        Sponge.getCommandManager().register(plugin, XTradeMainCommand.getCommandSpec(), "trade");
        Sponge.getCommandManager().register(plugin, XFactionsMainCommand.getCommandSpec(), "factions");
        Sponge.getCommandManager().register(plugin, XManagerProtectChunk.getCommandSpec(), "protectchunk");
        Sponge.getCommandManager().register(plugin, XManagerUnProtectChunk.getCommandSpec(), "unprotectchunk");
        Sponge.getCommandManager().register(plugin, XManagerSetHub.getCommandSpec(), "sethub");
        Sponge.getCommandManager().register(plugin, XManagerHub.getCommandSpec(), "hub");

    }


    @Listener
    public void onServerInit(GameInitializationEvent event) throws IOException {

        initLoadConfig();

        xLogger.info("\u00a7d##################################################");
        xLogger.info("\u00a7d#                                                #");
        xLogger.info("\u00a7d#     [XManager] | Successfully Initialized!     #");
        xLogger.info("\u00a7d#                                                #");
        xLogger.info("\u00a7d##################################################");

    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event){

        writeFactionsConfigurationFile();
        writeMainDataConfigurationFile();
        writeKnownUsersConfigurationFile();
        writeFactionsConfigurationFile();

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

                if(XFactionsClaim.isLocationClaimed(worldName, chunkPosition)){

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

            if(XFactionsClaim.isLocationClaimed(worldName, chunkPosition)){

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

            if(XFactionsClaim.isLocationClaimed(worldName, chunkPosition)){

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

        // Prevent mob spawning in protected areas
        if(XUtilities.isLocationProtected(event.getTransform().getLocation())) { event.setCancelled(true); }

    }

    @Listener
    public void onDamageDealt(DamageEntityEvent event){

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

        if (!knownUsers.getPlayersUUIDs().contains(event.getTargetEntity().getUniqueId())) {

            for (Player ply : Sponge.getGame().getServer().getOnlinePlayers()) {

                ply.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "Please welcome ", TextColors.YELLOW, event.getTargetEntity().getName(), TextColors.LIGHT_PURPLE, " to the server!"));

            }

            knownUsers.getPlayersUUIDs().add(event.getTargetEntity().getUniqueId());
            writeKnownUsersConfigurationFile();

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
        loadKnownUsers();
        loadMainData();
        loadPlayerAccounts();
        loadCustomTrades();


    }

    public Boolean loadFactions(){

        try {
            XFactionContainer newData = factionsConfigLoader.load().getValue(TypeToken.of(XFactionContainer.class));
            if(newData != null) {factionsContainer = newData; return true;}
        } catch (ObjectMappingException | IOException ignored) {

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

    public Boolean loadKnownUsers(){

        try {
            XManagerKnownUserData newData = knownUsersConfigLoader.load().getValue(TypeToken.of(XManagerKnownUserData.class));
            if(newData != null) {knownUsers = newData; return true;}
        } catch (ObjectMappingException | IOException ignored) {

        }

        this.xLogger.warn("[XManager] | Failed to load known users config. Using new data.");
        return false;

    }

    public Boolean loadCustomTrades(){

        try {
            XTradeContainer newData = tradesDataConfigLoader.load().getValue(TypeToken.of(XTradeContainer.class));
            if(newData != null) {tradesContainer = newData; return true;}
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

    public boolean writeKnownUsersConfigurationFile(){

        if(knownUsers.getPlayersUUIDs().size() > 0){

            try {
                knownUsersConfigLoader.save(knownUsersConfigLoader.createEmptyNode().setValue(TypeToken.of(XManagerKnownUserData.class), knownUsers));
                return true;
            } catch (IOException | ObjectMappingException e) {
                return false;
            }

        }

        return false;

    }

    public boolean writeAccountsConfigurationFile(){

        try {

            accountsConfigLoader.save(accountsConfigLoader.createEmptyNode().setValue(TypeToken.of(XAccountContainer.class), accountContainer));
            return true;

        } catch (IOException | ObjectMappingException e) {

            return false;

        }

    }

    public boolean writeCustomTrades(){

        if(getTradesContainer().getTradeList().size() > 0){

            try {

                tradesDataConfigLoader.save(tradesDataConfigLoader.createEmptyNode().setValue(TypeToken.of(XTradeContainer.class), tradesContainer));
                return true;

            } catch (IOException | ObjectMappingException e) {

                return false;

            }

        }

        return false;

    }

}

