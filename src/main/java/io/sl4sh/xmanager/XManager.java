package io.sl4sh.xmanager;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import io.sl4sh.xmanager.commands.*;
import io.sl4sh.xmanager.commands.economy.XEconomyMainCommand;
import io.sl4sh.xmanager.commands.economy.XTradeBuilder;
import io.sl4sh.xmanager.commands.factions.XFactionsClaim;
import io.sl4sh.xmanager.commands.factions.XFactionsMainCommand;
import io.sl4sh.xmanager.commands.tradebuilder.XTradeBuilderMainCommand;
import io.sl4sh.xmanager.data.XAccountContainer;
import io.sl4sh.xmanager.data.XConfigData;
import io.sl4sh.xmanager.data.XManagerKnownUserData;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.economy.XPlayerAccount;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.data.factions.XFactionContainer;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
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
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    private XFactionContainer factionsContainer;
    private XConfigData configData;
    private XManagerKnownUserData knownUsers;
    private XEconomyService economyService;
    private XAccountContainer accountContainer;
    @Nonnull
    private List<TradeOffer> tradesList = new ArrayList<>();
    private XTradeBuilder tradeBuilder;

    @Nonnull
    public List<TradeOffer> getTradesList(){

        return tradesList;

    }

    public XTradeBuilder newTradeBuilder(){

        tradeBuilder = new XTradeBuilder();
        return tradeBuilder;

    }

    public Optional<XTradeBuilder> getTradeBuilder(){

        if(tradeBuilder == null) { return Optional.empty(); }

        return Optional.of(tradeBuilder);

    }

    public XEconomyService getXEconomyService(){

        return economyService;

    }

    public List<XPlayerAccount> getPlayerAccounts(){

        return this.accountContainer.getPlayerAccounts();

    }

    public XConfigData getConfigData(){

        return this.configData;

    }

    public XFactionContainer getFactionsContainer(){

        return this.factionsContainer;

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

        Sponge.getCommandManager().register(plugin, XTradeBuilderMainCommand.getCommandSpec(), "tradebuilder");
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

                // Prevent block placement in protected areas
                if(XUtilities.isLocationProtected(snap.getFinal().getLocation().get())) { event.setCancelled(true); return; }

                Vector3i chunkPos = snap.getFinal().getLocation().get().getChunkPosition();

                if(XFactionsClaim.isChunkClaimed(chunkPos)){

                    if(event.getSource() instanceof Player){

                        Player ply = (Player)event.getSource();

                        XFaction owningFaction = XFactionsClaim.getClaimedChunkFaction(chunkPos);

                        if(owningFaction == null) { return; }

                        Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(ply);

                        if(optTargetFaction.isPresent() && owningFaction == optTargetFaction.get()) {

                            Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

                            if(optPermData.isPresent()) {

                                if(optPermData.get().getInteract()){

                                    return;

                                }

                            }

                        }
                        else if(optTargetFaction.isPresent() && optTargetFaction.get().isFactionAllied(owningFaction)){

                            return;

                        }

                        String niceDisplayName = XUtilities.getStringReplacingModifierChar(owningFaction.getFactionDisplayName());
                        ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc() , " Chunk owned by " , niceDisplayName , TextColors.RESET , TextColors.RED , "."));

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

            Vector3i chunkPos = location.getChunkPosition();

            if(XFactionsClaim.isChunkClaimed(chunkPos)){

                if(event.getSource() instanceof Player){

                    Player ply = (Player)event.getSource();

                    XFaction owningFaction = XFactionsClaim.getClaimedChunkFaction(chunkPos);

                    if(owningFaction == null) { return; }

                    Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(ply);

                    if(optTargetFaction.isPresent() && owningFaction == optTargetFaction.get()) {

                        Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

                        if(optPermData.isPresent()) {

                            if(optPermData.get().getInteract()){

                                return;

                            }

                        }

                    }
                    else if(optTargetFaction.isPresent() && optTargetFaction.get().isFactionAllied(owningFaction)){

                        return;

                    }

                    String niceDisplayName = XUtilities.getStringReplacingModifierChar(owningFaction.getFactionDisplayName());
                    ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc() , " Chunk owned by " , niceDisplayName , TextColors.RESET , TextColors.RED , "."));

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

            Vector3i chunkPos = event.getTargetBlock().getLocation().get().getChunkPosition();

            if(XFactionsClaim.isChunkClaimed(chunkPos)){

                if(event.getSource() instanceof Player){

                    Player ply = (Player)event.getSource();

                    XFaction owningFaction = XFactionsClaim.getClaimedChunkFaction(chunkPos);

                    if(owningFaction == null) { return; }

                    Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(ply);

                    if(optTargetFaction.isPresent() && owningFaction == optTargetFaction.get()) {

                        Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

                        if(optPermData.isPresent()) {

                            if(optPermData.get().getInteract()){

                                return;

                            }

                        }

                    }
                    else if(optTargetFaction.isPresent() && optTargetFaction.get().isFactionAllied(owningFaction)){

                        return;

                    }

                    String niceDisplayName = XUtilities.getStringReplacingModifierChar(owningFaction.getFactionDisplayName());
                    ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc() , " Chunk owned by " , niceDisplayName , TextColors.RESET , TextColors.RED , "."));

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

            String message = XUtilities.getStringReplacingModifierChar(event.getRawMessage().toPlain());

            if(optXFac.isPresent() && !optXFac.get().getFactionPrefix().equals("")){

                String nicePrefix = XUtilities.getStringReplacingModifierChar(optXFac.get().getFactionPrefix());
                event.setMessage(Text.of(nicePrefix, "\u00a7r <" , ply.getName() , "> " , message));

            }
            else{

                event.setMessage(Text.of("<" , ply.getName() , "> " , message));

            }

        }

    }

    @Listener
    public void onPlayerJoined(ClientConnectionEvent.Join event){

        if(knownUsers != null) {

            if (!knownUsers.getPlayersUUIDs().contains(event.getTargetEntity().getUniqueId())) {

                for (Player ply : Sponge.getGame().getServer().getOnlinePlayers()) {

                    ply.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "Please welcome ", TextColors.YELLOW, event.getTargetEntity().getName(), TextColors.LIGHT_PURPLE, " to the server!"));

                }

                knownUsers.getPlayersUUIDs().add(event.getTargetEntity().getUniqueId());
                writeKnownUsersConfigurationFile();

            }

        }

        if(economyService != null){

            economyService.getOrCreateAccount(event.getTargetEntity().getUniqueId());

        }

        XTabListManager.refreshTabLists();

    }

    private void initLoadConfig(){

        File configDir = new File("config/XManager");

        //Try and create the XManager config directory if not present
        if(!configDir.exists()){

            // Print an error if the creation fails.
            if(!configDir.mkdir()){

                xLogger.error(XError.XERROR_DIRWRITEFAIL.getDesc().toPlain());
                return;

            }

        }

        // Try, read and load the Factions config file
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            factionsContainer = gson.fromJson(new FileReader("config/XManager/Factions.json"), XFactionContainer.class);

        } catch (FileNotFoundException e) {

            factionsContainer = new XFactionContainer();
            writeFactionsConfigurationFile();

        }

        // Try, read and load the main data config file
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            configData = gson.fromJson(new FileReader("config/XManager/MainData.json"), XConfigData.class);

        } catch (FileNotFoundException e) {

            configData = new XConfigData();
            writeMainDataConfigurationFile();

        }

        // Try, read and load the known users data config file
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            knownUsers = gson.fromJson(new FileReader("config/XManager/KnownUsers.json"), XManagerKnownUserData.class);

        } catch (FileNotFoundException e) {

            knownUsers = new XManagerKnownUserData();
            writeKnownUsersConfigurationFile();

        }

        // Try, read and load the accounts data config file
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            accountContainer = gson.fromJson(new FileReader("config/XManager/Accounts.json"), XAccountContainer.class);

        } catch (FileNotFoundException e) {

            accountContainer = new XAccountContainer();
            writeAccountsConfigurationFile();

        }

        File tradesDir = new File("config/XManager/Trades/");

        if(tradesDir.exists() && tradesDir.isDirectory()){

            int it = 0;

            for(File tradeDirectory : Objects.requireNonNull(tradesDir.listFiles())){

                try{

                    ItemStackSnapshot firstItem = XUtilities.deserializeSnapShot("config/XManager/Trades/" + it + "/firstItem");
                    ItemStackSnapshot secondItem = XUtilities.deserializeSnapShot("config/XManager/Trades/" + it + "/secondItem");
                    ItemStackSnapshot sellingItem = XUtilities.deserializeSnapShot("config/XManager/Trades/" + it + "/sellingItem");

                    Optional<TradeOffer> optTrade = new XTradeBuilder(firstItem, secondItem, sellingItem).makeTradeOffer();

                }
                catch(IOException ioError){

                    ioError.printStackTrace();

                }

                it++;

            }

            xLogger.warn("[XManager] | Loaded " + getTradesList().size() + " trade recipes.");

        }

    }


    public Boolean writeFactionsConfigurationFile(){

        if(factionsContainer != null){

            try {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String content = gson.toJson(factionsContainer);
                Files.write(Paths.get("config/XManager/", "Factions.json"), content.getBytes(Charsets.UTF_8));
                return true;

            } catch (IOException e) {

                xLogger.error(XError.XERROR_FILEWRITEFAIL.getDesc() + e.getMessage());
                return false;

            }

        }

        return false;

    }

    public void writeMainDataConfigurationFile(){

        if(configData != null){

            try {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String content = gson.toJson(configData);
                Files.write(Paths.get("config/XManager/", "MainData.json"), content.getBytes(Charsets.UTF_8));


            } catch (IOException e) {

                xLogger.error(XError.XERROR_FILEWRITEFAIL.getDesc() + e.getMessage());

            }

        }

    }

    public void writeKnownUsersConfigurationFile(){

        if(knownUsers != null){

            try {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String content = gson.toJson(knownUsers);
                Files.write(Paths.get("config/XManager/", "KnownUsers.json"), content.getBytes(Charsets.UTF_8));


            } catch (IOException e) {

                xLogger.error(XError.XERROR_FILEWRITEFAIL.getDesc() + e.getMessage());

            }

        }

    }

    public void writeAccountsConfigurationFile(){

        if(accountContainer != null){

            try {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String content = gson.toJson(accountContainer);
                Files.write(Paths.get("config/XManager/", "Accounts.json"), content.getBytes(Charsets.UTF_8));

            } catch (IOException eIO) {

                // Print an error if the creation fails.
                xLogger.error(XError.XERROR_FILEWRITEFAIL.getDesc() + eIO.getMessage());

            }

        }

    }

    public void writeCustomTrades(){

        if(getTradesList().size() > 0){

            File configDir = new File("config/XManager/Trades");

            // Print an error if the creation fails.
            if(!configDir.exists() && !configDir.mkdir()){

                xLogger.error(XError.XERROR_DIRWRITEFAIL.getDesc().toPlain());
                return;

            }

            int it = 0;

            for(TradeOffer offer : tradesList){

                try{

                    File tradeDir = new File("config/XManager/Trades/" + it);

                    if(tradeDir.exists() || tradeDir.mkdir()){

                        String firstItemData = XUtilities.serializeSnapShot(offer.getFirstBuyingItem());
                        String sellingItemData = XUtilities.serializeSnapShot(offer.getSellingItem());

                        Files.write(Paths.get("config/XManager/Trades/" + it, "firstItem"), firstItemData.getBytes(Charsets.UTF_8));
                        Files.write(Paths.get("config/XManager/Trades/" + it, "sellingItem"), sellingItemData.getBytes(Charsets.UTF_8));

                        String secondItemData;

                        if(offer.getSecondBuyingItem().isPresent()){

                            secondItemData = XUtilities.serializeSnapShot(offer.getSecondBuyingItem().get());

                        }
                        else{

                            secondItemData = XUtilities.serializeSnapShot(ItemStackSnapshot.NONE);

                        }

                        Files.write(Paths.get("config/XManager/Trades/" + it, "secondItem"), secondItemData.getBytes(Charsets.UTF_8));


                    }

                }
                catch(IOException err){

                    continue;

                }

                it++;

            }

        }

    }

}

