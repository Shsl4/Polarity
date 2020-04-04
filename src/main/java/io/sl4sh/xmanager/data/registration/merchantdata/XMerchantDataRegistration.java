package io.sl4sh.xmanager.data.registration.merchantdata;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.plugin.PluginContainer;

public class XMerchantDataRegistration implements DataRegistration<XMerchantData, XImmutableMerchantData> {

    @Override
    public Class<XMerchantData> getManipulatorClass() {
        return XMerchantData.class;
    }

    @Override
    public Class<? extends XMerchantData> getImplementationClass() {
        return XMerchantData.class;
    }

    @Override
    public Class<XImmutableMerchantData> getImmutableManipulatorClass() {
        return XImmutableMerchantData.class;
    }

    @Override
    public Class<? extends XImmutableMerchantData> getImmutableImplementationClass() {
        return XImmutableMerchantData.class;
    }

    @Override
    public DataManipulatorBuilder<XMerchantData, XImmutableMerchantData> getDataManipulatorBuilder() {
        return new XMerchantDataManipulatorBuilder();
    }

    @Override
    public PluginContainer getPluginContainer() {
        return Sponge.getPluginManager().getPlugin("xmanager").get();
    }

    @Override
    public String getId() {
        return "xmerchant_dr";
    }

    @Override
    public String getName() {
        return "XMerchantData Registration";
    }
}
