package dev.sl4sh.polarity.data.registration.UIStack;

import dev.sl4sh.polarity.data.registration.DataRegistration;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class UIStackDataManipulatorBuilder implements DataManipulatorBuilder<UIStackData, ImmutableUIStackData> {

    @Override
    public UIStackData create() {
        return new UIStackData();
    }

    @Override
    public Optional<UIStackData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(UIStackData.class).orElse(new UIStackData()));
    }

    @Override
    public Optional<UIStackData> build(DataView container) throws InvalidDataException {

        if(!container.contains(DataRegistration.Keys.UI_STACK)) { return Optional.of(new UIStackData()); }

        return Optional.of(new UIStackData(container.getBoolean(DataRegistration.Keys.UI_STACK.getQuery()).get()));

    }

}
