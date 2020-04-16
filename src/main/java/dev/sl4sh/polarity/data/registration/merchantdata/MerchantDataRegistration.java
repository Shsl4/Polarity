package dev.sl4sh.polarity.data.registration.merchantdata;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.plugin.PluginContainer;

public class MerchantDataRegistration implements DataRegistration<MerchantData, ImmutableMerchantData> {

    @Override
    public Class<MerchantData> getManipulatorClass() {
        return MerchantData.class;
    }

    @Override
    public Class<? extends MerchantData> getImplementationClass() {
        return MerchantData.class;
    }

    @Override
    public Class<ImmutableMerchantData> getImmutableManipulatorClass() {
        return ImmutableMerchantData.class;
    }

    @Override
    public Class<? extends ImmutableMerchantData> getImmutableImplementationClass() {
        return ImmutableMerchantData.class;
    }

    @Override
    public DataManipulatorBuilder<MerchantData, ImmutableMerchantData> getDataManipulatorBuilder() {
        return new MerchantDataManipulatorBuilder();
    }

    @Override
    public PluginContainer getPluginContainer() {
        return Sponge.getPluginManager().getPlugin("polarity").get();
    }

    @Override
    public String getId() {
        return "merchant_dr";
    }

    @Override
    public String getName() {
        return "MerchantData Registration";
    }
}
