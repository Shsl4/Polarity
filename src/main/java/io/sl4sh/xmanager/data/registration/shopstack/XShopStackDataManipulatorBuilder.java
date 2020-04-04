package io.sl4sh.xmanager.data.registration.shopstack;

import io.sl4sh.xmanager.data.XDataRegistration;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class XShopStackDataManipulatorBuilder implements DataManipulatorBuilder<XShopStackData, XImmutableShopStackData> {

    @Override
    public XShopStackData create() {
        return new XShopStackData();
    }

    @Override
    public Optional<XShopStackData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(XShopStackData.class).orElse(new XShopStackData()));
    }

    @Override
    public Optional<XShopStackData> build(DataView container) throws InvalidDataException {

        if(!container.contains(XDataRegistration.Keys.SHOP_STACK)) { return Optional.of(new XShopStackData()); }

        return Optional.of(new XShopStackData(container.getBoolean(XDataRegistration.Keys.SHOP_STACK.getQuery()).get()));

    }

}
