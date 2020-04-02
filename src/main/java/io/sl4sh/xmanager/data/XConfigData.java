package io.sl4sh.xmanager.data;

import com.flowpowered.math.vector.Vector3d;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.commands.XManagerWarp;
import io.sl4sh.xmanager.economy.XShopProfile;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import noppes.npcs.api.constants.ItemType;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.*;

@ConfigSerializable
public class XConfigData {

    @Nonnull
    @Setting(value = "serverProtectedChunks")
    private List<XManagerLocationData> serverProtectedChunks = new ArrayList<>();

    @Nonnull
    @Setting(value = "hubData")
    private XManagerLocationData hubData = new XManagerLocationData();

    @Nonnull
    @Setting(value = "warpsData")
    private Map<String, XManagerLocationData> warpsData = new LinkedHashMap<>();

    @Nonnull
    @Setting(value = "protectedDimensions")
    private List<String> protectedDimensions = new ArrayList<>();

    @Nonnull
    @Setting(value = "initialSpawnLocation")
    private XManagerLocationData initialSpawnLocation = new XManagerLocationData();

    public XConfigData() {

    }

    public void addProtectedDimension(World world){

        protectedDimensions.add(world.getName());

    }

    public List<XManagerLocationData> getServerProtectedChunks() {
        return serverProtectedChunks;
    }

    public void setServerProtectedChunks(List<XManagerLocationData> serverProtectedChunks) {
        this.serverProtectedChunks = serverProtectedChunks;
    }

    public XManagerLocationData getHubData() {
        return hubData;
    }

    public void setHubData(XManagerLocationData hubData) {
        this.hubData = hubData;
    }

    @Nonnull
    public List<String> getProtectedDimensions() {
        return protectedDimensions;
    }

    public void setProtectedDimensions(@Nonnull List<String> protectedDimensions) {
        this.protectedDimensions = protectedDimensions;
    }

    @Listener
    public void onDamageEvent(DamageEntityEvent event){

        if (event.getSource() instanceof Player) {

            if (((Player) event.getSource()).hasPermission("*")) {

                return;

            }

        }

        if(XManager.getConfigData().getProtectedDimensions().contains(event.getTargetEntity().getWorld().getName())){

            event.setCancelled(true);

        }

    }

    @Listener
    public void onEntityInteract(InteractEntityEvent event){

        if (event.getSource() instanceof Player) {

            if (((Player) event.getSource()).hasPermission("*")) {

                return;

            }

        }

        if(XManager.getConfigData().getProtectedDimensions().contains(event.getTargetEntity().getWorld().getName())){

            if(event.getTargetEntity() instanceof Hanging){

                event.setCancelled(true);

            }

        }

    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event){

        if(event.getSource() instanceof Player){

            if(((Player)event.getSource()).hasPermission("*")) { return; }

        }

        if(event.getTargetBlock().getState().getType().equals(BlockTypes.WOODEN_DOOR) || event.getTargetBlock().getState().getType().equals(BlockTypes.TRAPDOOR)){

            return;

        }

        if(event.getTargetBlock().getLocation().isPresent() && XManager.getConfigData().getProtectedDimensions().contains(event.getTargetBlock().getLocation().get().getExtent().getName())){

            event.setCancelled(true);

        }

    }

    @Listener
    public void preBlockBroken(ChangeBlockEvent.Break.Pre event){

        if (event.getSource() instanceof Player) {

            if (((Player) event.getSource()).hasPermission("*")) {

                return;

            }

        }

        for(Location<World> location : event.getLocations()){

            if(XManager.getConfigData().getProtectedDimensions().contains(location.getExtent().getName())){

                event.setCancelled(true);

            }

        }

    }


    @Listener
    public void onBlockPlaced(ChangeBlockEvent.Place event) {

        if (event.getSource() instanceof Player) {

            if (((Player) event.getSource()).hasPermission("*")) {
                return;
            }

        }

        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getLocation().isPresent() && XManager.getConfigData().getProtectedDimensions().contains(snap.getFinal().getLocation().get().getExtent().getName())){

                event.setCancelled(true);

            }

        }

    }

    @Listener
    public void onPlayerJoined(ClientConnectionEvent.Join event){

        if (!event.getTargetEntity().hasPlayedBefore()) {

            if(!XManager.getConfigData().getInitialSpawnLocation().getLocation().equals("") && !XManager.getConfigData().getInitialSpawnLocation().getDimensionName().equals("")){

                if(Sponge.getServer().getWorld(XManager.getConfigData().getInitialSpawnLocation().getDimensionName()).isPresent()){

                    World world = Sponge.getServer().getWorld(XManager.getConfigData().getInitialSpawnLocation().getDimensionName()).get();
                    Vector3d pos = XUtilities.getStringAsVector3d(XManager.getConfigData().getInitialSpawnLocation().getLocation());

                    event.getTargetEntity().setLocation(new Location<World>(world, pos));

                }

            }

        }

    }

    @Nonnull
    public XManagerLocationData getInitialSpawnLocation() {
        return initialSpawnLocation;
    }

    public void setInitialSpawnLocation(@Nonnull XManagerLocationData initialSpawnLocation) {
        this.initialSpawnLocation = initialSpawnLocation;
    }

    @Nonnull
    public Map<String, XManagerLocationData> getWarpsData() {
        return warpsData;
    }

    public void setWarpsData(@Nonnull Map<String, XManagerLocationData> warpsData) {
        this.warpsData = warpsData;
    }

    public List<String> getWarpNames(){

        return new ArrayList<>(warpsData.keySet());

    }

}
