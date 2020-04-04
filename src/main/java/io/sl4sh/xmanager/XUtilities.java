package io.sl4sh.xmanager;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.data.XWarpData;
import io.sl4sh.xmanager.data.XWorldInfo;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.*;

public class XUtilities {

    public static XWorldInfo getOrCreateWorldInfo(World world){

        for(XWorldInfo worldInfo : XManager.getWorldsInfo()){

            if(worldInfo.getTargetWorld().isPresent() && worldInfo.getTargetWorld().get().equals(world)){

                return worldInfo;

            }

        }

        XWorldInfo worldInfo = new XWorldInfo(world);
        XManager.getWorldsInfo().add(worldInfo);

        return worldInfo;

    }

    public static Optional<XWarpData> getWarpDataByName(String warpName){

        for(XWorldInfo worldInfo : XManager.getWorldsInfo()){

            if(worldInfo.getWarps().get(warpName) != null){

                return Optional.of(worldInfo.getWarps().get(warpName));

            }

        }

        return Optional.empty();

    }

    public static boolean isLocationProtected(Location<World> target){

        XWorldInfo worldInfo = XUtilities.getOrCreateWorldInfo(target.getExtent());

        return worldInfo.isWorldProtected() || worldInfo.getWorldProtectedChunks().contains(target.getChunkPosition());

    }

    @Nonnull
    public static List<String> getExistingFactionsNames(){

        List<String> returnList = new ArrayList<>();

        for(XFaction faction : XManager.getFactions()){

            returnList.add(faction.getName());

        }

        return returnList;

    }


    public static Optional<XFaction> getFactionByUniqueID(UUID factionID){

        for(XFaction faction : XManager.getFactions()){

            if(faction.getUniqueId().equals(factionID)){

                return Optional.of(faction);

            }

        }

        return Optional.empty();

    }

    public static Optional<WorldServer> getSpongeWorldToServerWorld(World world){


        for(IWorld iWorld : NpcAPI.Instance().getIWorlds()){

            if(iWorld.getName().equals(world.getName())){

                return Optional.of(iWorld.getMCWorld());

            }

        }

        return Optional.empty();

    }

    public static Optional<World> getFactionHomeWorld(UUID factionID){

        for(XWorldInfo worldInfo : XManager.getWorldsInfo()){

            if(worldInfo.getFactionHome(factionID).isPresent()){

                return worldInfo.getTargetWorld();

            }

        }

        return Optional.empty();

    }

    static public Boolean doesFactionExistByUniqueID(UUID factionName) {

        List<XFaction> factionsContainer = XManager.getFactions();

        for(XFaction faction : factionsContainer){

            if(faction.getUniqueId().equals(factionName)){

                return true;

            }

        }

        return false;

    }

    static public Boolean doesFactionExistByName(String factionName) {

        List<XFaction> factionsContainer = XManager.getFactions();

        for(XFaction faction : factionsContainer){

            if(faction.getName().equals(factionName)){

                return true;

            }

        }

        return false;

    }

    static public List<Vector3i> getFactionClaimsInWorld(UUID factionUUID, World world){

        XWorldInfo worldInfo = getOrCreateWorldInfo(world);
        return worldInfo.getFactionClaimedChunks(factionUUID);

    }

    static public Map<Vector3i, World> getAllFactionClaims(UUID factionUUID){

        Map<Vector3i, World> returnMap = new LinkedHashMap<>();

        for(XWorldInfo worldInfo : XManager.getWorldsInfo()){

            if(worldInfo.getTargetWorld().isPresent()){

                for(Vector3i chunkPos : worldInfo.getFactionClaimedChunks(factionUUID)){

                    returnMap.put(chunkPos, worldInfo.getTargetWorld().get());

                }

            }

        }

        return returnMap;

    }

    static public Optional<XFactionPermissionData> getPlayerFactionPermissions(Player ply) {

        if (getPlayerFaction(ply).isPresent()) {

            for (XFactionMemberData mbData : getPlayerFaction(ply).get().getMemberDataList()) {

                if (mbData.getPlayerUniqueID().equals(ply.getUniqueId())) {

                    return Optional.ofNullable(mbData.getPermissions());

                }

            }

        }

        return Optional.empty();

    }

    static public Optional<XFactionMemberData> getMemberDataForPlayer(Player ply) {

        if (getPlayerFaction(ply).isPresent()) {

            for (XFactionMemberData mbData : getPlayerFaction(ply).get().getMemberDataList()) {

                if (mbData.getPlayerUniqueID().equals(ply.getUniqueId())) {

                    return Optional.of(mbData);

                }

            }

        }

        return Optional.empty();

    }

    public static Optional<XFaction> getFactionByName(String factionName){

        for(XFaction faction : XManager.getFactions()){

            if(faction.getName().equals(factionName)){

                return Optional.of(faction);

            }

        }

        return Optional.empty();

    }


    static public Optional<XFaction> getPlayerFaction(Player player){

        List<XFaction> factionsContainer = XManager.getFactions();

        for(XFaction faction : factionsContainer){

            for(XFactionMemberData memberData : faction.getMemberDataList()){

                if(memberData.getPlayerUniqueID().equals(player.getUniqueId())){

                    return Optional.of(faction);

                }

            }

        }

        return Optional.empty();

    }

    public static Optional<Player> getPlayerByName(String PlayerName){

        return Sponge.getServer().getPlayer(PlayerName);

    }

    public static Optional<Player> getPlayerByUniqueID(UUID uuid){

        return Sponge.getServer().getPlayer(uuid);

    }

    public static Vector3i getStringAsVector3i(String vectorString){

        if(vectorString.length() <= 1) { return Vector3i.ZERO; }

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

        if(vectorString.length() <= 1) { return Vector3d.ZERO; }

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

        int symSep = input.indexOf("\u00a7");

        while(symSep != -1){

            strBld.deleteCharAt(symSep);
            strBld.deleteCharAt(symSep);
            symSep = strBld.toString().indexOf("\u00a7");

        }

        return strBld.toString();

    }

}
