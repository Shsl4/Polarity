package io.sl4sh.xmanager.data.registration;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.plugin.PluginContainer;

public class XPlaceholderShopStackRegistration implements DataRegistration<XPlaceholderShopStack, XImmutablePlaceholderShopStack> {

    @Override
    public Class<XPlaceholderShopStack> getManipulatorClass() {
        return XPlaceholderShopStack.class;
    }

    @Override
    public Class<? extends XPlaceholderShopStack> getImplementationClass() {
        return XPlaceholderShopStack.class;
    }

    @Override
    public Class<XImmutablePlaceholderShopStack> getImmutableManipulatorClass() {
        return XImmutablePlaceholderShopStack.class;
    }

    @Override
    public Class<? extends XImmutablePlaceholderShopStack> getImmutableImplementationClass() {
        return XImmutablePlaceholderShopStack.class;
    }

    @Override
    public DataManipulatorBuilder<XPlaceholderShopStack, XImmutablePlaceholderShopStack> getDataManipulatorBuilder() {
        return new XPlaceholderShopStackManipulatorBuilder();
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
