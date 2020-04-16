package dev.sl4sh.polarity.economy.shops.merchants;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.registration.DataRegistration;
import dev.sl4sh.polarity.data.registration.merchantdata.MerchantData;
import dev.sl4sh.polarity.economy.shops.UI.ShopUI;
import dev.sl4sh.polarity.economy.ShopProfile;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.event.NpcEvent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class ShopNPC {

    // type 0:Interact, 1:Attack, 2:World, 3:Killed, 4:Kill, 5:NPC Interact
    // slot 0-7

    public static void summonNPC(World world, Player caller, String dataName){

        Optional<ShopProfile> optShopProfile = Polarity.getShopProfiles().getShopProfileByName(dataName);

        if(!optShopProfile.isPresent()) { caller.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Unable to load shop profile")); return; }

        if(!Utilities.getSpongeWorldToServerWorld(world).isPresent()) { return; }
        MerchantData data = new MerchantData(dataName);

        ICustomNpc newNPC = NpcAPI.Instance().spawnNPC(Utilities.getSpongeWorldToServerWorld(world).get(), (int)caller.getPosition().getX(), (int)caller.getPosition().getY(), (int)caller.getPosition().getZ());
        Entity npcEntity = (Entity)newNPC.getMCEntity();

        npcEntity.offer(data);
        npcEntity.offer(DataRegistration.Keys.SHOP_DATA_NAME, dataName);

    }

    @SubscribeEvent
    public void onInteract(NpcEvent.InteractEvent event){

        Player clicker = (Player)event.player.getMCEntity();
        Entity npcEntity = (Entity)event.npc.getMCEntity();

        if(!npcEntity.get(MerchantData.class).isPresent()) { return; }

        Optional<ShopProfile> optShopProfile = Polarity.getShopProfiles().getShopProfileByName(npcEntity.get(MerchantData.class).get().merchantData().get());

        if(!optShopProfile.isPresent()) { return; }

        ShopProfile shopProfile = optShopProfile.get();

        ShopUI shopInterface = new ShopUI();
        shopInterface.makeFromShopProfile(clicker, shopProfile, event.npc);

    }

}
