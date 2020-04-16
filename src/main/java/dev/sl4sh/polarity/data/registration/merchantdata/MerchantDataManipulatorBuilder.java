package dev.sl4sh.polarity.data.registration.merchantdata;

import dev.sl4sh.polarity.data.registration.DataRegistration;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class MerchantDataManipulatorBuilder implements DataManipulatorBuilder<MerchantData, ImmutableMerchantData> {

    @Override
    public MerchantData create() {
        return new MerchantData();
    }

    @Override
    public Optional<MerchantData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(MerchantData.class).orElse(new MerchantData()));
    }

    @Override
    public Optional<MerchantData> build(DataView container) throws InvalidDataException {

        if(!container.contains(DataRegistration.Keys.SHOP_DATA_NAME)) { return Optional.of(new MerchantData()); }

        return Optional.of(new MerchantData(container.getString(DataRegistration.Keys.SHOP_DATA_NAME.getQuery()).get()));

    }

}
