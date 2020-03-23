package io.sl4sh.xmanager.factions;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.commands.XFactionCommandManager;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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
    private Location<World> factionHome;

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

    public void setFactionHome(Location<World> factionHome){

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

    public Location<World> getFactionHome(){

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
                    Location<World> factionHome, List<String> factionAllies, List<String> factionEnemies, List<String> factionInvites, List<String> factionAllyInvites){

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

        String modDPName = XManager.getStringReplacingModifierChar(getFactionDisplayName());

        src.sendMessage(Text.of("\u00a72============ " + modDPName + "\u00a7r \u00a72Members ============"));

        List<XFactionMemberData> glData = this.getFactionMembers();
        int it = 0;

        for(XFactionMemberData mbData : glData){

            if (mbData.getPlayerName().equals(getFactionOwner())){

                src.sendMessage(Text.of("\u00a7a#" + (it+1) + ". \u00a7f" + mbData.playerName + " \u00a7a| \u00a76(Owner)"));

            }
            else{

                src.sendMessage(Text.of("\u00a7a#" + (it+1) + ". \u00a7f" + mbData.playerName + " \u00a7a| \u00a7f(Member)"));

            }

            it++;

        }

    }

    public void listAllies(@Nonnull CommandSource src){

        String modDPName = XManager.getStringReplacingModifierChar(getFactionDisplayName());

        src.sendMessage(Text.of("\u00a72============ " + modDPName + "\u00a7r \u00a72Allies ============"));

        if(getFactionAllies().size() <= 0){

            src.sendMessage(Text.of("\u00a7aNothing to see here... Yet!"));
            return;

        }

        int it = 1;

        for(String alliedFactionName : getFactionAllies()){

            Optional<XFaction> optAlliedFaction = XFactionCommandManager.getFactionByName(alliedFactionName);

            if(!optAlliedFaction.isPresent()) { continue; }

            src.sendMessage(Text.of("\u00a7a#" + it + ". \u00a7f" + optAlliedFaction.get().getFactionDisplayName() + "\u00a7a | Raw Name: \u00a7f" + optAlliedFaction.get().getFactionName()));

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
        return factionDisplayName.equals("") ? XManager.getStringReplacingModifierChar(getFactionName()) : XManager.getStringReplacingModifierChar(factionDisplayName);
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
