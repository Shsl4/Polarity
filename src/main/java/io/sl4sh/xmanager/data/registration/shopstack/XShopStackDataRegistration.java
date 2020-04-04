package io.sl4sh.xmanager.data.registration.shopstack;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.plugin.PluginContainer;

public class XShopStackDataRegistration implements DataRegistration<XShopStackData, XImmutableShopStackData> {

    @Override
    public Class<XShopStackData> getManipulatorClass() {
        return XShopStackData.class;
    }

    @Override
    public Class<? extends XShopStackData> getImplementationClass() {
        return XShopStackData.class;
    }

    @Override
    public Class<XImmutableShopStackData> getImmutableManipulatorClass() {
        return XImmutableShopStackData.class;
    }

    @Override
    public Class<? extends XImmutableShopStackData> getImmutableImplementationClass() {
        return XImmutableShopStackData.class;
    }

    @Override
    public DataManipulatorBuilder<XShopStackData, XImmutableShopStackData> getDataManipulatorBuilder() {
        return new XShopStackDataManipulatorBuilder();
    }

    @Override
    public PluginContainer getPluginContainer() {
        return Sponge.getPluginManager().getPlugin("xmanager").get();
    }

    @Override
    public String getId() {
        return "xshopstack_dr";
    }

    @Override
    public String getName() {
        return "XShopStack Registration";
    }
}
