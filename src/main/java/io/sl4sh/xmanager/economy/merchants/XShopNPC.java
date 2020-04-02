package io.sl4sh.xmanager.economy.merchants;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.data.XMerchantData;
import io.sl4sh.xmanager.economy.XShopUI;
import io.sl4sh.xmanager.economy.XShopProfile;
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

import static io.sl4sh.xmanager.XManager.SHOP_DATA_NAME;

public class XShopNPC {

    // type 0:Interact, 1:Attack, 2:World, 3:Killed, 4:Kill, 5:NPC Interact
    // slot 0-7

    public static void summonNPC(World world, Player caller, String dataName){

        Optional<XShopProfile> optShopProfile = XManager.getShopProfiles().getShopProfileByName(dataName);

        if(!optShopProfile.isPresent()) { caller.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Unable to load shop profile")); return; }

        if(!XUtilities.getSpongeWorldToServerWorld(world).isPresent()) { return; }
        XMerchantData data = new XMerchantData(dataName);

        ICustomNpc newNPC = NpcAPI.Instance().spawnNPC(XUtilities.getSpongeWorldToServerWorld(world).get(), (int)caller.getPosition().getX(), (int)caller.getPosition().getY(), (int)caller.getPosition().getZ());
        Entity npcEntity = (Entity)newNPC.getMCEntity();

        npcEntity.offer(data);
        npcEntity.offer(SHOP_DATA_NAME, dataName);
        System.out.println(npcEntity.get(XMerchantData.class).isPresent());

    }

    @SubscribeEvent
    public void onInteract(NpcEvent.InteractEvent event){

        Player clicker = (Player)event.player.getMCEntity();
        Entity npcEntity = (Entity)event.npc.getMCEntity();

        if(!npcEntity.get(XMerchantData.class).isPresent()) { return; }

        Optional<XShopProfile> optShopProfile = XManager.getShopProfiles().getShopProfileByName(npcEntity.get(XMerchantData.class).get().merchantData().get());

        if(!optShopProfile.isPresent()) { return; }

        XShopProfile shopProfile = optShopProfile.get();

        XShopUI shopInterface = new XShopUI();
        shopInterface.makeFromShopProfile(clicker, shopProfile, event.npc);

    }

}
