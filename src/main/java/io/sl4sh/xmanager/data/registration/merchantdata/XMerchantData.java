package io.sl4sh.xmanager.data.registration.merchantdata;

import io.sl4sh.xmanager.data.XDataRegistration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nonnull;
import java.util.Optional;

public class XMerchantData extends AbstractData<XMerchantData, XImmutableMerchantData> {

    private String dataName;

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public XMerchantData(){

        this("");

    }

    public XMerchantData(String dataName) {
        this.dataName = dataName;
    }

    public Value<String> merchantData(){

        return Sponge.getRegistry().getValueFactory().createValue(XDataRegistration.Keys.SHOP_DATA_NAME, this.dataName, "");

    }

    @Override
    protected void registerGettersAndSetters() {

        registerFieldGetter(XDataRegistration.Keys.SHOP_DATA_NAME, this::getDataName);
        registerFieldSetter(XDataRegistration.Keys.SHOP_DATA_NAME, this::setDataName);
        registerKeyValue(XDataRegistration.Keys.SHOP_DATA_NAME, this::merchantData);


    }

    @Nonnull
    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(XDataRegistration.Keys.SHOP_DATA_NAME, this.dataName);
    }

    @Override
    public Optional<XMerchantData> fill(DataHolder dataHolder, MergeFunction overlap) {

        XMerchantData merged = overlap.merge(this, dataHolder.get(XMerchantData.class).orElse(null));

        this.dataName = (merged.merchantData().get());

        return Optional.of(this);

    }

    @Override
    public Optional<XMerchantData> from(DataContainer container) {

        if(!container.contains(XDataRegistration.Keys.SHOP_DATA_NAME)) { return Optional.empty(); }
        this.dataName = container.getString(XDataRegistration.Keys.SHOP_DATA_NAME.getQuery()).get();
        return Optional.of(this);

    }

    @Override
    public XMerchantData copy() {

        return new XMerchantData(this.dataName);

    }

    @Override
    public XImmutableMerchantData asImmutable() {
        return new XImmutableMerchantData(this.dataName);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

}
