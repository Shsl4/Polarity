package dev.sl4sh.polarity.data.registration.player;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.enums.ChannelTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class TransientPlayerData extends AbstractData<TransientPlayerData, ImmutableTransientPlayerData> {

    private ChannelTypes preferredChannel;

    public ChannelTypes getPreferredChannel() {
        return preferredChannel;
    }

    public Value<ChannelTypes> preferredChannel(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.PREFERRED_CHANNEL, this.preferredChannel, ChannelTypes.WORLD_CHANNEL);

    }

    public TransientPlayerData(ChannelTypes channel){
        this.preferredChannel = channel;
        registerGettersAndSetters();
    }

    public TransientPlayerData(){
        this(null);
    }

    public void setPreferredChannel(ChannelTypes preferredChannel) {
        this.preferredChannel = preferredChannel;
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(Polarity.Keys.PREFERRED_CHANNEL, this::getPreferredChannel);
        registerFieldSetter(Polarity.Keys.PREFERRED_CHANNEL, this::setPreferredChannel);
        registerKeyValue(Polarity.Keys.PREFERRED_CHANNEL, this::preferredChannel);

    }

    @Override
    public Optional<TransientPlayerData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.empty();
    }

    @Override
    public Optional<TransientPlayerData> from(DataContainer container) {
        return Optional.empty();
    }

    @Override
    public TransientPlayerData copy() {
        return new TransientPlayerData(this.preferredChannel);
    }

    @Override
    public ImmutableTransientPlayerData asImmutable() {
        return new ImmutableTransientPlayerData(this.preferredChannel);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }
}
