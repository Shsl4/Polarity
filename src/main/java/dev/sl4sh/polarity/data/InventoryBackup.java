package dev.sl4sh.polarity.data;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class InventoryBackup implements Serializable {

    @Nonnull
    @Setting(value = "targetPlayer")
    private UUID targetPlayerID = UUID.randomUUID();

    @Nonnull
    @Setting(value = "snapshots")
    private List<ItemStackSnapshot> snapshots = new ArrayList<>();

    public Optional<Player> getTargetPlayer() { return Sponge.getServer().getPlayer(targetPlayerID); }

    @Nonnull
    public UUID getTargetPlayerID() { return targetPlayerID; }

    @Nonnull
    public List<ItemStackSnapshot> getSnapshots() { return snapshots; }

    public InventoryBackup() {}

    public InventoryBackup(Player player, @Nonnull List<ItemStackSnapshot> snapshots) { this.targetPlayerID = player.getUniqueId(); this.snapshots = snapshots; }

}
