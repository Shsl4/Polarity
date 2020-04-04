package io.sl4sh.xmanager.data.registration.merchantdata;

import io.sl4sh.xmanager.data.XDataRegistration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class XImmutableMerchantData extends AbstractImmutableData<XImmutableMerchantData, XMerchantData> {

    private final String dataName;

    public String getDataName() {
        return dataName;
    }

    public XImmutableMerchantData(String dataName) {
        this.dataName = dataName;
    }

    public ImmutableValue<String> dataName(){

        return Sponge.getRegistry().getValueFactory().createValue(XDataRegistration.Keys.SHOP_DATA_NAME, dataName, "").asImmutable();
    }

    @Override
    protected void registerGetters() {

        registerFieldGetter(XDataRegistration.Keys.SHOP_DATA_NAME, this::getDataName);
        registerKeyValue(XDataRegistration.Keys.SHOP_DATA_NAME, this::dataName);

    }

    @Override
    public XMerchantData asMutable() {
        return new XMerchantData(this.dataName);
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(XDataRegistration.Keys.SHOP_DATA_NAME, this.dataName);
    }


    @Override
    public int getContentVersion() {
        return 0;
    }
}
