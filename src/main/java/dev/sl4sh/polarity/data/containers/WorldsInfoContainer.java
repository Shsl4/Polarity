package dev.sl4sh.polarity.data.containers;

import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.events.PlayerWarpEvent;
import dev.sl4sh.polarity.Polarity;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ConfigSerializable
public class WorldsInfoContainer implements PolarityContainer<WorldInfo> {

    @Setting(value = "list")
    @Nonnull
    private List<WorldInfo> list = new ArrayList<>();

    @Nonnull
    @Override
    public List<WorldInfo> getList() {
        return list;
    }

    @Override
    public boolean add(@Nonnull WorldInfo object) {
        return list.add(object);
    }

    @Override
    public boolean remove(@Nonnull WorldInfo object) {
        return list.remove(object);
    }

    @Override
    public boolean shouldSave() { return getList().size() > 0; }

    public WorldsInfoContainer() {}

    /**
     * This method fetches or creates an {@link WorldInfo} object for the desired world.
     * @param world The world to get the custom info of
     * @return The fetched or newly created object
     */
    public WorldInfo getOrCreateWorldInfo(World world){

        // Check if an WorldInfo object exists for the input world
        for(WorldInfo worldInfo : getList()){

            if(worldInfo.getWorldUniqueID().equals(world.getUniqueId())){

                return worldInfo;

            }

        }

        // Otherwise, create and return a new one
        WorldInfo worldInfo = new WorldInfo(world);
        list.add(worldInfo);

        return worldInfo;

    }

    /**
     * This method removes a {@link WorldInfo} object for the desired world if existing.
     * @param world The world to get the custom info removed
     * @return Whether the object was removed or not
     */
    public boolean removeWorldInfo(World world){

        return list.removeIf(worldInfo -> worldInfo.getWorldUniqueID().equals(world.getUniqueId()));

    }

    @Listener
    public void onDamageEvent(DamageEntityEvent event){

        WorldInfo worldInfo = getOrCreateWorldInfo(event.getTargetEntity().getWorld());

        // Cancel damage if the dimension is protected or if the target is in a protected chunk
        if(worldInfo.isWorldProtected() || (worldInfo.getWorldProtectedChunks().contains(event.getTargetEntity().getLocation().getChunkPosition()))){

            // Allow damage if causer and targets are players who are both in the forced damage player list.
            if(event.getTargetEntity() instanceof Player){

                Player target = (Player)event.getTargetEntity();

                if(worldInfo.getForcedDamageTakers().contains(target)){

                    if (event.getSource() instanceof Player && worldInfo.getForcedDamageTakers().contains(target)) {

                        event.setCancelled(false);

                    }

                }

            }
            else{

                event.setCancelled(false);

            }

            event.setCancelled(true);

        }

        if(!event.isCancelled() && event.getTargetEntity() instanceof Player){

            Player target = (Player)event.getTargetEntity();

            if(worldInfo.getRecentDamageMap().containsKey(target)){

                worldInfo.getRecentDamageMap().get(target).cancel();

            }

            Task task = Task.builder().delay(10L, TimeUnit.SECONDS).execute(() -> worldInfo.getRecentDamageMap().remove(target)).submit(Polarity.getPolarity());
            worldInfo.getRecentDamageMap().put(target, task);

        }
        
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent event){

        WorldInfo worldInfo = getOrCreateWorldInfo(event.getTargetEntity().getWorld());

        // Cancel interaction if the dimension is protected or if the target (Hanging) is in a protected chunk. This prevents frames form being interacted with.
        if(worldInfo.isWorldProtected() || (worldInfo.getWorldProtectedChunks().contains(event.getTargetEntity().getLocation().getChunkPosition()))){

            // Ignore restrictions if the instigator is an administrator
            if (event.getSource() instanceof Player) {

                if (((Player) event.getSource()).hasPermission("*")) {

                    return;

                }

            }

            if(event.getTargetEntity() instanceof Hanging){

                event.setCancelled(true);

            }

        }

    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event){

        if(event.getTargetBlock().getLocation().isPresent()){

            WorldInfo worldInfo = getOrCreateWorldInfo(event.getTargetBlock().getLocation().get().getExtent());

            // Cancel interaction if the dimension is protected or if the target (container block) is in a protected chunk
            if(worldInfo.isWorldProtected() || (worldInfo.getWorldProtectedChunks().contains(event.getTargetBlock().getLocation().get().getChunkPosition()))){

                // Ignore restrictions if the instigator is an administrator
                if(event.getSource() instanceof Player){

                    if(((Player)event.getSource()).hasPermission("*")) { return; }

                }

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
    public void onBlockBroken(ChangeBlockEvent.Break event){

        // Cancel destruction if the dimension is protected or if the target (block) is in a protected chunk
        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getLocation().isPresent()){

                Location<World> location = snap.getFinal().getLocation().get();
                WorldInfo worldInfo = getOrCreateWorldInfo(location.getExtent());

                if(worldInfo.isWorldProtected() || worldInfo.getWorldProtectedChunks().contains(location.getChunkPosition())){

                    // Ignore restrictions if the instigator is an administrator
                    if(event.getSource() instanceof Player){

                        if(((Player)event.getSource()).hasPermission("*")) { return; }

                    }

                    event.setCancelled(true);

                }

            }

        }

    }

    @Listener
    public void onExplosion(ExplosionEvent.Pre event){

        WorldInfo worldInfo = getOrCreateWorldInfo(event.getExplosion().getLocation().getExtent());

        // Cancel all explosions
        if(worldInfo.isWorldProtected() || worldInfo.getWorldProtectedChunks().contains(event.getExplosion().getLocation().getChunkPosition())){

            event.setCancelled(true);

        }

    }

    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event){

        // Respawn the player to the hub if the warp exists.
        PolarityWarp.warp(event.getTargetEntity(), "Hub", Polarity.getPolarity());

    }

    @Listener
    public void onBlockPlaced(ChangeBlockEvent.Place event) {

        // Cancel placement if the dimension is protected or if the target (block) is in a protected chunk
        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getLocation().isPresent()) {

                WorldInfo worldInfo = getOrCreateWorldInfo(snap.getFinal().getLocation().get().getExtent());

                // Ignore restrictions if the instigator is an administrator
                if (event.getSource() instanceof Player) {

                    if (((Player) event.getSource()).hasPermission("*")) {
                        return;
                    }

                }

                if(worldInfo.isWorldProtected() || worldInfo.getWorldProtectedChunks().contains(snap.getFinal().getLocation().get().getChunkPosition())){

                    event.setCancelled(true);

                }

            }

        }

    }

    @Listener
    public void onPlayerWarp(PlayerWarpEvent.Pre event){

        if(event.getSource() instanceof Player){

            WorldInfo worldInfo = getOrCreateWorldInfo(event.getWorld());

            if(!worldInfo.isGameWorld() && worldInfo.getRecentDamageMap().containsKey(event.getTargetEntity())){

                event.setCancelled(true);
                event.getTargetEntity().sendTitle(Title.builder().actionBar(Text.of(TextColors.DARK_RED, "You must be safe in order to warp")).build());

            }

        }

    }

}
