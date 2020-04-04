package io.sl4sh.xmanager;

import com.flowpowered.math.vector.Vector3d;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Identifiable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class XFaction implements Identifiable {

    @Nonnull
    @Setting(value = "uniqueID")
    private UUID uniqueID = UUID.randomUUID();

    @Nonnull
    @Setting(value = "name")
    private String name = "name";

    @Nonnull
    @Setting(value = "displayName")
    private String displayName = "";

    @Nonnull
    @Setting(value = "prefix")
    private String prefix = "";

    //Faction's owner player name
    @Nonnull
    @Setting(value = "owner")
    private UUID owner = UUID.randomUUID();

    //Contains faction's members data
    @Nonnull
    @Setting(value = "memberDataList")
    private List<XFactionMemberData> memberDataList = new ArrayList<>();

    //Contains allied factions names
    @Nonnull
    @Setting(value = "allies")
    private List<UUID> allies = new ArrayList<>();

    //Contains enemy factions UUIDs
    @Nonnull
    @Setting(value = "enemies")
    private List<UUID> enemies = new ArrayList<>();

    //Contains invited player UUIDs
    @Nonnull
    @Setting(value = "playerInvites")
    private List<UUID> playerInvites = new ArrayList<>();

    //Contains allied factions UUIDs
    @Nonnull
    @Setting(value = "allyInvites")
    private List<UUID> allyInvites = new ArrayList<>();

    public XFaction() {}

    public void setName(@Nonnull String name){

        this.name = name;

    }

    public void setOwner(@Nonnull UUID owner){

        this.owner = owner;

    }

    public void setMemberDataList(@Nonnull List<XFactionMemberData> memberDataList){

        this.memberDataList = memberDataList;

    }

    public void setAllies(@Nonnull List<UUID> allies){

        this.allies = allies;

    }

    public void setEnemies(@Nonnull List<UUID> enemies){

        this.enemies = enemies;

    }

    public void setPlayerInvites(@Nonnull List<UUID> playerInvites){

        this.playerInvites = playerInvites;

    }

    @Nonnull
    public String getName(){

        return this.name;

    }

    @Nonnull
    public UUID getOwner(){

        return this.owner;

    }

    @Nonnull
    public List<XFactionMemberData> getMemberDataList(){

        return this.memberDataList;

    }

    @Nonnull
    public List<UUID> getAllies(){

        return this.allies;

    }

    @Nonnull
    public List<UUID> getEnemies(){

        return this.enemies;

    }

    @Nonnull
    public List<UUID> getPlayerInvites(){

        return this.playerInvites;

    }


    public XFaction(@Nonnull String name, @Nonnull Player owner){

        this.name = name;
        this.owner = owner.getUniqueId();
        this.prefix = "";
        this.displayName = "";
        this.memberDataList = new ArrayList<>();
        this.allies = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.playerInvites = new ArrayList<>();
        this.allyInvites = new ArrayList<>();

        this.memberDataList.add(new XFactionMemberData(owner.getUniqueId(), new XFactionPermissionData(true, true, true)));

        XTabListManager.refreshTabLists();

    }

    public boolean isOwner(Player player){

        return getOwner().equals(player.getUniqueId());

    }

    public boolean isFactionAllied(@Nonnull XFaction targetFaction){

        return allies.contains(targetFaction.getUniqueId());

    }

    public void listMembers(CommandSource src){

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ " , getDisplayName() , TextColors.RESET, TextColors.DARK_GREEN, " Members ============"));

        List<XFactionMemberData> glData = this.getMemberDataList();
        int it = 1;

        for(XFactionMemberData mbData : glData){

            if(Sponge.getServer().getPlayer(mbData.getPlayerUniqueID()).isPresent()){

                if (mbData.getPlayerUniqueID().equals(getOwner())){

                    src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , Sponge.getServer().getPlayer(mbData.getPlayerUniqueID()).get().getName() , TextColors.GREEN , " | " , TextColors.GOLD , "(Owner)"));

                }
                else{

                    src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , Sponge.getServer().getPlayer(mbData.getPlayerUniqueID()).get().getName() , TextColors.GREEN , " | " , TextColors.WHITE , "(Member)"));

                }

            }

            it++;

        }

    }

    public void listAllies(@Nonnull CommandSource src){

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ " , getDisplayName(), TextColors.RESET, TextColors.DARK_GREEN, " Allies ============"));

        if(getAllies().size() <= 0){

            src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!"));
            return;

        }

        int it = 1;

        for(UUID alliedFactionName : getAllies()){

            Optional<XFaction> optAlliedFaction = XUtilities.getFactionByUniqueID(alliedFactionName);

            if(!optAlliedFaction.isPresent()) { continue; }

            src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , optAlliedFaction.get().getDisplayName() , TextColors.RESET , TextColors.GREEN ," | Raw Name: " , TextColors.WHITE , optAlliedFaction.get().getName()));

            it++;

        }

    }

    public boolean setPermissionDataForPlayer(Player targetPlayer, XFactionPermissionData permData){

        for(XFactionMemberData mbData : getMemberDataList()){

            if(mbData.getPlayerUniqueID().equals(targetPlayer.getUniqueId())){

                mbData.permissions = permData;
                return true;

            }

        }

        return false;

    }

    @Nonnull
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(@Nonnull String prefix) {

        this.prefix = prefix;

    }

    @Nonnull
    public String getDisplayName() {
        return displayName.equals("") ? getName() : displayName;
    }

    public void setDisplayName(@Nonnull String displayName) {

        this.displayName = displayName;
    }

    @Nonnull
    public List<UUID> getAllyInvites() {
        return allyInvites;
    }

    public void setAllyInvites(@Nonnull List<UUID> allyInvites) {
        this.allyInvites = allyInvites;
    }

    @Nonnull
    @Override
    public UUID getUniqueId() {
        return this.uniqueID;
    }
}
