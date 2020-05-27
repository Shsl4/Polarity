package dev.sl4sh.polarity.games;

import com.flowpowered.math.vector.Vector3i;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;

@ConfigSerializable
public class PositionSnapshot {

    public static class Tags{

        public final static String ANY_SPAWN = "AnySpawn";
        public final static String DEFAULT_SPAWN = "DefaultSpawn";
        public final static String SPECTATOR_SPAWN = "SpectatorSpawn";
        public final static String WHITE_SPAWN = "WhiteSpawn";
        public final static String GOLD_SPAWN = "GoldSpawn";
        public final static String PURPLE_SPAWN = "PurpleSpawn";
        public final static String CYAN_SPAWN = "CyanSpawn";
        public final static String ORE_SPAWN = "OreSpawn";
        public final static String DIAMOND_SPAWN = "DiamondSpawn";
        public final static String RUSH_SHOP = "RushShop";

    }

    @Nonnull
    @Setting(value = "location")
    private final Vector3i location;
    
    @Nonnull
    @Setting(value = "rotation")
    private final Vector3i rotation;
    
    @Nonnull
    @Setting(value = "tag")
    private final String tag;

    public PositionSnapshot(@Nonnull Vector3i location, @Nonnull Vector3i rotation, @Nonnull String tag) {
        this.location = location;
        this.rotation = rotation;
        this.tag = tag;
    }

    public PositionSnapshot() {

        this.tag = "";
        this.location = Vector3i.ZERO;
        this.rotation = Vector3i.ZERO;

    }

    @Nonnull
    public Vector3i getLocation() {
        return location;
    }

    @Nonnull
    public Vector3i getRotation() {
        return rotation;
    }

    @Nonnull
    public String getTag() {
        return tag;
    }

}
