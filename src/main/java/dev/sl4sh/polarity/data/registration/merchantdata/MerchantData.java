package dev.sl4sh.polarity.data.registration.merchantdata;

import dev.sl4sh.polarity.data.registration.DataRegistration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nonnull;
import java.util.Optional;

public class MerchantData extends AbstractData<MerchantData, ImmutableMerchantData> {

    private String dataName;

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public MerchantData(){

        this("");

    }

    public MerchantData(String dataName) {
        this.dataName = dataName;
    }

    public Value<String> merchantData(){

        return Sponge.getRegistry().getValueFactory().createValue(DataRegistration.Keys.SHOP_DATA_NAME, this.dataName, "");

    }

    @Override
    protected void registerGettersAndSetters() {

        registerFieldGetter(DataRegistration.Keys.SHOP_DATA_NAME, this::getDataName);
        registerFieldSetter(DataRegistration.Keys.SHOP_DATA_NAME, this::setDataName);
        registerKeyValue(DataRegistration.Keys.SHOP_DATA_NAME, this::merchantData);


    }

    @Nonnull
    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(DataRegistration.Keys.SHOP_DATA_NAME, this.dataName);
    }

    @Override
    public Optional<MerchantData> fill(DataHolder dataHolder, MergeFunction overlap) {

        MerchantData merged = overlap.merge(this, dataHolder.get(MerchantData.class).orElse(null));

        this.dataName = (merged.merchantData().get());

        return Optional.of(this);

    }

    @Override
    public Optional<MerchantData> from(DataContainer container) {

        if(!container.contains(DataRegistration.Keys.SHOP_DATA_NAME)) { return Optional.empty(); }
        this.dataName = container.getString(DataRegistration.Keys.SHOP_DATA_NAME.getQuery()).get();
        return Optional.of(this);

    }

    @Override
    public MerchantData copy() {

        return new MerchantData(this.dataName);

    }

    @Override
    public ImmutableMerchantData asImmutable() {
        return new ImmutableMerchantData(this.dataName);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

}
