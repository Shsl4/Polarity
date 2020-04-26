package dev.sl4sh.polarity.games.arena;

import dev.sl4sh.polarity.games.GamePreset;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Random;

@ConfigSerializable
public class ArenaPreset implements GamePreset {

    public static ArenaPreset getRandomArenaStaticPreset(){

        return Arrays.asList(StaticPresets.LEATHER_PRESET, StaticPresets.IRON_PRESET, StaticPresets.GOLD_PRESET, StaticPresets.DIAMOND_PRESET).get(new Random().nextInt(4));

    }

    public static class StaticPresets{

        public static ArenaPreset LEATHER_PRESET = new ArenaPreset(ItemTypes.LEATHER_HELMET, ItemTypes.LEATHER_CHESTPLATE, ItemTypes.LEATHER_LEGGINGS, ItemTypes.LEATHER_BOOTS, ItemTypes.STONE_SWORD, ItemTypes.BOW);
        public static ArenaPreset IRON_PRESET = new ArenaPreset(ItemTypes.IRON_HELMET, ItemTypes.IRON_CHESTPLATE, ItemTypes.IRON_LEGGINGS, ItemTypes.IRON_BOOTS, ItemTypes.IRON_SWORD, ItemTypes.BOW);
        public static ArenaPreset GOLD_PRESET = new ArenaPreset(ItemTypes.GOLDEN_HELMET, ItemTypes.GOLDEN_CHESTPLATE, ItemTypes.GOLDEN_LEGGINGS, ItemTypes.GOLDEN_BOOTS, ItemTypes.GOLDEN_SWORD, ItemTypes.BOW);
        public static ArenaPreset DIAMOND_PRESET = new ArenaPreset(ItemTypes.DIAMOND_HELMET, ItemTypes.DIAMOND_CHESTPLATE, ItemTypes.DIAMOND_LEGGINGS, ItemTypes.DIAMOND_BOOTS, ItemTypes.DIAMOND_SWORD, ItemTypes.BOW);

    }

    @Nonnull
    @Setting("helmetType")
    public final ItemType helmetType;

    @Nonnull
    @Setting("chestplateType")
    public final ItemType chestplateType;

    @Nonnull
    @Setting("leggingsType")
    public final ItemType leggingsType;

    @Nonnull
    @Setting("bootsType")
    public final ItemType bootsType;

    @Nonnull
    @Setting("helmetType")
    public final ItemType swordType;

    @Nonnull
    @Setting("helmetType")
    public final ItemType bowType;

    public ArenaPreset(@Nonnull ItemType helmetType, @Nonnull ItemType chestplateType, @Nonnull ItemType leggingsType, @Nonnull ItemType bootsType, @Nonnull ItemType swordType, @Nonnull ItemType bowType) {
        this.helmetType = helmetType;
        this.chestplateType = chestplateType;
        this.leggingsType = leggingsType;
        this.bootsType = bootsType;
        this.swordType = swordType;
        this.bowType = bowType;
    }

}
