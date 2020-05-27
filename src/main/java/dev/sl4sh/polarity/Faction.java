package dev.sl4sh.polarity;

import dev.sl4sh.polarity.chat.FactionChatChannel;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Identifiable;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class Faction implements Identifiable, Serializable {

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
    private List<FactionMemberData> memberDataList = new ArrayList<>();

    //Contains allied factions names
    @Nonnull
    @Setting(value = "allies")
    private List<UUID> allies = new ArrayList<>();

    //Contains enemy factions UUIDs
    @Nonnull
    @Setting(value = "enemies")
    private List<UUID> enemies = new ArrayList<>();

    @Nonnull
    private final FactionChatChannel factionChannel = new FactionChatChannel(new ArrayList<>());

    public Faction() {}

    public void setName(@Nonnull String name){

        this.name = name;

    }

    public void setOwner(@Nonnull UUID owner){

        this.owner = owner;

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
    public List<FactionMemberData> getMemberDataList(){

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

    public Faction(@Nonnull String name, @Nonnull Player owner){

        this.name = name;
        this.owner = owner.getUniqueId();
        this.prefix = "";
        this.displayName = "";
        this.memberDataList = new ArrayList<>();
        this.allies = new ArrayList<>();
        this.enemies = new ArrayList<>();

        this.memberDataList.add(new FactionMemberData(owner.getUniqueId(), new FactionPermissionData(true, true, true)));

    }

    public boolean isOwner(Player player){

        return getOwner().equals(player.getUniqueId());

    }

    public boolean isFactionAllied(@Nonnull Faction targetFaction){

        return allies.contains(targetFaction.getUniqueId());

    }

    public void listMembers(CommandSource src){

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ " , getDisplayName() , TextColors.RESET, TextColors.DARK_GREEN, " Members ============"));

        List<FactionMemberData> glData = this.getMemberDataList();
        int it = 1;

        for(FactionMemberData mbData : glData){

            if(Utilities.getPlayerByUniqueID(mbData.getPlayerUniqueID()).isPresent()){

                if (mbData.getPlayerUniqueID().equals(getOwner())){

                    src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , Utilities.getPlayerByUniqueID(mbData.getPlayerUniqueID()).get().getName() , TextColors.GREEN , " | " , TextColors.GOLD , "(Owner)"));

                }
                else{

                    src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , Utilities.getPlayerByUniqueID(mbData.getPlayerUniqueID()).get().getName() , TextColors.GREEN , " | " , TextColors.WHITE , "(Member)"));

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

            Optional<Faction> optAlliedFaction = Utilities.getFactionByUniqueID(alliedFactionName);

            if(!optAlliedFaction.isPresent()) { continue; }

            src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , optAlliedFaction.get().getDisplayName() , TextColors.RESET , TextColors.GREEN ," | Raw Name: " , TextColors.WHITE , optAlliedFaction.get().getName()));

            it++;

        }

    }

    public boolean setPermissionDataForPlayer(Player targetPlayer, FactionPermissionData permData){

        for(FactionMemberData mbData : getMemberDataList()){

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
    @Override
    public UUID getUniqueId() {
        return this.uniqueID;
    }

    @Nonnull
    public FactionChatChannel getFactionChannel() {
        return factionChannel;
    }
}
