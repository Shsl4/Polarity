package io.sl4sh.xmanager.data;

import io.sl4sh.xmanager.XManager;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class XMerchantDataManipulatorBuilder implements DataManipulatorBuilder<XMerchantData, XImmutableMerchantData> {

    @Override
    public XMerchantData create() {
        return new XMerchantData();
    }

    @Override
    public Optional<XMerchantData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(XMerchantData.class).orElse(new XMerchantData()));
    }

    @Override
    public Optional<XMerchantData> build(DataView container) throws InvalidDataException {

        if(!container.contains(XManager.SHOP_DATA_NAME)) { return Optional.of(new XMerchantData()); }

        return Optional.of(new XMerchantData(container.getString(XManager.SHOP_DATA_NAME.getQuery()).get()));

    }

}
