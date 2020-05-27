package dev.sl4sh.polarity.data.registration.beddata;

import dev.sl4sh.polarity.Polarity;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;
import java.util.UUID;

public class BedData extends AbstractData<BedData, ImmutableBedData> {

    private UUID player;

    public BedData(){

        registerGettersAndSetters();

    }

    public BedData(UUID player){

        this();
        this.player = player;

    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public Value<UUID> players(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.BedData.PLAYER, player);

    }

    @Override
    protected void registerGettersAndSetters() {

        registerFieldGetter(Polarity.Keys.BedData.PLAYER, this::getPlayer);
        registerFieldSetter(Polarity.Keys.BedData.PLAYER, this::setPlayer);
        registerKeyValue(Polarity.Keys.BedData.PLAYER, this::players);

    }

    @Override
    public Optional<BedData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.empty();
    }

    @Override
    public Optional<BedData> from(DataContainer container) {
        return Optional.empty();
    }

    @Override
    public BedData copy() {
        return new BedData(this.player);
    }

    @Override
    public ImmutableBedData asImmutable() {
        return new ImmutableBedData(this.player);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

}
