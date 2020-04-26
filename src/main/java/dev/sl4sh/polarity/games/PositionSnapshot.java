package dev.sl4sh.polarity.games;

import com.flowpowered.math.vector.Vector3d;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;

@ConfigSerializable
public class PositionSnapshot {

    public static class Tags{

        public final static String SPAWN_ANY = "AnySpawn";
        public final static String DEFAULT_SPAWN = "DefaultSpawn";
        public final static String TEAM1_SPAWN = "Team1Spawn";
        public final static String TEAM2_SPAWN = "Team2Spawn";
        public final static String TEAM3_SPAWN = "Team3Spawn";
        public final static String TEAM4_SPAWN = "Team4Spawn";
        public final static String BRICK_SPAWN = "BrickSpawn";
        public final static String IRON_SPAWN = "IronSpawn";
        public final static String GOLD_SPAWN = "GoldSpawn";
        public final static String EMERALD_SPAWN = "EmeraldSpawn";

    }

    @Nonnull
    @Setting(value = "location")
    private final Vector3d location;
    
    @Nonnull
    @Setting(value = "rotation")
    private final Vector3d rotation;
    
    @Nonnull
    @Setting(value = "tag")
    private final String tag;

    public PositionSnapshot(@Nonnull Vector3d location, @Nonnull Vector3d rotation, @Nonnull String tag) {
        this.location = location;
        this.rotation = rotation;
        this.tag = tag;
    }

    public PositionSnapshot() {

        this.tag = "";
        this.location = Vector3d.ZERO;
        this.rotation = Vector3d.ZERO;

    }

    @Nonnull
    public Vector3d getLocation() {
        return location;
    }

    @Nonnull
    public Vector3d getRotation() {
        return rotation;
    }

    @Nonnull
    public String getTag() {
        return tag;
    }

}
