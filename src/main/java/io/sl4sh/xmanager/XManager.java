package io.sl4sh.xmanager;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import io.sl4sh.xmanager.chat.XGeneralChannel;
import io.sl4sh.xmanager.commands.*;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionContainer;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import io.sl4sh.xmanager.factions.commands.*;
import io.sl4sh.xmanager.player.XPlayer;
import io.sl4sh.xmanager.player.XPlayerContainer;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
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
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;
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
    private XTabListManager xTabListManager;
    private XFactionContainer xFactionContainer;
    private XPlayerContainer xPlayerContainer;
    private XConfigData configData;
    private XGeneralChannel xChannel = new XGeneralChannel();

    public XConfigData getConfigData(){

        return this.configData;

    }

    public XFactionContainer getFactionContainer(){

        return this.xFactionContainer;

    }

    public XPlayerContainer getPlayerContainer(){

        return this.xPlayerContainer;

    }

    static public XManager getXManager(){

        return (XManager)Sponge.getPluginManager().getPlugin("xmanager").get().getInstance().get();

    }

    public static void xLogInfo(String str){

        getXManager().xLogger.info(str);

    }

    public static void xLogSuccess(String str){

        getXManager().xLogger.info("\u00a7a" + str);

    }

    public static void xLogWarning(String str){

        getXManager().xLogger.warn(str);

    }

    public static void xLogError(String str){

        getXManager().xLogger.error(str);

    }

    XManager(){

        PluginContainer plugin = Sponge.getPluginManager().getPlugin("xmanager").get();

        CommandSpec forceDisbandCmdSpec = CommandSpec.builder()
                .description(Text.of("Force disband a faction."))
                .permission("xmanager.factions.forcedisband")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("factionName"))))
                .executor(new XFactionForceDisband())
                .build();

        CommandSpec disbandCmdSpec = CommandSpec.builder()
                .description(Text.of("Disbands your faction."))
                .permission("xmanager.factions.disband")
                .executor(new XFactionDisband())
                .build();

        CommandSpec claimCmdSpec = CommandSpec.builder()
                .description(Text.of("Claim a chunk for your faction."))
                .permission("xmanager.factions.claim")
                .executor(new XFactionClaim())
                .build();

        CommandSpec leaveCmdSpec = CommandSpec.builder()
                .description(Text.of("Leaves your faction."))
                .permission("xmanager.factions.leave")
                .executor(new XFactionLeave())
                .build();

        CommandSpec createCmdSpec = CommandSpec.builder()
                .description(Text.of("Creates a faction."))
                .permission("xmanager.factions.create")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("factionName"))))
                .executor(new XFactionCreate())
                .build();

        CommandSpec helpCmdSpec = CommandSpec.builder()
                .description(Text.of("Prints help about X factions."))
                .permission("xmanager.factions.help")
                .executor(new XFactionHelp())
                .build();

        CommandSpec unClaimCmdSpec = CommandSpec.builder()
                .description(Text.of("Unclaims a chunk."))
                .permission("xmanager.factions.unclaim")
                .executor(new XFactionUnClaim())
                .build();

        CommandSpec facListAlliesCmdSpec = CommandSpec.builder()
                .description(Text.of("Lists the allies of a faction."))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("factionName"))))
                .permission("xmanager.factions.list.allies")
                .executor(new XFactionListAllies())
                .build();

        CommandSpec facListMembersCmdSpec = CommandSpec.builder()
                .description(Text.of("Lists the members of a faction."))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("factionName"))))
                .permission("xmanager.factions.list.members")
                .executor(new XFactionListMembers())
                .build();

        CommandSpec facListHelpCmdSpec = CommandSpec.builder()
                .description(Text.of("List information about factions."))
                .permission("xmanager.factions.list.help")
                .executor(new XFactionListHelp())
                .build();

        CommandSpec facListCmdSpec = CommandSpec.builder()
                .description(Text.of("Lists the existing factions."))
                .permission("xmanager.factions.list")
                .child(facListAlliesCmdSpec, "allies")
                .child(facListMembersCmdSpec, "members")
                .child(facListHelpCmdSpec, "help")
                .executor(new XFactionList())
                .build();

        CommandSpec factionsSetDPNameCmdSpec = CommandSpec.builder()
                .description(Text.of("Sets your faction's display name."))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("displayName"))))
                .permission("xmanager.factions.setdisplayname")
                .executor(new XFactionSetDisplayName())
                .build();

        CommandSpec factionsSetPrefixCmdSpec = CommandSpec.builder()
                .description(Text.of("Sets your faction's prefix name."))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("prefix"))))
                .permission("xmanager.factions.setprefix")
                .executor(new XFactionSetPrefix())
                .build();

        CommandSpec facKickMemberCmdSpec = CommandSpec.builder()
                .description(Text.of("Kicks a member of your faction."))
                .arguments(GenericArguments.player(Text.of("playerName")))
                .permission("xmanager.factions.kick")
                .executor(new XFactionKick())
                .build();

        CommandSpec facShowClaimsCmdSpec = CommandSpec.builder()
                .description(Text.of("Shows your faction's claims."))
                .permission("xmanager.factions.showclaims")
                .executor(new XFactionShowClaims())
                .build();

        CommandSpec setHomeCmdSpec = CommandSpec.builder()
                .description(Text.of("Creates a home point."))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("homeName"))), GenericArguments.optional(GenericArguments.bool(Text.of("overWrite"))))
                .permission("xmanager.sethome")
                .executor(new XManagerSetHome())
                .build();

        CommandSpec homeCmdSpec = CommandSpec.builder()
                .description(Text.of("Teleport to a home point."))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("homeName"))))
                .permission("xmanager.home")
                .executor(new XManagerHome())
                .build();

        CommandSpec listHomesCmdSpec = CommandSpec.builder()
                .description(Text.of("Lists your different homes."))
                .permission("xmanager.listhomes")
                .executor(new XManagerListHomes())
                .build();

        CommandSpec rmHomeCmdSpec = CommandSpec.builder()
                .description(Text.of("Removes one of your homes."))
                .arguments(GenericArguments.string(Text.of("homeName")))
                .permission("xmanager.removeHome")
                .executor(new XManagerRemoveHome())
                .build();

        CommandSpec facPermCmdSpec = CommandSpec.builder()
                .description(Text.of("Sets a permission for a member of your faction."))
                .arguments(GenericArguments.player(Text.of("targetPlayer")), GenericArguments.string(Text.of("permName")), GenericArguments.bool(Text.of("value")))
                .permission("xmanager.factions.perm")
                .executor(new XFactionPerm())
                .build();

        CommandSpec facInviteCmdSpec = CommandSpec.builder()
                .description(Text.of("Invites a player to your faction."))
                .arguments(GenericArguments.player(Text.of("playerName")))
                .permission("xmanager.factions.invite")
                .executor(new XFactionInvite())
                .build();

        CommandSpec facJoinCmdSpec = CommandSpec.builder()
                .description(Text.of("Joins a faction."))
                .arguments(GenericArguments.string(Text.of("factionName")))
                .permission("xmanager.factions.join")
                .executor(new XFactionJoin())
                .build();

        CommandSpec facSetHomeCmdSpec = CommandSpec.builder()
                .description(Text.of("Sets your faction's home."))
                .permission("xmanager.factions.sethome")
                .executor(new XFactionSetHome())
                .build();

        CommandSpec facHomeCmdSpec = CommandSpec.builder()
                .description(Text.of("Teleport to your faction's home."))
                .permission("xmanager.factions.home")
                .executor(new XFactionHome())
                .build();

        CommandSpec facAllyRqCmdSpec = CommandSpec.builder()
                .description(Text.of("Request an alliance to another faction."))
                .arguments(GenericArguments.string(Text.of("factionName")))
                .permission("xmanager.factions.ally.request")
                .executor(new XFactionAllyRequest())
                .build();

        CommandSpec facAllyAcceptCmdSpec = CommandSpec.builder()
                .description(Text.of("Accept an alliance from another faction."))
                .arguments(GenericArguments.string(Text.of("factionName")))
                .permission("xmanager.factions.ally.accept")
                .executor(new XFactionAllyAccept())
                .build();


        CommandSpec facAllyDeclineCmdSpec = CommandSpec.builder()
                .description(Text.of("Decline an alliance from another faction."))
                .arguments(GenericArguments.string(Text.of("factionName")))
                .permission("xmanager.factions.ally.decline")
                .executor(new XFactionAllyDecline())
                .build();


        CommandSpec facSetOwnerCmdSpec = CommandSpec.builder()
                .description(Text.of("Sets your faction's owner."))
                .permission("xmanager.factions.setowner")
                .arguments(GenericArguments.player(Text.of("playerName")))
                .executor(new XFactionSetOwner())
                .build();

        CommandSpec factionsAllyCmdSpec = CommandSpec.builder()
                .description(Text.of("XFactions alliance command. Prints help if no argument is provided."))
                .permission("xmanager.factions.ally")
                .child(facAllyRqCmdSpec, "request")
                .child(facAllyAcceptCmdSpec, "accept")
                .child(facAllyDeclineCmdSpec, "decline")
                .executor(new XFactionAlly())
                .build();

        CommandSpec facDeAllyCmdSpec = CommandSpec.builder()
                .description(Text.of("Destroy an alliance with another faction."))
                .arguments(GenericArguments.string(Text.of("factionName")))
                .permission("xmanager.factions.deally")
                .executor(new XFactionDeAlly())
                .build();


        CommandSpec factionsCmdSpec = CommandSpec.builder()
                .description(Text.of("XFactions command. Prints help if no argument is provided."))
                .permission("xmanager.factions")
                .child(createCmdSpec, "create")
                .child(helpCmdSpec, "help")
                .child(claimCmdSpec, "claim")
                .child(forceDisbandCmdSpec, "forcedisband")
                .child(disbandCmdSpec, "disband")
                .child(leaveCmdSpec, "leave")
                .child(unClaimCmdSpec, "unclaim")
                .child(facListCmdSpec, "list")
                .child(factionsSetDPNameCmdSpec, "setdisplayname")
                .child(factionsSetPrefixCmdSpec, "setprefix")
                .child(facPermCmdSpec, "perm")
                .child(facInviteCmdSpec, "invite")
                .child(facJoinCmdSpec, "join")
                .child(facKickMemberCmdSpec, "kick")
                .child(facShowClaimsCmdSpec, "showclaims")
                .child(facHomeCmdSpec, "home")
                .child(facSetHomeCmdSpec, "sethome")
                .child(facSetOwnerCmdSpec, "setowner")
                .child(factionsAllyCmdSpec, "ally")
                .child(facDeAllyCmdSpec, "deally")
                .executor(new XFactionCommandManager())
                .build();

        CommandSpec protectChunkCmdSpec = CommandSpec.builder()
                .description(Text.of("Add protected chunk."))
                .permission("xmanager.protectchunk")
                .executor(new XManagerProtectChunk())
                .build();

        CommandSpec unProtectChunkCmdSpec = CommandSpec.builder()
                .description(Text.of("Removes protected chunk."))
                .permission("xmanager.unprotectchunk")
                .executor(new XManagerUnProtectChunk())
                .build();

        CommandSpec setHubCmdSpec = CommandSpec.builder()
                .description(Text.of("Sets the hub location."))
                .permission("xmanager.sethub")
                .executor(new XManagerSetHub())
                .build();

        CommandSpec hubCmdSpec = CommandSpec.builder()
                .description(Text.of("Teleports to the hub."))
                .permission("xmanager.hub")
                .executor(new XManagerHub())
                .build();

        CommandSpec mainCmdSpec = CommandSpec.builder()
                .description(Text.of("Main XManager command."))
                .permission("xmanager")
                .child(setHomeCmdSpec, "sethome")
                .child(homeCmdSpec, "home")
                .child(listHomesCmdSpec, "listhomes")
                .child(rmHomeCmdSpec, "rmhome")
                .executor(new XManagerCommandManager())
                .build();

        Sponge.getCommandManager().register(plugin, mainCmdSpec, "xm");
        Sponge.getCommandManager().register(plugin, factionsCmdSpec, "factions");
        Sponge.getCommandManager().register(plugin, protectChunkCmdSpec, "protectChunk");
        Sponge.getCommandManager().register(plugin, unProtectChunkCmdSpec, "unprotectChunk");
        Sponge.getCommandManager().register(plugin, setHubCmdSpec, "setHub");
        Sponge.getCommandManager().register(plugin, hubCmdSpec, "hub");

    }

    @Listener
    public void onServerInit(GameInitializationEvent event) throws IOException {

        xFactionContainer = startupGetFactions();
        xPlayerContainer = startupGetPlayerInfo();
        configData = startupGetDataConfigFile();
        xTabListManager = new XTabListManager();
        xLogSuccess("XManager successfully initialized!");

    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event){

        writeFactions();
        writePlayerInfo();
        writeDataConfigFile();

    }

    @Listener
    public void onBlockPlaced(ChangeBlockEvent.Place event){


        for (Transaction<BlockSnapshot> snap : event.getTransactions()) {

            Vector3i chunkPos = snap.getFinal().getLocation().get().getChunkPosition();

            List<String> protectedChunks = configData.getServerProtectedChunks();

            for(String protectedChunk : protectedChunks){

                if(getStringAsVector3i(protectedChunk).equals(chunkPos)){

                    event.setCancelled(true);
                    return;

                }

            }

            if(XFactionClaim.isChunkClaimed(chunkPos)){

                if(event.getSource() instanceof Player){

                    Player ply = (Player)event.getSource();

                    XFaction owningFaction = XFactionClaim.getClaimedChunkFaction(chunkPos);

                    if(owningFaction == null) { return; }

                    Optional<XFaction> optTargetFaction = XFactionCommandManager.getPlayerFaction(ply);

                    if(optTargetFaction.isPresent() && owningFaction == optTargetFaction.get()) {

                        Optional<XFactionPermissionData> optPermData = XFactionCommandManager.getPlayerFactionPermissions(ply);

                        if(optPermData.isPresent()) {

                            if(optPermData.get().getInteract()){

                                return;

                            }

                        }

                    }
                    else if(optTargetFaction.isPresent() && optTargetFaction.get().isFactionAllied(owningFaction)){

                        return;

                    }

                    String dName = owningFaction.getFactionDisplayName().replace("&", "\u00a7");
                    ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc() + "\u00a7c Chunk owned by " + dName + "\u00a7c."));

                }

                event.setCancelled(true);

            }

        }

    }

    @Listener
    public void onBlockBroken(ChangeBlockEvent.Break event){

        for (Transaction<BlockSnapshot> snap : event.getTransactions()) {

            Vector3i chunkPos = snap.getFinal().getLocation().get().getChunkPosition();

            List<String> protectedChunks = configData.getServerProtectedChunks();

            for(String protectedChunk : protectedChunks){

                if(getStringAsVector3i(protectedChunk).equals(chunkPos)){

                    event.setCancelled(true);
                    return;

                }

            }

            if(XFactionClaim.isChunkClaimed(chunkPos)){

                if(event.getSource() instanceof Player){

                    Player ply = (Player)event.getSource();

                    XFaction owningFaction = XFactionClaim.getClaimedChunkFaction(chunkPos);

                    if(owningFaction == null) { return; }

                    Optional<XFaction> optTargetFaction = XFactionCommandManager.getPlayerFaction(ply);

                    if(optTargetFaction.isPresent() && owningFaction == optTargetFaction.get()) {

                        Optional<XFactionPermissionData> optPermData = XFactionCommandManager.getPlayerFactionPermissions(ply);

                        if(optPermData.isPresent()) {

                            if(optPermData.get().getInteract()){

                                return;

                            }

                        }

                    }
                    else if(optTargetFaction.isPresent() && optTargetFaction.get().isFactionAllied(owningFaction)){

                        return;

                    }

                    String dName = owningFaction.getFactionDisplayName().replace("&", "\u00a7");
                    ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc() + "\u00a7c Chunk owned by " + dName + "\u00a7c."));

                }

                event.setCancelled(true);

            }

        }

    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event){

        if(event.getTargetBlock().getLocation().isPresent()){

            Vector3i chunkPos = event.getTargetBlock().getLocation().get().getChunkPosition();

            if(XFactionClaim.isChunkClaimed(chunkPos)){

                if(event.getSource() instanceof Player){

                    Player ply = (Player)event.getSource();

                    XFaction owningFaction = XFactionClaim.getClaimedChunkFaction(chunkPos);

                    if(owningFaction == null) { return; }

                    Optional<XFaction> optTargetFaction = XFactionCommandManager.getPlayerFaction(ply);

                    if(optTargetFaction.isPresent() && owningFaction == optTargetFaction.get()) {

                        Optional<XFactionPermissionData> optPermData = XFactionCommandManager.getPlayerFactionPermissions(ply);

                        if(optPermData.isPresent()) {

                            if(optPermData.get().getInteract()){

                                return;

                            }

                        }

                    }
                    else if(optTargetFaction.isPresent() && optTargetFaction.get().isFactionAllied(owningFaction)){

                        return;

                    }

                    String dName = owningFaction.getFactionDisplayName().replace("&", "\u00a7");
                    ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc() + "\u00a7c Chunk owned by " + dName + "\u00a7c."));

                }

                event.setCancelled(true);

            }

        }

    }

    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event){

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

        xLogSuccess("Called player respawn.");

    }

    @Listener
    public void onMobSpawn(ConstructEntityEvent.Pre event){

        List<String> protectedChunks = configData.getServerProtectedChunks();

        for(String protectedChunk : protectedChunks){

            if(event.getTransform().getLocation().getChunkPosition().equals(getStringAsVector3i(protectedChunk))){

                event.setCancelled(true);
                return;

            }

        }

    }

    @Listener
    public void onDamageDealt(DamageEntityEvent event){

        List<String> protectedChunks = configData.getServerProtectedChunks();

        for(String protectedChunk : protectedChunks){

            if(event.getTargetEntity().getLocation().getChunkPosition().equals(getStringAsVector3i(protectedChunk))){

                event.setCancelled(true);
                return;

            }

        }

    }

    @Listener
    public void onMessageSent(MessageChannelEvent.Chat event){

        if(event.getSource() instanceof Player){

            Player ply = (Player)event.getSource();
            Optional<XFaction> optXFac = XFactionCommandManager.getPlayerFaction(ply);

            String message = event.getRawMessage().toPlain().replace("&", "\u00a7");

            if(optXFac.isPresent() && !optXFac.get().getFactionPrefix().equals("")){

                String nicePrefix = optXFac.get().getFactionPrefix().replace("&", "\u00a7");
                event.setMessage(Text.of(nicePrefix + "\u00a7r <" + ply.getName() + "> " + message));

            }
            else{

                event.setMessage(Text.of("<" + ply.getName() + "> " + message));

            }

        }

    }

    @Listener
    public void onPlayerJoined(ClientConnectionEvent.Join event){

        if(xPlayerContainer == null){

            event.getTargetEntity().kick(Text.of("\u00a7cXManager failed to register your user. Please try again or contact your system administrator."));
            return;

        }

        if(!xPlayerContainer.isPlayerRegistered(event.getTargetEntity())){

            if(!xPlayerContainer.registerPlayer(event.getTargetEntity())){

                event.getTargetEntity().kick(Text.of("\u00a7cXManager failed to register your user. Please try again or contact your system administrator."));

            }
            else{

                for(Player ply : Sponge.getGame().getServer().getOnlinePlayers()){

                    ply.sendMessage(Text.of("\u00a7dPlease welcome \u00a7e" + event.getTargetEntity().getName() + " \u00a7dto the server!"));

                }

            }

        }

        XTabListManager.refreshTabLists();

    }

    private XFactionContainer startupGetFactions(){

        try {

            Yaml ymlConfig = new Yaml(new Constructor(XFaction.class));
            InputStream inputStream = new FileInputStream("config/XManager/factions.yml");
            return ymlConfig.loadAs(inputStream, XFactionContainer.class);

        } catch (FileNotFoundException e) {

            return createFactionsConfigFile();

        }

    }

    private XFactionContainer createFactionsConfigFile(){

        File configDir = new File("config/XManager");
        Yaml ymlConfig = new Yaml(new Constructor(XFaction.class));
        FileWriter writer = null;

        if(!configDir.exists()){

            if(!configDir.mkdir()){

                xLogError(XError.XERROR_DIRWRITEFAIL.getDesc());
                return null;

            }

        }

        try {

            writer = new FileWriter("config/XManager/factions.yml");
            XFactionContainer sfContainer = new XFactionContainer();
            ymlConfig.dump(sfContainer, writer);
            xLogSuccess("Successfully created xmfcontainer");
            return sfContainer;

        } catch (IOException e) {

            xLogError(XError.XERROR_FILEWRITEFAIL.getDesc() + e.getMessage());
            return null;

        }

    }

    public Boolean writeFactions(){

        try {

            Yaml ymlConfig = new Yaml(new Constructor(XFaction.class));
            FileWriter writer = new FileWriter("config/XManager/factions.yml");
            ymlConfig.dump(xFactionContainer, writer);
            return true;

        } catch (IOException e) {

            xLogError(XError.XERROR_FILEWRITEFAIL.getDesc() + e.getMessage());
            return false;

        }

    }

    private XPlayerContainer startupGetPlayerInfo(){

        try {

            Yaml ymlConfig = new Yaml(new Constructor(XPlayer.class));
            InputStream inputStream = new FileInputStream("config/XManager/players.yml");
            return ymlConfig.loadAs(inputStream, XPlayerContainer.class);

        } catch (FileNotFoundException e) {

            return createPlayerInfoConfigFile();

        }

    }

    private XPlayerContainer createPlayerInfoConfigFile(){

        File configDir = new File("config/XManager");
        Yaml ymlConfig = new Yaml(new Constructor(XPlayer.class));
        FileWriter writer = null;

        if(!configDir.exists()){

            if(!configDir.mkdir()){

                xLogError(XError.XERROR_DIRWRITEFAIL.getDesc());
                return null;

            }

        }

        try {

            writer = new FileWriter("config/XManager/players.yml");
            XPlayerContainer plContainer = new XPlayerContainer();
            ymlConfig.dump(plContainer, writer);
            xLogSuccess("Successfully created xmpContainer");
            return plContainer;

        } catch (IOException e) {

            xLogError(XError.XERROR_FILEWRITEFAIL.getDesc() + e.getMessage());
            return null;

        }

    }

    private XConfigData createDataConfigFile(){

        File configDir = new File("config/XManager");
        Yaml ymlConfig = new Yaml(new Constructor(XPlayer.class));
        FileWriter writer = null;

        if(!configDir.exists()){

            if(!configDir.mkdir()){

                xLogError(XError.XERROR_DIRWRITEFAIL.getDesc());
                return null;

            }

        }

        try {

            writer = new FileWriter("config/XManager/data.yml");
            XConfigData plContainer = new XConfigData();
            ymlConfig.dump(plContainer, writer);
            xLogSuccess("Successfully created dataContainer");
            return plContainer;

        } catch (IOException e) {

            xLogError(XError.XERROR_FILEWRITEFAIL.getDesc() + e.getMessage());
            return null;

        }

    }

    public Boolean writeDataConfigFile(){

        xLogWarning("Writing data config file...");

        try {

            Yaml ymlConfig = new Yaml(new Constructor(XConfigData.class));
            FileWriter writer = new FileWriter("config/XManager/data.yml");
            ymlConfig.dump(configData, writer);
            xLogSuccess("Successfully wrote data config file!");
            return true;

        } catch (IOException e) {

            xLogError(XError.XERROR_FILEWRITEFAIL.getDesc() + e.getMessage());
            return false;

        }
    }

    private XConfigData startupGetDataConfigFile(){

        try {

            Yaml ymlConfig = new Yaml(new Constructor(XConfigData.class));
            InputStream inputStream = new FileInputStream("config/XManager/data.yml");
            return ymlConfig.loadAs(inputStream, XConfigData.class);

        } catch (FileNotFoundException e) {

            return createDataConfigFile();

        }

    }

    public Boolean writePlayerInfo(){

        xLogWarning("Writing player config file...");

        try {

            Yaml ymlConfig = new Yaml(new Constructor(XPlayer.class));
            FileWriter writer = new FileWriter("config/XManager/players.yml");
            ymlConfig.dump(xPlayerContainer, writer);
            xLogSuccess("Successfully wrote player config file!");
            return true;

        } catch (IOException e) {

            xLogError(XError.XERROR_FILEWRITEFAIL.getDesc() + e.getMessage());
            return false;

        }

    }

    public static Vector3i getStringAsVector3i(String vectorString){

        String str = vectorString.substring(1, vectorString.length() - 1);

        int commaSep = str.indexOf(",");

        String xStr = str.substring(0, commaSep);
        String yStr = str.substring(commaSep + 2, str.length());
        commaSep = yStr.indexOf(",");
        String zStr = yStr.substring(commaSep + 2, yStr.length());
        yStr = yStr.substring(0, commaSep);

        int xVal = Integer.parseInt(xStr);
        int yVal = Integer.parseInt(yStr);
        int zVal = Integer.parseInt(zStr);

        return new Vector3i(xVal, yVal, zVal);

    }

    public static Vector3d getStringAsVector3d(String vectorString){

        String str = vectorString.substring(1, vectorString.length() - 1);

        int commaSep = str.indexOf(",");

        String xStr = str.substring(0, commaSep);
        String yStr = str.substring(commaSep + 2, str.length());
        commaSep = yStr.indexOf(",");
        String zStr = yStr.substring(commaSep + 2, yStr.length());
        yStr = yStr.substring(0, commaSep);

        double xVal = Double.parseDouble(xStr);
        double yVal = Double.parseDouble(yStr);
        double zVal = Double.parseDouble(zStr);

        return new Vector3d(xVal, yVal, zVal);

    }


    public static String getStringWithoutModifiers(String input){

        StringBuilder strBld = new StringBuilder(input);

        int ampSep = input.indexOf("&");

        while(ampSep != -1){

            strBld.deleteCharAt(ampSep);
            strBld.deleteCharAt(ampSep);
            ampSep = strBld.toString().indexOf("&");

        }

        return strBld.toString();

    }

    public static String getStringReplacingModifierChar(String str){

        return str.replace("&", "\u00a7");

    }

}

