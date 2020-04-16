package dev.sl4sh.polarity.data;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import dev.sl4sh.polarity.Polarity;
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
    private Map<String, Vector3d> warps = new LinkedHashMap<>();

    @Setting(value = "isDimensionProtected")
    private Boolean isDimensionProtected = false;

    @Nonnull
    @Setting(value = "worldFactionHomes")
    private Map<UUID, Vector3d> worldFactionHomes = new LinkedHashMap<>();

    @Nonnull
    @Setting(value = "worldFactionClaims")
    private Map<UUID, List<Vector3i>> worldFactionClaims = new LinkedHashMap<>();

    @Setting(value = "targetWorld")
    private UUID worldUniqueID;

    @Setting(value = "isGameWorld")
    private boolean isGameWorld;

    // This list contains players who must take damage, even if they are in a protected area
    // An example of usage case could be when players are playing a MiniGame in the hub.
    @Nonnull
    private List<Player> forcedDamageTakers = new ArrayList<>();

    @Nonnull
    private Map<Player, Task> recentDamageMap = new LinkedHashMap<>();

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

    @Nonnull
    public UUID addClaim(@Nonnull Vector3i chunkPos, @Nonnull UUID factionID){

        if(isClaimed(chunkPos)) { return getClaimedChunkFaction(chunkPos).get(); }

        List<Vector3i> claims;

        if (worldFactionClaims.get(factionID) != null) {

            claims = worldFactionClaims.get(factionID);

        } else {

            claims = new ArrayList<>();

        }

        claims.add(chunkPos);
        worldFactionClaims.put(factionID, claims);

        return factionID;

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

    public WorldInfo() {}

    public WorldInfo(World targetWorld) {

        this.worldUniqueID = targetWorld.getUniqueId();

    }

    public WorldInfo(World targetWorld, @Nonnull List<Vector3i> worldProtectedChunks, @Nonnull Map<String, Vector3d> warps, boolean isDimensionProtected) {

        this(targetWorld);
        this.worldProtectedChunks = worldProtectedChunks;
        this.warps = warps;
        this.isDimensionProtected = isDimensionProtected;

    }

    @Nonnull
    public List<Vector3i> getWorldProtectedChunks() {
        return worldProtectedChunks;
    }

    public void setWorldProtectedChunks(@Nonnull List<Vector3i> worldProtectedChunks) {
        this.worldProtectedChunks = worldProtectedChunks;
    }

    @Nonnull
    public Map<String, Vector3d> getWarps() {
        return warps;
    }

    public void setWarps(@Nonnull  Map<String, Vector3d> warps) {
        this.warps = warps;
    }

    public List<String> getWarpNames(){

        return new ArrayList<>(warps.keySet());

    }

    public Optional<World> getTargetWorld() {
        return worldUniqueID == null ? Optional.empty() : Sponge.getServer().getWorld(worldUniqueID);
    }

    public void setTargetWorld(World targetWorld) {
        this.worldUniqueID = targetWorld.getUniqueId();
    }

    public void setDimensionProtected(boolean dimensionProtected) {
        isDimensionProtected = dimensionProtected;
    }

    public boolean isWorldProtected() {
        return this.isDimensionProtected;
    }
    
    @Nonnull
    public Map<UUID, Vector3d> getWorldFactionHomes() {
        return worldFactionHomes;
    }

    public void setWorldFactionHomes(@Nonnull Map<UUID, Vector3d> worldFactionHomes) {
        this.worldFactionHomes = worldFactionHomes;
    }

    @Nonnull
    public List<Player> getForcedDamageTakers() {
        return forcedDamageTakers;
    }

    @Nonnull
    public Map<Player, Task> getRecentDamageMap() {
        return recentDamageMap;
    }

    public void setRecentDamageMap(@Nonnull Map<Player, Task> recentDamageMap) {
        this.recentDamageMap = recentDamageMap;
    }

    public void setGameWorld(boolean gameWorld) {
        isGameWorld = gameWorld;
    }

    public boolean isGameWorld() {
        return isGameWorld;
    }
}
