package io.sl4sh.xmanager;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.economy.accounts.XFactionAccount;
import io.sl4sh.xmanager.tablist.XTabListManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
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

    @Setting(value = "factionAccount")
    private XFactionAccount factionAccount;

    public XFaction() {}

    public void setFactionName(@Nonnull String factionName){

        this.factionName = factionName;

    }

    public void setFactionOwner(@Nonnull String factionOwner){

        this.factionOwner = factionOwner;

    }

    public void setFactionMembers(@Nonnull List<XFactionMemberData> factionMembers){

        this.factionMembers = factionMembers;

    }

    public void setFactionClaims(@Nonnull List<XManagerLocationData> factionClaims){

        this.factionClaims = factionClaims;

    }

    public void setFactionHome(@Nonnull XManagerLocationData factionHome){

        this.factionHome = factionHome;

    }

    public void setFactionAllies(@Nonnull List<String> factionAllies){

        this.factionAllies = factionAllies;

    }

    public void setFactionEnemies(@Nonnull List<String> factionEnemies){

        this.factionEnemies = factionEnemies;

    }

    public void setFactionInvites(@Nonnull List<String> factionInvites){

        this.factionInvites = factionInvites;

    }

    @Nonnull
    public String getFactionName(){

        return this.factionName;

    }

    @Nonnull
    public String getFactionOwner(){

        return this.factionOwner;

    }

    @Nonnull
    public List<XFactionMemberData> getFactionMembers(){

        return this.factionMembers;

    }

    @Nonnull
    public List<XManagerLocationData> getFactionClaims(){

        return this.factionClaims;

    }

    @Nonnull
    public XManagerLocationData getFactionHome(){

        return this.factionHome;

    }

    @Nonnull
    public List<String> getFactionAllies(){

        return this.factionAllies;

    }

    @Nonnull
    public List<String> getFactionEnemies(){

        return this.factionEnemies;

    }

    @Nonnull
    public List<String> getFactionInvites(){

        return this.factionInvites;

    }

    public XFaction getValue(){

        return this;

    }

    public XFaction(@Nonnull String factionName, @Nonnull Player factionOwner){

        this.factionName = factionName;
        this.factionOwner = factionOwner.getName();
        this.factionPrefix = "";
        this.factionDisplayName = "";
        this.factionMembers = new ArrayList<>();
        this.factionClaims = new ArrayList<>();
        this.factionHome = new XManagerLocationData();
        this.factionAllies = new ArrayList<>();
        this.factionEnemies = new ArrayList<>();
        this.factionInvites = new ArrayList<>();
        this.factionAllyInvites = new ArrayList<>();
        this.factionAccount = null;

        this.factionMembers.add(new XFactionMemberData(factionOwner.getName(), new XFactionPermissionData(true, true, true)));

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


    public void removeClaim(String worldName, Vector3i location){

        for(XManagerLocationData locationData : getFactionClaims()){

            if(locationData.getDimensionName().equals(worldName) && locationData.getLocation().equals(location.toString())){

                getFactionClaims().remove(locationData);
                return;

            }

        }

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


    public Optional<Account> getFactionAccount() {
        return factionAccount == null ? Optional.empty() : Optional.of(factionAccount);
    }

    public void setFactionAccount(XFactionAccount factionAccount) {
        this.factionAccount = factionAccount;
    }
}
