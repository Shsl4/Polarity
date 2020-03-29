package io.sl4sh.xmanager.economy.merchants;

import com.flowpowered.math.vector.Vector3d;
import de.dosmike.sponge.megamenus.MegaMenus;
import de.dosmike.sponge.megamenus.api.MenuRenderer;
import de.dosmike.sponge.megamenus.impl.BaseMenuImpl;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.XMerchantData;
import io.sl4sh.xmanager.economy.XEconomyShopRecipe;
import io.sl4sh.xmanager.economy.XShopProfile;
import io.sl4sh.xmanager.economy.XTradeProfile;
import io.sl4sh.xmanager.economy.ui.XButton;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.data.type.PickupRules;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import static io.sl4sh.xmanager.XManager.SHOP_DATA_NAME;

@ConfigSerializable
public class XVillager {

    public static void summonMerchant(World world, Player caller, String dataName) {

        Optional<XTradeProfile> optTradeProfile = XManager.getTradeProfiles().getTradeProfileByName(dataName);

        if(!optTradeProfile.isPresent()) { caller.sendMessage(Text.of(TextColors.AQUA, "[TradeBuilder] | Unable to load shop profile")); return; }

        Villager newVillager = (Villager)world.createEntity(EntityTypes.VILLAGER, caller.getPosition());

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {

            XMerchantData data = new XMerchantData(dataName);

            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            newVillager.offer(Keys.DISPLAY_NAME, Text.of(optTradeProfile.get().getMerchantName()));
            newVillager.offer(Keys.CUSTOM_NAME_VISIBLE, true);
            newVillager.offer(Keys.TRADE_OFFERS, optTradeProfile.get().getTradeRecipes());
            newVillager.offer(Keys.INFINITE_DESPAWN_DELAY, true);
            newVillager.offer(Keys.INVULNERABLE, true);
            newVillager.offer(Keys.IS_SILENT, true);
            newVillager.offer(Keys.CAREER, optTradeProfile.get().getVillagerCareer());
            newVillager.offer(Keys.PICKUP_RULE, PickupRules.DISALLOWED);
            newVillager.offer(data);
            newVillager.offer(SHOP_DATA_NAME, dataName);

            world.spawnEntity(newVillager);

        }

    }

    @Listener
    public void onMerchantMove(MoveEntityEvent event) {

        if(event.getTargetEntity().get(XMerchantData.class).isPresent()) {

            event.setCancelled(true);

        }
    }

    @Listener
    public void onRightClick(InteractEntityEvent event){

        if(event.getSource() instanceof Player) {

            Player clicker = (Player) event.getSource();

            if (!event.getTargetEntity().get(XMerchantData.class).isPresent() || !event.getTargetEntity().getType().equals(EntityTypes.VILLAGER)) { return; }

            Optional<XTradeProfile> optTradeProfile = XManager.getTradeProfiles().getTradeProfileByName(event.getTargetEntity().get(XMerchantData.class).get().merchantData().get());

            if (!optTradeProfile.isPresent()) {

                clicker.sendMessage(Text.of(TextColors.AQUA, "I don't want to trade now."));
                event.setCancelled(true);
                return;

            }

            XTradeProfile tradeProfile = optTradeProfile.get();

            event.getTargetEntity().offer(Keys.CAREER, tradeProfile.getVillagerCareer());
            event.getTargetEntity().offer(Keys.TRADE_OFFERS, tradeProfile.getTradeRecipes());
            event.getTargetEntity().offer(Keys.DISPLAY_NAME, Text.of(tradeProfile.getMerchantName()));

        }

    }

}

