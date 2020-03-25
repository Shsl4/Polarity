package io.sl4sh.xmanager.commands.economy;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class XVillager{

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

