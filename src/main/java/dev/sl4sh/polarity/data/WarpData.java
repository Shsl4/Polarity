package dev.sl4sh.polarity.data;

import com.flowpowered.math.vector.Vector3d;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class WarpData {

    @Nonnull
    @Setting(value = "position")
    private Vector3d position = Vector3d.ZERO;

    @Nonnull
    @Setting(value = "worldUniqueID")
    private UUID worldUniqueID;

    public WarpData() {}

    public WarpData(@Nonnull Vector3d position, @Nonnull UUID worldUniqueID) {
        this.position = position;
        this.worldUniqueID = worldUniqueID;
    }

    @Nonnull
    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(@Nonnull Vector3d position) {
        this.position = position;
    }

    public Optional<World> getTargetWorld() {
        return Sponge.getServer().getWorld(worldUniqueID);
    }

    public void setWorldUniqueID(UUID worldUniqueID) {
        this.worldUniqueID = worldUniqueID;
    }
}
