package dev.sl4sh.polarity.data.registration.merchantdata;

import dev.sl4sh.polarity.data.registration.DataRegistration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableMerchantData extends AbstractImmutableData<ImmutableMerchantData, MerchantData> {

    private final String dataName;

    public String getDataName() {
        return dataName;
    }

    public ImmutableMerchantData(String dataName) {
        this.dataName = dataName;
    }

    public ImmutableValue<String> dataName(){

        return Sponge.getRegistry().getValueFactory().createValue(DataRegistration.Keys.SHOP_DATA_NAME, dataName, "").asImmutable();
    }

    @Override
    protected void registerGetters() {

        registerFieldGetter(DataRegistration.Keys.SHOP_DATA_NAME, this::getDataName);
        registerKeyValue(DataRegistration.Keys.SHOP_DATA_NAME, this::dataName);

    }

    @Override
    public MerchantData asMutable() {
        return new MerchantData(this.dataName);
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(DataRegistration.Keys.SHOP_DATA_NAME, this.dataName);
    }


    @Override
    public int getContentVersion() {
        return 0;
    }
}
