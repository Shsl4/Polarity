package io.sl4sh.xmanager.economy.merchants;

import com.flowpowered.math.vector.Vector3d;
import de.dosmike.sponge.megamenus.MegaMenus;
import de.dosmike.sponge.megamenus.api.MenuRenderer;
import de.dosmike.sponge.megamenus.impl.BaseMenuImpl;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.XMerchantData;
import io.sl4sh.xmanager.economy.XEconomyShopRecipe;
import io.sl4sh.xmanager.economy.XShopProfile;
import io.sl4sh.xmanager.economy.ui.XButton;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.PickupRules;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

import java.util.Optional;

import static io.sl4sh.xmanager.XManager.SHOP_DATA_NAME;

public class XHuman {

    public static void summonMerchant(World world, Player caller, String dataName, Optional<Player> targetSkin) {

        Optional<XShopProfile> optShopProfile = XManager.getShopProfiles().getShopProfileByName(dataName);

        if(!optShopProfile.isPresent()) { caller.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Unable to load shop profile")); return; }

        Human newHuman = (Human)world.createEntity(EntityTypes.HUMAN, caller.getPosition());

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {

            XMerchantData data = new XMerchantData(dataName);

            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            newHuman.offer(Keys.DISPLAY_NAME, Text.of(optShopProfile.get().getMerchantName()));
            newHuman.offer(Keys.CUSTOM_NAME_VISIBLE, true);
            newHuman.offer(Keys.INFINITE_DESPAWN_DELAY, true);
            newHuman.offer(Keys.INVULNERABLE, true);
            newHuman.offer(Keys.IS_SILENT, true);
            newHuman.offer(Keys.SKIN_UNIQUE_ID, targetSkin.orElse(caller).getUniqueId());
            newHuman.offer(Keys.PICKUP_RULE, PickupRules.DISALLOWED);
            newHuman.offer(data);
            newHuman.offer(SHOP_DATA_NAME, dataName);
            world.spawnEntity(newHuman);
            caller.sendMessage(Text.of(TextColors.GREEN, "Human spawned!"));

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

        if(event.getSource() instanceof Player){

            Player clicker = (Player)event.getSource();

            if(!event.getTargetEntity().get(XMerchantData.class).isPresent() || !event.getTargetEntity().getType().equals(EntityTypes.HUMAN)) { return; }

            Optional<XShopProfile> optShopProfile = XManager.getShopProfiles().getShopProfileByName(event.getTargetEntity().get(XMerchantData.class).get().merchantData().get());

            if(!optShopProfile.isPresent()) { clicker.sendMessage(Text.of(TextColors.AQUA, "I'm currently unavailable. Sorry.")); return; }

            XShopProfile shopProfile = optShopProfile.get();

            event.getTargetEntity().offer(Keys.DISPLAY_NAME, Text.of(shopProfile.getMerchantName()));

            BaseMenuImpl menu = MegaMenus.createMenu();
            menu.setTitle(Text.of(TextStyles.BOLD, TextColors.AQUA, shopProfile.getShopName()));

            int x = 0;
            int y = 0;
            int height = shopProfile.getShopPageHeight();

            for(XEconomyShopRecipe recipe : shopProfile.getShopRecipes()){

                if(!recipe.isValidRecipe()) { continue; }

                if(y < height){

                    if(x < 9){

                        menu.add(XButton.builder().setPosition(SlotPos.of(x, y)).setRecipe(recipe).build());

                        x++;

                    }
                    else{

                        x = 0;
                        y++;

                    }

                }

            }

            MenuRenderer render = (MenuRenderer)menu.createGuiRenderer(height,true);
            render.open(clicker);

        }


    }

}
