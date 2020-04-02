package io.sl4sh.xmanager.data.registration;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Consumer;

import static io.sl4sh.xmanager.XManager.SHOP_DATA_NAME;
import static io.sl4sh.xmanager.XManager.SHOP_STACK;

public class XPlaceholderShopStack extends AbstractData<XPlaceholderShopStack, XImmutablePlaceholderShopStack> {

    private Boolean isPlaceholder;

    public Boolean getPlaceholder() {
        return isPlaceholder;
    }

    public void setPlaceholder(Boolean placeholder) {
        this.isPlaceholder = placeholder;
    }

    public XPlaceholderShopStack(){

        this(true);

    }

    public XPlaceholderShopStack(Boolean isPlaceholder) {
        this.isPlaceholder = isPlaceholder;
    }

    public Value<Boolean> isPlaceholder(){

        return Sponge.getRegistry().getValueFactory().createValue(SHOP_STACK, this.isPlaceholder, false);

    }

    @Override
    protected void registerGettersAndSetters() {

        registerFieldGetter(SHOP_STACK, this::getPlaceholder);
        registerFieldSetter(SHOP_STACK, this::setPlaceholder);
        registerKeyValue(SHOP_STACK, this::isPlaceholder);


    }

    @Nonnull
    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(SHOP_STACK, this.isPlaceholder);
    }

    @Override
    public Optional<XPlaceholderShopStack> fill(DataHolder dataHolder, MergeFunction overlap) {

        XPlaceholderShopStack merged = overlap.merge(this, dataHolder.get(XPlaceholderShopStack.class).orElse(null));

        this.isPlaceholder = (merged.isPlaceholder().get());

        return Optional.of(this);

    }

    @Override
    public Optional<XPlaceholderShopStack> from(DataContainer container) {

        System.out.println("Building from");

        if(!container.contains(SHOP_DATA_NAME)) { return Optional.empty(); }
        this.isPlaceholder = container.getBoolean(SHOP_STACK.getQuery()).get();
        return Optional.of(this);

    }

    @Override
    public XPlaceholderShopStack copy() {

        return new XPlaceholderShopStack(this.isPlaceholder);

    }

    @Override
    public XImmutablePlaceholderShopStack asImmutable() {
        return new XImmutablePlaceholderShopStack(this.isPlaceholder);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }
}
