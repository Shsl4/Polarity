package dev.sl4sh.polarity.data.containers;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.events.PlayerChangeDimensionEvent;
import dev.sl4sh.polarity.events.PlayerWarpEvent;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ConfigSerializable
public class WorldsInfoContainer implements PolarityContainer<WorldInfo> {

    @Setting(value = "list")
    @Nonnull
    private final List<WorldInfo> list = new ArrayList<>();

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
    public WorldInfo getOrCreate(World world){

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

    public WorldInfo createFrom(World world, World from){

        for(WorldInfo worldInfo : getList()){

            if(worldInfo.getWorldUniqueID().equals(world.getUniqueId())){

                return worldInfo;

            }

        }

        WorldInfo info = getOrCreate(from);
        WorldInfo copy = new WorldInfo(world, info.getWorldProtectedChunks(), info.getPositionSnapshots(), info.isWorldProtected());
        this.add(copy);
        return copy;

    }


    /**
     * This method removes a {@link WorldInfo} object for the desired world if existing.
     * @param world The world to get the custom info removed
     * @return Whether the object was removed or not
     */
    public boolean removeWorldInfo(World world){

        return list.removeIf(worldInfo -> worldInfo.getWorldUniqueID().equals(world.getUniqueId()));

    }

    public boolean removeWorldInfo(UUID worldID){

        for(WorldInfo info : getList()){

            if (info.getWorldUniqueID().equals(worldID)){

                return list.remove(info);

            }

        }

        return false;

    }

    @Listener
    public void onDimensionChanged(PlayerChangeDimensionEvent.Post event){

        WorldInfo toInfo = Utilities.getOrCreateWorldInfo(event.getToWorld());
        WorldInfo fromInfo = Utilities.getOrCreateWorldInfo(event.getFromWorld());

        fromInfo.getMessageChannel().removeMember(event.getTargetEntity());
        toInfo.getMessageChannel().addMember(event.getTargetEntity());

    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event){

        WorldInfo info = Utilities.getOrCreateWorldInfo(event.getTargetEntity().getWorld());
        info.getMessageChannel().addMember(event.getTargetEntity());

    }

    @Listener
    public void onDisconnect(ClientConnectionEvent.Disconnect event){

        WorldInfo info = Utilities.getOrCreateWorldInfo(event.getTargetEntity().getWorld());
        info.getMessageChannel().removeMember(event.getTargetEntity());

    }

    @Listener(beforeModifications = true, order= Order.FIRST)
    public void onDamageEvent(DamageEntityEvent event){

        if(event.getSource() instanceof Player && ((Player)event.getSource()).hasPermission("*")){

            event.setCancelled(false);
            return;

        }

        WorldInfo worldInfo = getOrCreate(event.getTargetEntity().getWorld());

        if(event.getTargetEntity() instanceof Player){

            if(worldInfo.isWorldProtected()){

                event.setCancelled(true);
                return;

            }

            Player target = (Player)event.getTargetEntity();

            if(worldInfo.getRecentDamageMap().containsKey(target)){

                worldInfo.getRecentDamageMap().get(target).cancel();

            }

            Task task = Task.builder().delay(10L, TimeUnit.SECONDS).execute(() -> worldInfo.getRecentDamageMap().remove(target)).submit(Polarity.getPolarity());
            worldInfo.getRecentDamageMap().put(target, task);

        }

    }

    @Listener(beforeModifications = true, order= Order.FIRST)
    public void onEntityInteract(InteractEntityEvent event){

        WorldInfo worldInfo = getOrCreate(event.getTargetEntity().getWorld());

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

    @Listener(beforeModifications = true, order= Order.FIRST)
    public void onBlockInteract(InteractBlockEvent event){

        if(event.getTargetBlock().getLocation().isPresent()){

            WorldInfo worldInfo = getOrCreate(event.getTargetBlock().getLocation().get().getExtent());

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

    @Listener(beforeModifications = true, order= Order.FIRST)
    public void onBlockBroken(ChangeBlockEvent.Break event){

        // Cancel destruction if the dimension is protected or if the target (block) is in a protected chunk
        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getLocation().isPresent()){

                Location<World> location = snap.getFinal().getLocation().get();
                WorldInfo worldInfo = getOrCreate(location.getExtent());

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

    @Listener(beforeModifications = true, order= Order.FIRST)
    public void onExplosion(ExplosionEvent.Pre event){

        WorldInfo worldInfo = getOrCreate(event.getExplosion().getLocation().getExtent());

        // Cancel all explosions
        if(worldInfo.isWorldProtected() || worldInfo.getWorldProtectedChunks().contains(event.getExplosion().getLocation().getChunkPosition())){

            event.setCancelled(true);

        }

    }

    @Listener(beforeModifications = true, order= Order.FIRST)
    public void onBlockPlaced(ChangeBlockEvent.Place event) {

        // Cancel placement if the dimension is protected or if the target (block) is in a protected chunk
        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getLocation().isPresent()) {

                WorldInfo worldInfo = getOrCreate(snap.getFinal().getLocation().get().getExtent());

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

            WorldInfo worldInfo = getOrCreate(event.getWorld());

            if((!worldInfo.isGameWorld() && !worldInfo.isWorldProtected()) && worldInfo.getRecentDamageMap().containsKey(event.getTargetEntity())){

                event.setCancelled(true);
                event.getTargetEntity().sendTitle(Title.builder().actionBar(Text.of(TextColors.DARK_RED, "You must be safe in order to warp")).build());

            }

        }

    }

}
