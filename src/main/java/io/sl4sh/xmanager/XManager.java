package io.sl4sh.xmanager;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.sl4sh.xmanager.commands.*;
import io.sl4sh.xmanager.data.*;
import io.sl4sh.xmanager.economy.shops.merchants.XBuyerNPC;
import io.sl4sh.xmanager.economy.shops.merchants.XShopNPC;
import noppes.npcs.api.NpcAPI;
import io.sl4sh.xmanager.commands.economy.XEconomyMainCommand;
import io.sl4sh.xmanager.commands.factions.XFactionsMainCommand;
import io.sl4sh.xmanager.commands.shopbuilder.XShopBuilderMain;
import io.sl4sh.xmanager.data.containers.XAccountContainer;
import io.sl4sh.xmanager.data.containers.XFactionContainer;
import io.sl4sh.xmanager.data.containers.XShopProfilesContainer;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.economy.XAccount;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import io.sl4sh.xmanager.economy.transactionidentifiers.XPlayRewardIdentifier;
import io.sl4sh.xmanager.tablist.XTabListManager;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

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
    private final HoconConfigurationLoader worldsInfoConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/WorldsInfo.hocon")).build();
    @Nonnull
    private final HoconConfigurationLoader shopProfilesConfigLoader = HoconConfigurationLoader.builder().setFile(new File("config/XManager/ShopProfiles.hocon")).build();

    @Nonnull
    private XFactionContainer factionsContainer = new XFactionContainer();
    @Nonnull
    private XWorldsInfoContainer worldsInfo = new XWorldsInfoContainer();
    @Nonnull
    private XShopProfilesContainer shopProfiles = new XShopProfilesContainer();
    @Nonnull
    private XAccountContainer accountContainer = new XAccountContainer();
    @Nullable
    private XEconomyService economyService;

    @Nonnull
    public static Optional<XEconomyService> getEconomyService(){ return getXManager().economyService == null ? Optional.empty() : Optional.of(getXManager().economyService); }
    @Nonnull
    public static XShopProfilesContainer getShopProfiles() { return getXManager().shopProfiles; }
    @Nonnull
    public static List<XAccount> getAccounts(){ return getXManager().accountContainer.getAccounts(); }
    @Nonnull
    public static List<XFaction> getFactions(){ return getXManager().factionsContainer.getFactionsList(); }
    @Nonnull
    public static XManager getXManager(){ return (XManager)Sponge.getPluginManager().getPlugin("xmanager").get().getInstance().get(); }
    @Nonnull
    public static List<XWorldInfo> getWorldsInfo() {
        return getXManager().worldsInfo.getWorldsInfo();
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

        Sponge.getEventManager().registerListeners(plugin, new XDataRegistration());

    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {

        initLoadConfig();

        Sponge.getCommandManager().register(getXManager(), XFactionsMainCommand.getCommandSpec(), "factions");
        Sponge.getCommandManager().register(getXManager(), XManagerProtectChunk.getCommandSpec(), "protectchunk");
        Sponge.getCommandManager().register(getXManager(), XManagerUnProtectChunk.getCommandSpec(), "unprotectchunk");
        Sponge.getCommandManager().register(getXManager(), XManagerHub.getCommandSpec(), "hub");
        Sponge.getCommandManager().register(getXManager(), XManagerReloadShops.getCommandSpec(), "reloadshops");
        Sponge.getCommandManager().register(getXManager(), XShopBuilderMain.getCommandSpec(), "shopbuilder");
        Sponge.getCommandManager().register(getXManager(), XManagerReloadMainData.getCommandSpec(), "reloadmaindata");
        Sponge.getCommandManager().register(getXManager(), XManagerProtectDimension.getCommandSpec(), "protectdimension");
        Sponge.getCommandManager().register(getXManager(), XManagerWarp.getCommandSpec(), "warp");
        Sponge.getCommandManager().register(getXManager(), XManagerWarp.getRemoveCommandSpec(), "removewarp");
        Sponge.getCommandManager().register(getXManager(), XManagerWarp.getSetCommandSpec(), "setwarp");

        Sponge.getEventManager().registerListeners(getXManager(), new XTabListManager());
        Sponge.getEventManager().registerListeners(getXManager(), factionsContainer);

        xLogger.info("\u00a7a##################################################");
        xLogger.info("\u00a7a#                                                #");
        xLogger.info("\u00a7a#     [XManager] | Successfully Initialized!     #");
        xLogger.info("\u00a7a#                                                #");
        xLogger.info("\u00a7a##################################################");

    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event){

        if(!getEconomyService().isPresent()) { return; }

        NpcAPI.Instance().events().register(new XShopNPC());
        NpcAPI.Instance().events().register(new XBuyerNPC());

        Task.builder().delay(15, TimeUnit.MINUTES).execute(this::giveMoneyRewards).submit(getXManager());

    }

    private void giveMoneyRewards(){

        XEconomyService economyService = getEconomyService().get();

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
        writeWorldsInfoData();
        writeFactionsConfigurationFile();
        writeShopProfiles();

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
        } catch (ObjectMappingException | IOException ignored) {

        }

        this.xLogger.warn("[XManager] | Failed to load factions config. Using new data.");
        return false;

    }

    public Boolean loadMainData(){

        try {
            @Nullable XWorldsInfoContainer newData = worldsInfoConfigLoader.load().getValue(TypeToken.of(XWorldsInfoContainer.class));
            if(newData != null) {worldsInfo = newData; return true;}
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
            } catch (IOException | ObjectMappingException ignored) {

            }

        }

        return false;

    }

    public boolean writeWorldsInfoData(){

        try {

            worldsInfoConfigLoader.save(worldsInfoConfigLoader.createEmptyNode().setValue(TypeToken.of(XWorldsInfoContainer.class), worldsInfo));
            return true;

        } catch (IOException | ObjectMappingException ignored) {


        }

        return false;

    }

    public boolean writeAccountsConfigurationFile(){

        try {

            accountsConfigLoader.save(accountsConfigLoader.createEmptyNode().setValue(TypeToken.of(XAccountContainer.class), accountContainer));
            return true;

        } catch (IOException | ObjectMappingException ignored) {

            return false;

        }

    }

    public boolean writeShopProfiles(){

        try {

            shopProfilesConfigLoader.save(shopProfilesConfigLoader.createEmptyNode().setValue(TypeToken.of(XShopProfilesContainer.class), shopProfiles));
            return true;

        } catch (IOException | ObjectMappingException ignored) {

            return false;

        }


    }

}

