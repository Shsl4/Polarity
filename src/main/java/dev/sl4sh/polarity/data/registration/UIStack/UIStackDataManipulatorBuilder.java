package dev.sl4sh.polarity.data.registration.UIStack;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class UIStackDataManipulatorBuilder extends AbstractDataBuilder<UIStackData> implements DataManipulatorBuilder<UIStackData, ImmutableUIStackData> {

    public UIStackDataManipulatorBuilder() {
        super(UIStackData.class, 1);
    }

    @Override
    public UIStackData create() {
        return new UIStackData();
    }

    @Override
    public Optional<UIStackData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(UIStackData.class).orElse(new UIStackData()));
    }

    @Override
    protected Optional<UIStackData> buildContent(DataView container) throws InvalidDataException {

        if(!container.contains(Polarity.Keys.UIStack.DATA_ID) ||
                !container.contains(Polarity.Keys.UIStack.TYPE) ||
                !container.contains(Polarity.Keys.UIStack.BUTTON_ID)) { return Optional.empty(); }

        Integer dataID = container.getInt(Polarity.Keys.UIStack.DATA_ID.getQuery()).get();
        StackTypes type = (StackTypes)container.get(Polarity.Keys.UIStack.TYPE.getQuery()).get();
        Integer buttonID = container.getInt(Polarity.Keys.UIStack.BUTTON_ID.getQuery()).get();

        return Optional.of(new UIStackData(type, dataID, buttonID));
    }

}
