package io.sl4sh.xmanager.data.registration.shopstack;

import io.sl4sh.xmanager.data.XDataRegistration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class XImmutableShopStackData extends AbstractImmutableData<XImmutableShopStackData, XShopStackData> {

    private final Boolean isPlaceholder;

    public Boolean getIsPlaceholder() {
        return isPlaceholder;
    }

    public XImmutableShopStackData(Boolean isPlaceholder) {
        this.isPlaceholder = isPlaceholder;
    }

    public ImmutableValue<Boolean> isPlaceholder(){

        return Sponge.getRegistry().getValueFactory().createValue(XDataRegistration.Keys.SHOP_STACK, isPlaceholder, false).asImmutable();
    }

    @Override
    protected void registerGetters() {

        registerFieldGetter(XDataRegistration.Keys.SHOP_STACK, this::getIsPlaceholder);
        registerKeyValue(XDataRegistration.Keys.SHOP_STACK, this::isPlaceholder);

    }

    @Override
    public XShopStackData asMutable() {
        return new XShopStackData(this.isPlaceholder);
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(XDataRegistration.Keys.SHOP_STACK, this.isPlaceholder);
    }


    @Override
    public int getContentVersion() {
        return 0;
    }
}
