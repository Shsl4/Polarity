package io.sl4sh.xmanager.data.registration;

import io.sl4sh.xmanager.XManager;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class XPlaceholderShopStackManipulatorBuilder implements DataManipulatorBuilder<XPlaceholderShopStack, XImmutablePlaceholderShopStack> {

    @Override
    public XPlaceholderShopStack create() {
        return new XPlaceholderShopStack();
    }

    @Override
    public Optional<XPlaceholderShopStack> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(XPlaceholderShopStack.class).orElse(new XPlaceholderShopStack()));
    }

    @Override
    public Optional<XPlaceholderShopStack> build(DataView container) throws InvalidDataException {

        if(!container.contains(XManager.SHOP_STACK)) { return Optional.of(new XPlaceholderShopStack()); }

        return Optional.of(new XPlaceholderShopStack(container.getBoolean(XManager.SHOP_STACK.getQuery()).get()));

    }

}
