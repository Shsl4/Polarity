package dev.sl4sh.polarity.data.registration;

import com.google.common.reflect.TypeToken;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.SharedUI;
import dev.sl4sh.polarity.data.registration.npcdata.ImmutableNPCData;
import dev.sl4sh.polarity.data.registration.npcdata.NPCData;
import dev.sl4sh.polarity.data.registration.npcdata.NPCDataManipulatorBuilder;
import dev.sl4sh.polarity.data.registration.UIStack.ImmutableUIStackData;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackDataManipulatorBuilder;
import dev.sl4sh.polarity.data.registration.player.ImmutableTransientPlayerData;
import dev.sl4sh.polarity.data.registration.player.TransientPlayerData;
import dev.sl4sh.polarity.data.registration.player.TransientPlayerDataManipulatorBuilder;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.enums.NPCTypes;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import dev.sl4sh.polarity.enums.games.ChannelTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.PluginContainer;

public class PolarityDataRegistration {

    private static void registerKeys(){

        Polarity.Keys.NPC.TAGS = Key.builder()
                .type(new TypeToken<ListValue<String>>() {})
                .query(DataQuery.of("ShopDataName"))
                .name("ShopDataName")
                .id("shopdataname")
                .build();

        Polarity.Keys.UIStack.TYPE = Key.builder()
                .type(new TypeToken<Value<StackTypes>>() {})
                .query(DataQuery.of("StackType"))
                .name("StackType")
                .id("stacktype")
                .build();

        Polarity.Keys.UIStack.DATA_ID = Key.builder()
                .type(new TypeToken<Value<Integer>>() {})
                .query(DataQuery.of("StackDataID"))
                .name("StackDataID")
                .id("stackdataid")
                .build();


        Polarity.Keys.UIStack.BUTTON_ID = Key.builder()
                .type(new TypeToken<Value<Integer>>() {})
                .query(DataQuery.of("ButtonID"))
                .name("ButtonID")
                .id("buttonid")
                .build();

        Polarity.Keys.NPC.TYPE = Key.builder()
                .type(new TypeToken<Value<NPCTypes>>() {})
                .query(DataQuery.of("NPCType"))
                .name("NPCType")
                .id("npctype")
                .build();

        Polarity.Keys.PREFERRED_CHANNEL = Key.builder()
                .type(new TypeToken<Value<ChannelTypes>>() {})
                .query(DataQuery.of("PreferredChannel"))
                .name("PreferredChannel")
                .id("preferredchannel")
                .build();

        Polarity.Keys.PREFERRED_CHANNEL = Key.builder()
                .type(new TypeToken<Value<ChannelTypes>>() {})
                .query(DataQuery.of("PreferredChannel"))
                .name("PreferredChannel")
                .id("preferredchannel")
                .build();

        Polarity.Keys.NPC.SHARED_UI = Key.builder()
                .type(new TypeToken<OptionalValue<SharedUI>>() {})
                .query(DataQuery.of("SharedUI"))
                .name("SharedUI")
                .id("sharedui")
                .build();

        Polarity.Keys.NPC.SHOP_PROFILE = Key.builder()
                .type(new TypeToken<Value<ShopProfile>>() {})
                .query(DataQuery.of("ShopProfile"))
                .name("ShopProfile")
                .id("shopprofile")
                .build();

        Polarity.Keys.NPC.STORAGE = Key.builder()
                .type(new TypeToken<ListValue<ItemStackSnapshot>>() {})
                .query(DataQuery.of("NPCStorage"))
                .name("NPCStorage")
                .id("npcstorage")
                .build();

    }

    public static void register(){

        PluginContainer plugin = Sponge.getPluginManager().getPlugin("polarity").get();

        registerKeys();

        DataRegistration.builder()
                .dataClass(NPCData.class)
                .immutableClass(ImmutableNPCData.class)
                .builder(new NPCDataManipulatorBuilder())
                .manipulatorId("npc_data")
                .dataName("NPC Data")
                .buildAndRegister(plugin);

        DataRegistration.builder()
                .dataClass(UIStackData.class)
                .immutableClass(ImmutableUIStackData.class)
                .builder(new UIStackDataManipulatorBuilder())
                .manipulatorId("uistack")
                .dataName("UIStack Data")
                .buildAndRegister(plugin);

        DataRegistration.builder()
                .dataClass(TransientPlayerData.class)
                .immutableClass(ImmutableTransientPlayerData.class)
                .builder(new TransientPlayerDataManipulatorBuilder())
                .manipulatorId("transientplayerdata")
                .dataName("Transient Player Data")
                .buildAndRegister(plugin);

    }

}
