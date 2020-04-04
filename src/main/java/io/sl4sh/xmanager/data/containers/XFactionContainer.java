package io.sl4sh.xmanager.data.containers;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.commands.factions.XFactionsClaim;
import io.sl4sh.xmanager.data.XWorldInfo;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class XFactionContainer {

    @Nonnull
    @Setting(value = "factionsList")
    public List<XFaction> factionsList = new ArrayList<>();

    @Nonnull
    public List<XFaction> getFactionsList(){

        return this.factionsList;

    }

    public void setFactionsList(@Nonnull List<XFaction> factionsList){

        this.factionsList = factionsList;

    }

    public void add(XFaction faction){

        this.factionsList.add(faction);

    }

    public XFactionContainer(){

    }

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

                event.setCancelled(this.checkCancelInteraction(world, chunkPosition, event.getSource()));

            }

        }

    }

    @Listener
    public void preBlockBroken(ChangeBlockEvent.Break.Pre event){

        // Ignore all restrictions if player has * permission (Admin)
        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        for(Location<World> location : event.getLocations()){

            World world = location.getExtent();
            Vector3i chunkPosition = location.getChunkPosition();

            event.setCancelled(this.checkCancelInteraction(world, chunkPosition, event.getSource()));

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

            event.setCancelled(this.checkCancelInteraction(world, chunkPosition, event.getSource()));

        }

    }

    @Listener
    public void onExplosion(ExplosionEvent.Pre event){

        World world = event.getExplosion().getWorld();
        Vector3i chunkPos = event.getExplosion().getLocation().getChunkPosition();
        XWorldInfo worldInfo = XUtilities.getOrCreateWorldInfo(world);

        if(worldInfo.getClaimedChunkFaction(chunkPos).isPresent()){

            event.setCancelled(true);

        }

    }

    private boolean checkCancelInteraction(World world, Vector3i chunkPosition, Object eventSource){

        XWorldInfo worldInfo = XUtilities.getOrCreateWorldInfo(world);

        Optional<UUID> optFactionID = worldInfo.getClaimedChunkFaction(chunkPosition);

        if(!optFactionID.isPresent()) { return false; }

        Optional<XFaction> optOwningFaction = XUtilities.getFactionByUniqueID(optFactionID.get());

        if(!optOwningFaction.isPresent()) { return false; }

        if(eventSource instanceof Player){

            Player inst = (Player)eventSource;
            XFaction owningFaction = optOwningFaction.get();
            Optional<XFaction> instFaction = XUtilities.getPlayerFaction(inst);

            if(instFaction.isPresent()){

                if(instFaction.get().getUniqueId().equals(owningFaction.getUniqueId())){

                    Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(inst);

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
