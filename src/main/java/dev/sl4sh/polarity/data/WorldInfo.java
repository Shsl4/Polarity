package dev.sl4sh.polarity.data;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.chat.WorldChannel;
import dev.sl4sh.polarity.games.PositionSnapshot;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.*;

@ConfigSerializable
public class WorldInfo implements Serializable {

    @Nonnull
    @Setting(value = "worldProtectedChunks")
    private List<Vector3i> worldProtectedChunks = new ArrayList<>();

    @Nonnull
    @Setting(value = "warps")
    private final Map<String, Vector3d> warps = new LinkedHashMap<>();

    @Setting(value = "isDimensionProtected")
    private Boolean isWorldProtected = false;

    @Nonnull
    @Setting(value = "worldFactionHomes")
    private final Map<UUID, Vector3d> worldFactionHomes = new LinkedHashMap<>();

    @Nonnull
    @Setting(value = "worldFactionClaims")
    private final Map<UUID, List<Vector3i>> worldFactionClaims = new LinkedHashMap<>();

    @Setting(value = "targetWorld")
    private UUID worldUniqueID;

    @Setting(value = "isGameWorld")
    private boolean isGameWorld;

    @Nonnull
    private final Map<Player, Task> recentDamageMap = new LinkedHashMap<>();

    @Nonnull
    @Setting(value = "positionSnapshots")
    private List<PositionSnapshot> positionSnapshots = new ArrayList<>();

    @Nonnull
    public List<PositionSnapshot> getPositionSnapshots() {
        return positionSnapshots;
    }

    @Nonnull
    public Map<UUID, List<Vector3i>> getWorldFactionClaims() {
        return worldFactionClaims;
    }

    @Nonnull
    private final WorldChannel messageChannel = new WorldChannel(new ArrayList<>(getTargetWorld().isPresent() ? getTargetWorld().get().getPlayers() : new ArrayList<>()));

    @Nonnull
    public WorldChannel getMessageChannel() { return this.messageChannel; }

    public boolean isClaimed(Vector3i chunkLocation){

        return getClaimedChunkFaction(chunkLocation).isPresent();

    }

    public Optional<UUID> getClaimedChunkFaction(Vector3i chunkPosition){

        for(UUID factionID : worldFactionClaims.keySet()){

            if(worldFactionClaims.get(factionID) != null && worldFactionClaims.get(factionID).contains(chunkPosition)){

                return Optional.of(factionID);

            }

        }

        return Optional.empty();

    }

    public List<Vector3i> getFactionClaimedChunks(UUID factionID){

        for(UUID uuid : worldFactionClaims.keySet()){

            if(uuid.equals(factionID)){

                return worldFactionClaims.get(uuid) != null ? worldFactionClaims.get(uuid) : new ArrayList<>();

            }

        }

        return new ArrayList<>();

    }

    public boolean removeClaim(Vector3i chunkPos, UUID factionID){

        if(worldFactionClaims.get(factionID) != null && worldFactionClaims.get(factionID).contains(chunkPos)){

            return worldFactionClaims.get(factionID).remove(chunkPos);

        }

        return false;

    }

    public UUID getWorldUniqueID(){

        return worldUniqueID;

    }

    public void addClaim(@Nonnull Vector3i chunkPos, @Nonnull UUID factionID){

        if(isClaimed(chunkPos)) { return; }

        List<Vector3i> claims;

        if (worldFactionClaims.get(factionID) != null) {

            claims = worldFactionClaims.get(factionID);

        } else {

            claims = new ArrayList<>();

        }

        claims.add(chunkPos);
        worldFactionClaims.put(factionID, claims);

    }

    public Optional<Vector3d> getFactionHome(UUID factionID){

        return Optional.ofNullable(worldFactionHomes.get(factionID));

    }

    public void setFactionHome(UUID factionID, Vector3d chunkPos){

        for(WorldInfo worldInfo : Polarity.getWorldsInfo().getList()){

            if(worldInfo.getFactionHome(factionID).isPresent()){

                worldInfo.getWorldFactionHomes().remove(factionID);

            }

        }

        this.worldFactionHomes.put(factionID, chunkPos);

    }

    @Deprecated
    public WorldInfo() {}

    public void addPositionSnapshot(Vector3d location, Vector3d rotation, String tag){

        positionSnapshots.add(new PositionSnapshot(location.toInt(), rotation.toInt(), tag));

    }

    public WorldInfo(World targetWorld) {

        this.worldUniqueID = targetWorld.getUniqueId();

    }

    public WorldInfo(World targetWorld, @Nonnull List<Vector3i> worldProtectedChunks, @Nonnull List<PositionSnapshot> positionSnapshots, boolean isWorldProtected) {

        this(targetWorld);
        this.worldProtectedChunks = worldProtectedChunks;
        this.positionSnapshots = positionSnapshots;
        this.isWorldProtected = isWorldProtected;

    }

    @Nonnull
    public List<Vector3i> getWorldProtectedChunks() {
        return worldProtectedChunks;
    }

    @Nonnull
    public Map<String, Vector3d> getWarps() {
        return warps;
    }

    public List<String> getWarpNames(){

        return new ArrayList<>(warps.keySet());

    }

    public Optional<World> getTargetWorld() {
        return worldUniqueID == null ? Optional.empty() : Sponge.getServer().getWorld(worldUniqueID);
    }

    public void setDimensionProtected(boolean dimensionProtected) {
        isWorldProtected = dimensionProtected;
    }

    public boolean isWorldProtected() {
        return this.isWorldProtected;
    }
    
    @Nonnull
    public Map<UUID, Vector3d> getWorldFactionHomes() {
        return worldFactionHomes;
    }

    @Nonnull
    public Map<Player, Task> getRecentDamageMap() {
        return recentDamageMap;
    }

    public void setIsGameWorld(boolean gameWorld) {
        isGameWorld = gameWorld;
    }

    public boolean isGameWorld() { return isGameWorld; }

}
