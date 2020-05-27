package dev.sl4sh.polarity.data.registration.beddata;

import dev.sl4sh.polarity.Polarity;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.UUID;

public class ImmutableBedData extends AbstractImmutableData<ImmutableBedData, BedData> {

    private UUID player;

    public ImmutableBedData(){

        registerGetters();

    }

    public ImmutableBedData(UUID player){

        this();
        this.player = player;

    }

    public UUID getPlayer() {
        return player;
    }

    public ImmutableValue<UUID> players(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.BedData.PLAYER, player).asImmutable();

    }

    @Override
    protected void registerGetters() {

        registerFieldGetter(Polarity.Keys.BedData.PLAYER, this::getPlayer);
        registerKeyValue(Polarity.Keys.BedData.PLAYER, this::players);

    }

    @Override
    public BedData asMutable() {
        return new BedData(this.player);
    }


    @Override
    public int getContentVersion() {
        return 0;
    }

}
