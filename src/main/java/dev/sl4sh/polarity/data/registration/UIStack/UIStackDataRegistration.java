package dev.sl4sh.polarity.data.registration.UIStack;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.plugin.PluginContainer;

public class UIStackDataRegistration implements DataRegistration<UIStackData, ImmutableUIStackData> {

    @Override
    public Class<UIStackData> getManipulatorClass() {
        return UIStackData.class;
    }

    @Override
    public Class<? extends UIStackData> getImplementationClass() {
        return UIStackData.class;
    }

    @Override
    public Class<ImmutableUIStackData> getImmutableManipulatorClass() {
        return ImmutableUIStackData.class;
    }

    @Override
    public Class<? extends ImmutableUIStackData> getImmutableImplementationClass() {
        return ImmutableUIStackData.class;
    }

    @Override
    public DataManipulatorBuilder<UIStackData, ImmutableUIStackData> getDataManipulatorBuilder() {
        return new UIStackDataManipulatorBuilder();
    }

    @Override
    public PluginContainer getPluginContainer() {
        return Sponge.getPluginManager().getPlugin("polarity").get();
    }

    @Override
    public String getId() {
        return "uistack_dr";
    }

    @Override
    public String getName() {
        return "UIStack Registration";
    }
}
