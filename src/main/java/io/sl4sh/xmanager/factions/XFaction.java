package io.sl4sh.xmanager.factions;

import io.sl4sh.xmanager.tablist.XTabListManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class XFaction {

    private String factionName;

    private String factionDisplayName;

    private String factionPrefix;

    //Faction's owner player name
    private String factionOwner;

    //Contains faction's members data
    private List<XFactionMemberData> factionMembers;

    //Contains factions's claimed chunks locations
    @NonNull
    private List<String> factionClaims = new ArrayList<String>();

    //Contains faction's homes data
    private List<XFactionHomeData> factionHomes;

    //Contains allied factions names
    private List<XFactionAllyData> factionAllies;

    //Contains enemy factions names
    private List<String> factionEnemies;

    //Contains invited player names
    private List<String> factionInvites;

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

    public void setFactionHomes(List<XFactionHomeData> factionHomes){

        this.factionHomes = factionHomes;

    }

    public void setFactionAllies(List<XFactionAllyData> factionAllies){

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

    public List<XFactionHomeData> getFactionHomes(){

        return this.factionHomes;

    }

    public List<XFactionAllyData> getFactionAllies(){

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
                    List<XFactionHomeData> factionHomes, List<XFactionAllyData> factionAllies, List<String> factionEnemies, List<String> factionInvites){

        this.factionName = factionName;
        this.factionPrefix = factionPrefix;
        this.factionDisplayName = factionDisplayName;
        this.factionOwner = factionOwner;
        this.factionMembers = factionMembers;
        this.factionClaims = factionClaims;
        this.factionHomes = factionHomes;
        this.factionAllies = factionAllies;
        this.factionEnemies = factionEnemies;
        this.factionInvites = factionInvites;

        XTabListManager.refreshTabLists();

    }

    public void listMembers(CommandSource src){

        String modDPName = getFactionDisplayName();
        modDPName = modDPName.replace("&", "\u00a7");

        src.sendMessage(Text.of("\u00a72============ " + modDPName + "\u00a7r \u00a72Members ============"));

        List<XFactionMemberData> glData = this.getFactionMembers();
        int it = 0;

        for(XFactionMemberData mbData : glData){

            switch (mbData.getPermissions().getRank()){

                case Owner:

                    src.sendMessage(Text.of("\u00a7a#" + (it+1) + ". \u00a7f" + mbData.playerName + " \u00a7a| \u00a76(" + mbData.getPermissions().getRank() + ")"));
                    break;

                case Adventurer:

                    src.sendMessage(Text.of("\u00a7a#" + (it+1) + ". \u00a7f" + mbData.playerName + " \u00a7a| \u00a7b(" + mbData.getPermissions().getRank() + ")"));
                    break;

                case Superior:

                    src.sendMessage(Text.of("\u00a7a#" + (it+1) + ". \u00a7f" + mbData.playerName + " \u00a7a| \u00a75(" + mbData.getPermissions().getRank() + ")"));
                    break;

                case Member:

                    src.sendMessage(Text.of("\u00a7a#" + (it+1) + ". \u00a7f" + mbData.playerName + " \u00a7a| \u00a7f(" + mbData.getPermissions().getRank() + ")"));
                    break;

            }

            it++;

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
}
