package io.sl4sh.xmanager.data;

import com.google.common.reflect.TypeToken;
import io.sl4sh.xmanager.data.registration.merchantdata.XImmutableMerchantData;
import io.sl4sh.xmanager.data.registration.merchantdata.XMerchantData;
import io.sl4sh.xmanager.data.registration.merchantdata.XMerchantDataManipulatorBuilder;
import io.sl4sh.xmanager.data.registration.shopstack.XImmutableShopStackData;
import io.sl4sh.xmanager.data.registration.shopstack.XShopStackData;
import io.sl4sh.xmanager.data.registration.shopstack.XShopStackDataManipulatorBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public class XDataRegistration {

    public static class Keys{

        public static Key<Value<String>> SHOP_DATA_NAME = DummyObjectProvider.createFor(Key.class, "SHOP_DATA_NAME");
        public static Key<Value<Boolean>> SHOP_STACK = DummyObjectProvider.createFor(Key.class, "SHOP_STACK");

    }

    @Listener
    public void onRegisterKeys(GameRegistryEvent.Register<Key<?>> event) {

        Keys.SHOP_DATA_NAME = Key.builder()
                .type(new TypeToken<Value<String>>() {})
                .query(DataQuery.of("ShopDataName"))
                .name("ShopDataName")
                .id("shopdataname")
                .build();

        Keys.SHOP_STACK = Key.builder()
                .type(new TypeToken<Value<Boolean>>() {})
                .query(DataQuery.of("ShopStack"))
                .name("ShopStack")
                .id("shopstack")
                .build();

        event.register(Keys.SHOP_DATA_NAME);
        event.register(Keys.SHOP_STACK);

    }

    @Listener
    public void onServerPreInit(GamePreInitializationEvent event){

        PluginContainer plugin = Sponge.getPluginManager().getPlugin("xmanager").get();

        DataRegistration.builder()
                .dataClass(XMerchantData.class)
                .immutableClass(XImmutableMerchantData.class)
                .builder(new XMerchantDataManipulatorBuilder())
                .manipulatorId("xmerchant_dr")
                .dataName("XMerchantData Registration")
                .buildAndRegister(plugin);

        DataRegistration.builder()
                .dataClass(XShopStackData.class)
                .immutableClass(XImmutableShopStackData.class)
                .builder(new XShopStackDataManipulatorBuilder())
                .manipulatorId("xshopstack_dr")
                .dataName("XShopStack Registration")
                .buildAndRegister(plugin);

    }

}
