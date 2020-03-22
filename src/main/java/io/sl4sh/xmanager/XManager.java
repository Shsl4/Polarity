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
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;

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
    private XGeneralChannel xChannel = new XGeneralChannel();

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

        CommandSpec listMembersCmdSpec = CommandSpec.builder()
                .description(Text.of("Lists the members of a faction."))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("factionName"))))
                .permission("xmanager.factions.list.members")
                .executor(new XFactionListMembers())
                .build();

        CommandSpec factionsListCmdSpec = CommandSpec.builder()
                .description(Text.of("List information about factions."))
                .permission("xmanager.factions.list")
                .child(listMembersCmdSpec, "members")
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
                .arguments(GenericArguments.string(Text.of("playerName")))
                .permission("xmanager.factions.kick")
                .executor(new XFactionKick())
                .build();

        CommandSpec facShowClaimsCmdSpec = CommandSpec.builder()
                .description(Text.of("Shows your faction's claims."))
                .permission("xmanager.factions.showclaims")
                .executor(new XFactionShowClaims())
                .build();


        CommandSpec writeCmdSpec = CommandSpec.builder()
                .description(Text.of("Writes to config to yml files."))
                .permission("xmanager.write")
                .child(listMembersCmdSpec, "write")
                .executor(new XManagerWrite())
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
                .arguments(GenericArguments.string(Text.of("targetPlayer")), GenericArguments.string(Text.of("permName")), GenericArguments.bool(Text.of("value")))
                .permission("xmanager.factions.perm")
                .executor(new XFactionPerm())
                .build();

        CommandSpec facInviteCmdSpec = CommandSpec.builder()
                .description(Text.of("Invites a player to your faction."))
                .arguments(GenericArguments.string(Text.of("playerName")))
                .permission("xmanager.factions.invite")
                .executor(new XFactionInvite())
                .build();

        CommandSpec facJoinCmdSpec = CommandSpec.builder()
                .description(Text.of("Joins a faction."))
                .arguments(GenericArguments.string(Text.of("factionName")))
                .permission("xmanager.factions.join")
                .executor(new XFactionJoin())
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
                .child(factionsListCmdSpec, "list")
                .child(factionsSetDPNameCmdSpec, "setdisplayname")
                .child(factionsSetPrefixCmdSpec, "setprefix")
                .child(facPermCmdSpec, "perm")
                .child(facInviteCmdSpec, "invite")
                .child(facJoinCmdSpec, "join")
                .child(facKickMemberCmdSpec, "kick")
                .child(facShowClaimsCmdSpec, "showclaims")
                .executor(new XFactionCommandManager())
                .build();

        CommandSpec mainCmdSpec = CommandSpec.builder()
                .description(Text.of("Main XManager command."))
                .permission("xmanager")
                .child(factionsCmdSpec, "factions")
                .child(writeCmdSpec, "write")
                .child(setHomeCmdSpec, "sethome")
                .child(homeCmdSpec, "home")
                .child(listHomesCmdSpec, "listhomes")
                .child(rmHomeCmdSpec, "rmhome")
                .executor(new XManagerCommandManager())
                .build();

        Sponge.getCommandManager().register(plugin, mainCmdSpec, "xm");

    }

    @Listener
    public void onServerInit(GameInitializationEvent event) throws IOException {

        xFactionContainer = startupGetFactions();
        xPlayerContainer = startupGetPlayerInfo();
        xTabListManager = new XTabListManager();
        xLogSuccess("XManager successfully initialized!");

    }

    @Listener
    public void onBlockPlaced(ChangeBlockEvent.Place event){

        for (Transaction<BlockSnapshot> snap : event.getTransactions()) {

            Vector3i chunkPos = snap.getFinal().getLocation().get().getChunkPosition();

            if(XFactionClaim.isChunkClaimed(chunkPos)){

                if(event.getSource() instanceof Player){

                    Player ply = (Player)event.getSource();

                    XFaction owningFaction = XFactionClaim.getClaimedChunkFaction(chunkPos);

                    if(owningFaction == XFactionCommandManager.getPlayerFaction(ply)) {

                        XFactionPermissionData permData = XFactionCommandManager.getPlayerFactionPermissions(ply);

                        if(permData.getInteract()){

                            return;

                        }

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

            if(XFactionClaim.isChunkClaimed(chunkPos)){

                if(event.getSource() instanceof Player){

                    Player ply = (Player)event.getSource();

                    XFaction owningFaction = XFactionClaim.getClaimedChunkFaction(chunkPos);

                    if(owningFaction == XFactionCommandManager.getPlayerFaction(ply)) {

                        XFactionPermissionData permData = XFactionCommandManager.getPlayerFactionPermissions(ply);

                        if(permData.getInteract()){

                            return;

                        }

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

                    if(owningFaction == XFactionCommandManager.getPlayerFaction(ply)) {

                        XFactionPermissionData permData = XFactionCommandManager.getPlayerFactionPermissions(ply);

                        if(permData.getInteract()){

                            return;

                        }

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
    public void onMessageSent(MessageChannelEvent.Chat event){

        if(event.getSource() instanceof Player){

            Player ply = (Player)event.getSource();
            XFaction xFac = XFactionCommandManager.getPlayerFaction(ply);

            String message = event.getRawMessage().toPlain().replace("&", "\u00a7");

            if(xFac != null && !xFac.getFactionPrefix().equals("")){

                String nicePrefix = xFac.getFactionPrefix().replace("&", "\u00a7");
                event.setMessage(Text.of(nicePrefix + "\u00a7r (" + ply.getName() + ") " + message));

            }
            else{

                event.setMessage(Text.of("(" + ply.getName() + ") " + message));

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

        int xVal = Integer.parseInt(xStr);
        int yVal = Integer.parseInt(yStr);
        int zVal = Integer.parseInt(zStr);

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

}

