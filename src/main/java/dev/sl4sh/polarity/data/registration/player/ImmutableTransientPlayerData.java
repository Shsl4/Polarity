package dev.sl4sh.polarity.data.registration.player;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.enums.games.ChannelTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.channel.MessageChannel;

import javax.annotation.Nullable;
import java.util.Optional;

public class ImmutableTransientPlayerData extends AbstractImmutableData<ImmutableTransientPlayerData, TransientPlayerData> {

    private final ChannelTypes preferredChannel;

    public ImmutableTransientPlayerData(ChannelTypes channel){
        this.preferredChannel = channel;
        registerGetters();;
    }

    public ImmutableTransientPlayerData(){
        this(null);
    }

    public ChannelTypes getPreferredChannel() {
        return preferredChannel;
    }

    public ImmutableValue<ChannelTypes> preferredChannel(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.PREFERRED_CHANNEL, this.preferredChannel).asImmutable();

    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(Polarity.Keys.PREFERRED_CHANNEL, this::getPreferredChannel);
        registerKeyValue(Polarity.Keys.PREFERRED_CHANNEL, this::preferredChannel);

    }

    @Override
    public TransientPlayerData asMutable() {
        return new TransientPlayerData(this.preferredChannel);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }
}
