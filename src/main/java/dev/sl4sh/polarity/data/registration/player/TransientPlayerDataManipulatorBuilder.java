package dev.sl4sh.polarity.data.registration.player;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.data.registration.UIStack.ImmutableUIStackData;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class TransientPlayerDataManipulatorBuilder extends AbstractDataBuilder<TransientPlayerData> implements DataManipulatorBuilder<TransientPlayerData, ImmutableTransientPlayerData> {

    public TransientPlayerDataManipulatorBuilder() {
        super(TransientPlayerData.class, 0);
    }

    @Override
    public TransientPlayerData create() {
        return new TransientPlayerData();
    }

    @Override
    public Optional<TransientPlayerData> createFrom(DataHolder dataHolder) {
        return Optional.empty();
    }

    @Override
    protected Optional<TransientPlayerData> buildContent(DataView container) throws InvalidDataException {
        return Optional.empty();
    }
}
