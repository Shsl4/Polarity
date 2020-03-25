package io.sl4sh.xmanager;

import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class XFaction {

    private String factionName;

    private String factionDisplayName;

    private String factionPrefix;

    //Faction's owner player name
    private String factionOwner;

    //Contains faction's members data
    private List<XFactionMemberData> factionMembers;

    //Contains factions's claimed chunks locations
    private List<String> factionClaims = new ArrayList<String>();

    //Contains faction's homes data
    private XManagerLocationData factionHome = new XManagerLocationData();

    //Contains allied factions names
    private List<String> factionAllies;

    //Contains enemy factions names
    private List<String> factionEnemies;

    //Contains invited player names
    private List<String> factionInvites;

    //Contains invited player names
    private List<String> factionAllyInvites;

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

    public void setFactionClaims(List<String> factionClaims){

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

    public List<String> getFactionClaims(){

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

    public XFaction(String factionName, String factionPrefix, String factionDisplayName, String factionOwner, List<XFactionMemberData> factionMembers, List<String> factionClaims,
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

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ " , XUtilities.getStringReplacingModifierChar(getFactionDisplayName()) , TextColors.RESET, TextColors.DARK_GREEN, " Members ============"));

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

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ " , XUtilities.getStringReplacingModifierChar(getFactionDisplayName()), TextColors.RESET, TextColors.DARK_GREEN, " Allies ============"));

        if(getFactionAllies().size() <= 0){

            src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!"));
            return;

        }

        int it = 1;

        for(String alliedFactionName : getFactionAllies()){

            Optional<XFaction> optAlliedFaction = XUtilities.getFactionByName(alliedFactionName);

            if(!optAlliedFaction.isPresent()) { continue; }

            src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , XUtilities.getStringReplacingModifierChar(optAlliedFaction.get().getFactionDisplayName()) , TextColors.RESET , TextColors.GREEN ," | Raw Name: " , TextColors.WHITE , optAlliedFaction.get().getFactionName()));

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