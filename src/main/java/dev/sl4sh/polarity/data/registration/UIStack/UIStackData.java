package dev.sl4sh.polarity.data.registration.UIStack;

import dev.sl4sh.polarity.data.registration.DataRegistration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nonnull;
import java.util.Optional;

public class UIStackData extends AbstractData<UIStackData, ImmutableUIStackData> {

    private Boolean isPlaceholder;

    public Boolean getPlaceholder() {
        return isPlaceholder;
    }

    public void setPlaceholder(Boolean placeholder) {
        this.isPlaceholder = placeholder;
    }

    public UIStackData(){

        this(true);

    }

    public UIStackData(Boolean isPlaceholder) {
        this.isPlaceholder = isPlaceholder;
    }

    public Value<Boolean> isPlaceholder(){

        return Sponge.getRegistry().getValueFactory().createValue(DataRegistration.Keys.UI_STACK, this.isPlaceholder, false);

    }

    @Override
    protected void registerGettersAndSetters() {

        registerFieldGetter(DataRegistration.Keys.UI_STACK, this::getPlaceholder);
        registerFieldSetter(DataRegistration.Keys.UI_STACK, this::setPlaceholder);
        registerKeyValue(DataRegistration.Keys.UI_STACK, this::isPlaceholder);


    }

    @Nonnull
    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(DataRegistration.Keys.UI_STACK, this.isPlaceholder);
    }

    @Override
    public Optional<UIStackData> fill(DataHolder dataHolder, MergeFunction overlap) {

        UIStackData merged = overlap.merge(this, dataHolder.get(UIStackData.class).orElse(null));

        this.isPlaceholder = (merged.isPlaceholder().get());

        return Optional.of(this);

    }

    @Override
    public Optional<UIStackData> from(DataContainer container) {

        System.out.println("Building from");

        if(!container.contains(DataRegistration.Keys.SHOP_DATA_NAME)) { return Optional.empty(); }
        this.isPlaceholder = container.getBoolean(DataRegistration.Keys.UI_STACK.getQuery()).get();
        return Optional.of(this);

    }

    @Override
    public UIStackData copy() {

        return new UIStackData(this.isPlaceholder);

    }

    @Override
    public ImmutableUIStackData asImmutable() {
        return new ImmutableUIStackData(this.isPlaceholder);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }
}
