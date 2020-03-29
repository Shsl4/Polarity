package io.sl4sh.xmanager.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import static io.sl4sh.xmanager.XManager.SHOP_DATA_NAME;

public class XImmutableMerchantData extends AbstractImmutableData<XImmutableMerchantData, XMerchantData> {

    private final String dataName;

    public String getDataName() {
        return dataName;
    }

    public XImmutableMerchantData(String dataName) {
        this.dataName = dataName;
    }

    public ImmutableValue<String> dataName(){

        return Sponge.getRegistry().getValueFactory().createValue(SHOP_DATA_NAME, dataName, "").asImmutable();
    }

    @Override
    protected void registerGetters() {

        registerFieldGetter(SHOP_DATA_NAME, this::getDataName);
        registerKeyValue(SHOP_DATA_NAME, this::dataName);

    }

    @Override
    public XMerchantData asMutable() {
        return new XMerchantData(this.dataName);
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(SHOP_DATA_NAME, this.dataName);
    }


    @Override
    public int getContentVersion() {
        return 0;
    }
}
