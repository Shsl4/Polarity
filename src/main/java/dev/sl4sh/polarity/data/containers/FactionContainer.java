package dev.sl4sh.polarity.data.containers;

import com.flowpowered.math.vector.Vector3i;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class FactionContainer implements PolarityContainer<Faction> {

    @Setting(value = "list")
    @Nonnull
    private List<Faction> list = new ArrayList<>();

    @Nonnull
    @Override
    public List<Faction> getList() {
        return list;
    }

    @Override
    public boolean add(@Nonnull Faction object) {
        return list.add(object);
    }

    @Override
    public boolean remove(@Nonnull Faction object) {
        return list.remove(object);
    }

    @Override
    public boolean shouldSave() { return getList().size() > 0; }

    public FactionContainer() {}

    @Listener
    public void onBlockPlaced(ChangeBlockEvent.Place event){

        // Ignore all restrictions if player has * permission (Admin)
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getLocation().isPresent())
            {

                World world = snap.getFinal().getLocation().get().getExtent();
                Vector3i chunkPosition = snap.getFinal().getLocation().get().getChunkPosition();

                event.setCancelled(checkCancelInteraction(world, chunkPosition, event.getSource()));

            }

        }

    }

    @Listener
    public void onBlockBroken(ChangeBlockEvent.Break event){

        // Ignore all restrictions if player has * permission (Admin)
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getLocation().isPresent())
            {

                World world = snap.getFinal().getLocation().get().getExtent();
                Vector3i chunkPosition = snap.getFinal().getLocation().get().getChunkPosition();

                event.setCancelled(checkCancelInteraction(world, chunkPosition, event.getSource()));

            }

        }

    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event){

        // Ignore all restrictions if player has * permission (Admin)
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        if(event.getTargetBlock().getLocation().isPresent()){

            World world = event.getTargetBlock().getLocation().get().getExtent();
            Vector3i chunkPosition = event.getTargetBlock().getLocation().get().getChunkPosition();

            event.setCancelled(checkCancelInteraction(world, chunkPosition, event.getSource()));

        }

    }

    @Listener
    public void onExplosion(ExplosionEvent.Pre event){

        World world = event.getExplosion().getWorld();
        Vector3i chunkPos = event.getExplosion().getLocation().getChunkPosition();
        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(world);

        if(worldInfo.getClaimedChunkFaction(chunkPos).isPresent()){

            event.setCancelled(true);

        }

    }

    private static boolean checkCancelInteraction(World world, Vector3i chunkPosition, Object eventSource){

        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(world);

        Optional<UUID> optFactionID = worldInfo.getClaimedChunkFaction(chunkPosition);

        if(!optFactionID.isPresent()) { return false; }

        Optional<Faction> optOwningFaction = Utilities.getFactionByUniqueID(optFactionID.get());

        if(!optOwningFaction.isPresent()) { return false; }

        if(eventSource instanceof Player){

            Player inst = (Player)eventSource;
            Faction owningFaction = optOwningFaction.get();
            Optional<Faction> instFaction = Utilities.getPlayerFaction(inst);

            if(instFaction.isPresent()){

                if(instFaction.get().getUniqueId().equals(owningFaction.getUniqueId())){

                    Optional<FactionPermissionData> optPermData = Utilities.getPlayerFactionPermissions(inst);

                    if(optPermData.isPresent() && optPermData.get().getInteract()){

                        return false;

                    }

                }

                if(owningFaction.getAllies().contains(instFaction.get().getUniqueId())){

                    return false;

                }

            }

            inst.sendMessage(Text.of(TextColors.RED, "[Factions] | You are not allowed to do that. Chunk owned by ", owningFaction.getDisplayName()));

        }

        return true;

    }

}
