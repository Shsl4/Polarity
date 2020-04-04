package io.sl4sh.xmanager.data;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.*;

@ConfigSerializable
public class XWorldInfo {

    @Nonnull
    @Setting(value = "worldProtectedChunks")
    private List<Vector3i> worldProtectedChunks = new ArrayList<>();

    @Nonnull
    @Setting(value = "warps")
    private Map<String, XWarpData> warps = new LinkedHashMap<>();

    @Setting(value = "isDimensionProtected")
    private Boolean isDimensionProtected = false;

    @Nonnull
    @Setting(value = "worldFactionHomes")
    private Map<UUID, Vector3d> worldFactionHomes = new LinkedHashMap<>();

    @Nonnull
    @Setting(value = "worldFactionClaims")
    private Map<Vector3i, UUID> worldFactionClaims = new LinkedHashMap<>();

    @Setting(value = "targetWorld")
    private UUID worldUniqueID;

    // This list contains players who must take damage, even if they are in a protected area
    // An example of usage case could be when players are playing a MiniGame in the hub.
    @Nonnull
    private List<Player> forcedDamageTakers = new ArrayList<>();

    public boolean isClaimed(Vector3i chunkLocation){

        return getClaimedChunkFaction(chunkLocation).isPresent();

    }

    public Optional<UUID> getClaimedChunkFaction(Vector3i chunkPosition){

        return Optional.ofNullable(worldFactionClaims.get(chunkPosition));

    }

    public List<Vector3i> getFactionClaimedChunks(UUID factionID){

        List<Vector3i> chunkList = new ArrayList<>();

        for(Vector3i chunkPos : worldFactionClaims.keySet()){

            if(worldFactionClaims.get(chunkPos).equals(factionID)){

                chunkList.add(chunkPos);

            }

        }

        return chunkList;

    }

    public boolean removeClaim(Vector3i chunkPos, UUID factionID){

        if(worldFactionClaims.get(chunkPos) != null && worldFactionClaims.get(chunkPos).equals(factionID)){

            return worldFactionClaims.remove(chunkPos) != null;

        }

        return false;

    }

    @Nonnull
    public UUID addClaim(@Nonnull Vector3i chunkPos, @Nonnull UUID factionID){

        if(isClaimed(chunkPos)) { return getClaimedChunkFaction(chunkPos).get(); }

        worldFactionClaims.put(chunkPos, factionID);

        return factionID;

    }

    public Optional<Vector3d> getFactionHome(UUID factionID){

        return Optional.ofNullable(worldFactionHomes.get(factionID));

    }

    public void setFactionHome(UUID factionID, Vector3d chunkPos){

        for(XWorldInfo worldInfo : XManager.getWorldsInfo()){

            if(worldInfo.getFactionHome(factionID).isPresent()){

                worldInfo.getWorldFactionHomes().remove(factionID);

            }

        }

        this.worldFactionHomes.put(factionID, chunkPos);

    }

    public XWorldInfo() {

        Sponge.getEventManager().registerListeners(XManager.getXManager(), this);

    }

    public XWorldInfo(World targetWorld) {

        this();
        this.worldUniqueID = targetWorld.getUniqueId();

    }

    public XWorldInfo(World targetWorld, @Nonnull List<Vector3i> worldProtectedChunks, @Nonnull Map<String, XWarpData> warps, boolean isDimensionProtected) {

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
    public  Map<String, XWarpData> getWarps() {
        return warps;
    }

    public void setWarps(@Nonnull  Map<String, XWarpData> warps) {
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

    @Listener
    public void onDamageEvent(DamageEntityEvent event){

        // Ignore restrictions if the instigator is an administrator
        if (event.getSource() instanceof Player) {

            if (((Player) event.getSource()).hasPermission("*")) {

                return;

            }

        }

        if (event.getTargetEntity().getWorld().getUniqueId().equals(worldUniqueID)){

            // Cancel damage if the dimension is protected or if the target is in a protected chunk
            if(isDimensionProtected || (worldProtectedChunks.contains(event.getTargetEntity().getLocation().getChunkPosition()))){


                // Allow damage if causer and targets are players who are both in the forced damage player list.
                if(event.getTargetEntity() instanceof Player && forcedDamageTakers.contains((Player)event.getTargetEntity())){

                    if (event.getSource() instanceof Player && forcedDamageTakers.contains((Player)event.getSource())) {

                        event.setCancelled(false);
                        return;

                    }

                }

                event.setCancelled(true);

            }

        }

    }

    @Listener
    public void onEntityInteract(InteractEntityEvent event){

        // Ignore restrictions if the instigator is an administrator
        if (event.getSource() instanceof Player) {

            if (((Player) event.getSource()).hasPermission("*")) {

                return;

            }

        }

        if (event.getTargetEntity().getWorld().getUniqueId().equals(worldUniqueID)){

            // Cancel interaction if the dimension is protected or if the target (Hanging) is in a protected chunk. This prevents frames form being interacted with.
            if(isDimensionProtected || (worldProtectedChunks.contains(event.getTargetEntity().getLocation().getChunkPosition()))){

                if(event.getTargetEntity() instanceof Hanging){

                    event.setCancelled(true);

                }

            }

        }

    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event){

        // Ignore restrictions if the instigator is an administrator
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        if(event.getTargetBlock().getWorldUniqueId().equals(worldUniqueID)){

            // Cancel interaction if the dimension is protected or if the target (container block) is in a protected chunk
            if(isDimensionProtected || (event.getTargetBlock().getLocation().isPresent() && worldProtectedChunks.contains(event.getTargetBlock().getLocation().get().getChunkPosition()))){

                // Allow the use of doors, buttons and ender chests only in protected areas
                if(!event.getTargetBlock().getExtendedState().getType().toString().contains("door") &&
                        !event.getTargetBlock().getExtendedState().getType().toString().contains("button") &&
                        !event.getTargetBlock().getExtendedState().getType().equals(BlockTypes.ENDER_CHEST)){

                    event.setCancelled(true);

                }

            }

        }

    }

    @Listener
    public void preBlockBroken(ChangeBlockEvent.Break.Pre event){

        // Ignore restrictions if the instigator is an administrator
        if (event.getSource() instanceof Player) {

            if (((Player) event.getSource()).hasPermission("*")) {

                return;

            }

        }

        // Cancel destruction if the dimension is protected or if the target (block) is in a protected chunk
        for(Location<World> location : event.getLocations()){

            if(location.getExtent().getUniqueId().equals(worldUniqueID)){

                if(isDimensionProtected || worldProtectedChunks.contains(location.getChunkPosition())){

                    event.setCancelled(true);

                }

            }

        }

    }

    @Listener
    public void onExplosion(ExplosionEvent.Pre event){

        // Cancel all explosions
        if(isWorldProtected() || worldProtectedChunks.contains(event.getExplosion().getLocation().getChunkPosition())){

            event.setCancelled(true);

        }

    }

    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event){

        // Respawn the player to the hub if the warp exists.
        Optional<XWarpData> warpData = XUtilities.getWarpDataByName("Hub");

        if(warpData.isPresent()){

            if(warpData.get().getTargetWorld().isPresent()){

                event.setToTransform(new Transform<>(warpData.get().getTargetWorld().get(), warpData.get().getPosition()));

            }

        }

    }

    @Listener
    public void onBlockPlaced(ChangeBlockEvent.Place event) {

        // Ignore restrictions if the instigator is an administrator
        if (event.getSource() instanceof Player) {

            if (((Player) event.getSource()).hasPermission("*")) {
                return;
            }

        }

        // Cancel placement if the dimension is protected or if the target (block) is in a protected chunk
        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getWorldUniqueId().equals(worldUniqueID)){

                if(isDimensionProtected || (snap.getFinal().getLocation().isPresent() && worldProtectedChunks.contains(snap.getFinal().getLocation().get().getChunkPosition()))){

                    event.setCancelled(true);

                }

            }

        }

    }

    @Nonnull
    public List<Player> getForcedDamageTakers() {
        return forcedDamageTakers;
    }

}
