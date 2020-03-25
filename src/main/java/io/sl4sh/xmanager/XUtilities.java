package io.sl4sh.xmanager;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.data.factions.XFactionContainer;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class XUtilities {

    public static boolean isLocationProtected(Location<World> target){

       for(XManagerLocationData locationData : XManager.getXManager().getConfigData().getServerProtectedChunks()){

            if(locationData.getLocation().equals(target.getChunkPosition().toString()) && locationData.getDimensionName().equals(target.getExtent().getName())) { return true; }

        }

       return false;

    }

    public static boolean isLocationProtected(Location<World> target, MutableInt existingIndex){

        World targetWorld = target.getExtent();

        int it = 0;

        for(XManagerLocationData locationData : XManager.getXManager().getConfigData().getServerProtectedChunks()){

            if(targetWorld.getName().equals(locationData.getDimensionName())){

                if(target.getChunkPosition().toString().equals(locationData.getLocation())){

                    existingIndex.setValue(it);
                    return true;

                }

            }

            it++;

        }

        return false;

    }

    static public Boolean doesFactionExist(String factionName) {

        XFactionContainer factionsContainer = XManager.getXManager().getFactionsContainer();

        if(factionsContainer != null) {

            for(XFaction faction : factionsContainer.getFactionList()){

                if(faction.getFactionName().equals(factionName)){

                    return true;

                }

            }

        }

        return false;

    }

    static public Optional<XFaction> getFactionByName(String factionName){

        XFactionContainer factionsContainer = XManager.getXManager().getFactionsContainer();

        if(factionsContainer != null) {

            for(XFaction faction : factionsContainer.getFactionList()){

                if(faction.getFactionName().equals(factionName)){

                    return Optional.of(faction);

                }

            }

        }

        return Optional.empty();

    }


    static public Optional<XFactionPermissionData> getPlayerFactionPermissions(Player ply) {

        if (getPlayerFaction(ply).isPresent()) {

            for (XFactionMemberData mbData : getPlayerFaction(ply).get().getFactionMembers()) {

                if (mbData.getPlayerName().equals(ply.getName())) {

                    return Optional.ofNullable(mbData.getPermissions());

                }

            }

        }

        return Optional.empty();

    }

    static public Optional<XFactionMemberData> getMemberDataForPlayer(Player ply) {

        if (getPlayerFaction(ply).isPresent()) {

            for (XFactionMemberData mbData : getPlayerFaction(ply).get().getFactionMembers()) {

                if (mbData.getPlayerName().equals(ply.getName())) {

                    return Optional.of(mbData);

                }

            }

        }

        return Optional.empty();

    }


    static public Optional<XFaction> getPlayerFaction(Player player){

        XFactionContainer fContainer = XManager.getXManager().getFactionsContainer();

        if(fContainer != null){

            for(XFaction faction : fContainer.getFactionList()){

                for(XFactionMemberData memberData : faction.getFactionMembers()){

                    if(memberData.getPlayerName().equals(player.getName())){

                        return Optional.of(faction);

                    }

                }

            }

        }

        return Optional.empty();

    }

    public static Optional<Player> getPlayerByName(String PlayerName){

        return Sponge.getServer().getPlayer(PlayerName);

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


    // Replace "&" with "\u00a7" in the faction's prefix or display name, otherwise the color doesn't apply in game
    // Example: "&cPrefix" becomes (*Red colored*) "Prefix"
    public static String getStringReplacingModifierChar(String str){

        return str.replace("&", "\u00a7");

    }

    // https://github.com/Zerthick/PlayerShopsRPG/blob/master/src/main/java/io/github/zerthick/playershopsrpg/utils/config/serializers/ItemStackHOCONSerializer.java
    public static String serializeSnapShot(ItemStackSnapshot snapshot) throws IOException {

        DataContainer container = snapshot.toContainer();
        StringWriter stringWriter = new StringWriter();

        DataFormats.HOCON.writeTo(stringWriter, container);

        return stringWriter.toString();
    }

    // https://github.com/Zerthick/PlayerShopsRPG/blob/master/src/main/java/io/github/zerthick/playershopsrpg/utils/config/serializers/ItemStackHOCONSerializer.java
    public static ItemStackSnapshot deserializeSnapShot(String filePath) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        DataContainer container = DataFormats.HOCON.read(content);
        return ItemStack.builder().fromContainer(container).build().createSnapshot();

    }

}
