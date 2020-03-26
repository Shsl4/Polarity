package io.sl4sh.xmanager.commands.economy;

import io.sl4sh.xmanager.economy.XTradeBuilder;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

@ConfigSerializable
public class XVillager{

    @Setting(value = "villagerName")
    private Text villagerName;

    @Setting(value = "tradeBuilders")
    private List<XTradeBuilder> tradeBuilders;

    public static void spawnEntity(Location<World> spawnLocation, Text villagerName, List<TradeOffer> tradeOffers, Player caller) {
        World world = spawnLocation.getExtent();

        Villager newVillager = (Villager)world.createEntity(EntityTypes.VILLAGER, spawnLocation.getPosition());

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            world.spawnEntity(newVillager);
            caller.sendMessage(Text.of(TextColors.GREEN, "Villager spawned!"));
            newVillager.offer(Keys.DISPLAY_NAME, villagerName);
            newVillager.offer(Keys.CUSTOM_NAME_VISIBLE, true);
            newVillager.offer(Keys.TRADE_OFFERS, tradeOffers);
            newVillager.offer(Keys.INFINITE_DESPAWN_DELAY, true);
            newVillager.offer(Keys.INVULNERABLE, true);
            newVillager.offer(Keys.IS_SILENT, true);
        }
    }

}

