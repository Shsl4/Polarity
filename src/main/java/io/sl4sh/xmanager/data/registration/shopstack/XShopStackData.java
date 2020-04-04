package io.sl4sh.xmanager.data.registration.shopstack;

import io.sl4sh.xmanager.data.XDataRegistration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nonnull;
import java.util.Optional;

public class XShopStackData extends AbstractData<XShopStackData, XImmutableShopStackData> {

    private Boolean isPlaceholder;

    public Boolean getPlaceholder() {
        return isPlaceholder;
    }

    public void setPlaceholder(Boolean placeholder) {
        this.isPlaceholder = placeholder;
    }

    public XShopStackData(){

        this(true);

    }

    public XShopStackData(Boolean isPlaceholder) {
        this.isPlaceholder = isPlaceholder;
    }

    public Value<Boolean> isPlaceholder(){

        return Sponge.getRegistry().getValueFactory().createValue(XDataRegistration.Keys.SHOP_STACK, this.isPlaceholder, false);

    }

    @Override
    protected void registerGettersAndSetters() {

        registerFieldGetter(XDataRegistration.Keys.SHOP_STACK, this::getPlaceholder);
        registerFieldSetter(XDataRegistration.Keys.SHOP_STACK, this::setPlaceholder);
        registerKeyValue(XDataRegistration.Keys.SHOP_STACK, this::isPlaceholder);


    }

    @Nonnull
    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(XDataRegistration.Keys.SHOP_STACK, this.isPlaceholder);
    }

    @Override
    public Optional<XShopStackData> fill(DataHolder dataHolder, MergeFunction overlap) {

        XShopStackData merged = overlap.merge(this, dataHolder.get(XShopStackData.class).orElse(null));

        this.isPlaceholder = (merged.isPlaceholder().get());

        return Optional.of(this);

    }

    @Override
    public Optional<XShopStackData> from(DataContainer container) {

        System.out.println("Building from");

        if(!container.contains(XDataRegistration.Keys.SHOP_DATA_NAME)) { return Optional.empty(); }
        this.isPlaceholder = container.getBoolean(XDataRegistration.Keys.SHOP_STACK.getQuery()).get();
        return Optional.of(this);

    }

    @Override
    public XShopStackData copy() {

        return new XShopStackData(this.isPlaceholder);

    }

    @Override
    public XImmutableShopStackData asImmutable() {
        return new XImmutableShopStackData(this.isPlaceholder);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }
}
