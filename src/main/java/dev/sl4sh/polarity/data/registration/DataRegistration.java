package dev.sl4sh.polarity.data.registration;

import com.google.common.reflect.TypeToken;
import dev.sl4sh.polarity.data.registration.merchantdata.ImmutableMerchantData;
import dev.sl4sh.polarity.data.registration.merchantdata.MerchantData;
import dev.sl4sh.polarity.data.registration.merchantdata.MerchantDataManipulatorBuilder;
import dev.sl4sh.polarity.data.registration.UIStack.ImmutableUIStackData;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackDataManipulatorBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public class DataRegistration {

    public static class Keys{

        public static Key<Value<String>> SHOP_DATA_NAME = DummyObjectProvider.createFor(Key.class, "SHOP_DATA_NAME");
        public static Key<Value<Boolean>> UI_STACK = DummyObjectProvider.createFor(Key.class, "UI_STACK");

    }

    @Listener
    public void onRegisterKeys(GameRegistryEvent.Register<Key<?>> event) {

        Keys.SHOP_DATA_NAME = Key.builder()
                .type(new TypeToken<Value<String>>() {})
                .query(DataQuery.of("ShopDataName"))
                .name("ShopDataName")
                .id("shopdataname")
                .build();

        Keys.UI_STACK = Key.builder()
                .type(new TypeToken<Value<Boolean>>() {})
                .query(DataQuery.of("UIStack"))
                .name("UIStack")
                .id("uistack")
                .build();

        event.register(Keys.SHOP_DATA_NAME);
        event.register(Keys.UI_STACK);

    }

    @Listener
    public void onServerPreInit(GamePreInitializationEvent event){

        PluginContainer plugin = Sponge.getPluginManager().getPlugin("polarity").get();

        org.spongepowered.api.data.DataRegistration.builder()
                .dataClass(MerchantData.class)
                .immutableClass(ImmutableMerchantData.class)
                .builder(new MerchantDataManipulatorBuilder())
                .manipulatorId("merchant_dr")
                .dataName("MerchantData Registration")
                .buildAndRegister(plugin);

        org.spongepowered.api.data.DataRegistration.builder()
                .dataClass(UIStackData.class)
                .immutableClass(ImmutableUIStackData.class)
                .builder(new UIStackDataManipulatorBuilder())
                .manipulatorId("uistack_dr")
                .dataName("UIStack Registration")
                .buildAndRegister(plugin);

    }

}
