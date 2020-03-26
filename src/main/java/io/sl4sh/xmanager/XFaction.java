package io.sl4sh.xmanager;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.ResettableBuilder;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ConfigSerializable
public class XFaction {

    @Nonnull
    @Setting(value = "factionName")
    private String factionName = "name";

    @Nonnull
    @Setting(value = "factionDisplayName")
    private String factionDisplayName = "";

    @Nonnull
    @Setting(value = "factionPrefix")
    private String factionPrefix = "";

    //Faction's owner player name
    @Nonnull
    @Setting(value = "factionOwner")
    private String factionOwner = "owner";

    //Contains faction's members data
    @Nonnull
    @Setting(value = "factionMembers")
    private List<XFactionMemberData> factionMembers = new ArrayList<>();

    //Contains factions's claimed chunks locations
    @Nonnull
    @Setting(value = "factionClaims")
    private List<XManagerLocationData> factionClaims = new ArrayList<>();

    //Contains faction's homes data
    @Nonnull
    @Setting(value = "factionHome")
    private XManagerLocationData factionHome = new XManagerLocationData();

    //Contains allied factions names
    @Nonnull
    @Setting(value = "factionAllies")
    private List<String> factionAllies = new ArrayList<>();

    //Contains enemy factions names
    @Nonnull
    @Setting(value = "factionEnemies")
    private List<String> factionEnemies = new ArrayList<>();

    //Contains invited player names
    @Nonnull
    @Setting(value = "factionInvites")
    private List<String> factionInvites = new ArrayList<>();

    //Contains invited player names
    @Nonnull
    @Setting(value = "factionAllyInvites")
    private List<String> factionAllyInvites = new ArrayList<>();

    public XFaction(){

    }

    public void setFactionName(String factionName){

        this.factionName = factionName;

    }

    public void setFactionOwner(String factionOwner){

        this.factionOwner = factionOwner;

    }

    public void setFactionMembers(List<XFactionMemberData> factionMembers){

        this.factionMembers = factionMembers;

    }

    public void setFactionClaims(List<XManagerLocationData> factionClaims){

        this.factionClaims = factionClaims;

    }

    public void setFactionHome(XManagerLocationData factionHome){

        this.factionHome = factionHome;

    }

    public void setFactionAllies(List<String> factionAllies){

        this.factionAllies = factionAllies;

    }

    public void setFactionEnemies(List<String> factionEnemies){

        this.factionEnemies = factionEnemies;

    }

    public void setFactionInvites(List<String> factionInvites){

        this.factionInvites = factionInvites;

    }

    public String getFactionName(){

        return this.factionName;

    }

    public String getFactionOwner(){

        return this.factionOwner;

    }

    public List<XFactionMemberData> getFactionMembers(){

        return this.factionMembers;

    }

    public List<XManagerLocationData> getFactionClaims(){

        return this.factionClaims;

    }

    public XManagerLocationData getFactionHome(){

        return this.factionHome;

    }

    public List<String> getFactionAllies(){

        return this.factionAllies;

    }

    public List<String> getFactionEnemies(){

        return this.factionEnemies;

    }

    public List<String> getFactionInvites(){

        return this.factionInvites;

    }

    public XFaction getValue(){

        return this;

    }

    public XFaction(String factionName, String factionPrefix, String factionDisplayName, String factionOwner, List<XFactionMemberData> factionMembers, List<XManagerLocationData> factionClaims,
                    XManagerLocationData factionHome, List<String> factionAllies, List<String> factionEnemies, List<String> factionInvites, List<String> factionAllyInvites){

        this.factionName = factionName;
        this.factionPrefix = factionPrefix;
        this.factionDisplayName = factionDisplayName;
        this.factionOwner = factionOwner;
        this.factionMembers = factionMembers;
        this.factionClaims = factionClaims;
        this.factionHome = factionHome;
        this.factionAllies = factionAllies;
        this.factionEnemies = factionEnemies;
        this.factionInvites = factionInvites;
        this.factionAllyInvites = factionAllyInvites;

        XTabListManager.refreshTabLists();

    }

    public boolean isOwner(String playerName){

        return getFactionOwner().equals(playerName);

    }

    public boolean isFactionAllied(@Nonnull XFaction targetFaction){

        return factionAllies.contains(targetFaction.getFactionName());

    }

    public void listMembers(CommandSource src){

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ " , getFactionDisplayName() , TextColors.RESET, TextColors.DARK_GREEN, " Members ============"));

        List<XFactionMemberData> glData = this.getFactionMembers();
        int it = 1;

        for(XFactionMemberData mbData : glData){

            if (mbData.getPlayerName().equals(getFactionOwner())){

                src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , mbData.playerName , TextColors.GREEN , " | " , TextColors.GOLD , "(Owner)"));

            }
            else{

                src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , mbData.playerName , TextColors.GREEN , " | " , TextColors.WHITE , "(Member)"));

            }

            it++;

        }

    }

    public void listAllies(@Nonnull CommandSource src){

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ " , getFactionDisplayName(), TextColors.RESET, TextColors.DARK_GREEN, " Allies ============"));

        if(getFactionAllies().size() <= 0){

            src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!"));
            return;

        }

        int it = 1;

        for(String alliedFactionName : getFactionAllies()){

            Optional<XFaction> optAlliedFaction = XUtilities.getFactionByName(alliedFactionName);

            if(!optAlliedFaction.isPresent()) { continue; }

            src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , optAlliedFaction.get().getFactionDisplayName() , TextColors.RESET , TextColors.GREEN ," | Raw Name: " , TextColors.WHITE , optAlliedFaction.get().getFactionName()));

            it++;

        }

    }

    public boolean setPermissionDataForPlayer(Player targetPlayer, XFactionPermissionData permData){

        for(XFactionMemberData mbData : getFactionMembers()){

            if(mbData.getPlayerName().equals(targetPlayer.getName())){

                mbData.permissions = permData;
                return true;

            }

        }

        return false;

    }

    public boolean isClaimed(String worldName, Vector3i location){

        for(XManagerLocationData locationData : getFactionClaims()){

            if(locationData.getDimensionName().equals(worldName) && locationData.getLocation().equals(location.toString())){

                return true;

            }

        }

        return false;

    }


    public boolean removeClaim(String worldName, Vector3i location){

        for(XManagerLocationData locationData : getFactionClaims()){

            if(locationData.getDimensionName().equals(worldName) && locationData.getLocation().equals(location.toString())){

                getFactionClaims().remove(locationData);
                return true;

            }

        }

        return false;

    }

    public String getFactionPrefix() {
        return factionPrefix;
    }

    public void setFactionPrefix(String factionPrefix) {

        this.factionPrefix = factionPrefix;

    }

    public String getFactionDisplayName() {
        return factionDisplayName.equals("") ? getFactionName() : factionDisplayName;
    }

    public void setFactionDisplayName(String factionDisplayName) {

        this.factionDisplayName = factionDisplayName;
    }

    public List<String> getFactionAllyInvites() {
        return factionAllyInvites;
    }

    public void setFactionAllyInvites(List<String> factionAllyInvites) {
        this.factionAllyInvites = factionAllyInvites;
    }


}
