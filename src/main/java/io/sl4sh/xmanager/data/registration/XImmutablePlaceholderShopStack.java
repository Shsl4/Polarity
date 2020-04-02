package io.sl4sh.xmanager.data.registration;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import static io.sl4sh.xmanager.XManager.SHOP_STACK;

public class XImmutablePlaceholderShopStack extends AbstractImmutableData<XImmutablePlaceholderShopStack, XPlaceholderShopStack> {

    private final Boolean isPlaceholder;

    public Boolean getIsPlaceholder() {
        return isPlaceholder;
    }

    public XImmutablePlaceholderShopStack(Boolean isPlaceholder) {
        this.isPlaceholder = isPlaceholder;
    }

    public ImmutableValue<Boolean> dataName(){

        return Sponge.getRegistry().getValueFactory().createValue(SHOP_STACK, isPlaceholder, false).asImmutable();
    }

    @Override
    protected void registerGetters() {

        registerFieldGetter(SHOP_STACK, this::getIsPlaceholder);
        registerKeyValue(SHOP_STACK, this::dataName);

    }

    @Override
    public XPlaceholderShopStack asMutable() {
        return new XPlaceholderShopStack(this.isPlaceholder);
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(SHOP_STACK, this.isPlaceholder);
    }


    @Override
    public int getContentVersion() {
        return 0;
    }
}
