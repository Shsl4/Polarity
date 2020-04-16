package dev.sl4sh.polarity.data.registration.UIStack;

import dev.sl4sh.polarity.data.registration.DataRegistration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableUIStackData extends AbstractImmutableData<ImmutableUIStackData, UIStackData> {

    private final Boolean isPlaceholder;

    public Boolean getIsPlaceholder() {
        return isPlaceholder;
    }

    public ImmutableUIStackData(Boolean isPlaceholder) {
        this.isPlaceholder = isPlaceholder;
    }

    public ImmutableValue<Boolean> isPlaceholder(){

        return Sponge.getRegistry().getValueFactory().createValue(DataRegistration.Keys.UI_STACK, isPlaceholder, false).asImmutable();
    }

    @Override
    protected void registerGetters() {

        registerFieldGetter(DataRegistration.Keys.UI_STACK, this::getIsPlaceholder);
        registerKeyValue(DataRegistration.Keys.UI_STACK, this::isPlaceholder);

    }

    @Override
    public UIStackData asMutable() {
        return new UIStackData(this.isPlaceholder);
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(DataRegistration.Keys.UI_STACK, this.isPlaceholder);
    }


    @Override
    public int getContentVersion() {
        return 0;
    }
}
