package io.sl4sh.xmanager.economy.shops.merchants;

import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.economy.shops.UI.XSellDepositUI;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.event.NpcEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;



public class XBuyerNPC {

    public static void summonNPC(World world, Player caller){

        if(!XUtilities.getSpongeWorldToServerWorld(world).isPresent()) { return; }

        ICustomNpc newNPC = NpcAPI.Instance().spawnNPC(XUtilities.getSpongeWorldToServerWorld(world).get(), (int)caller.getPosition().getX(), (int)caller.getPosition().getY(), (int)caller.getPosition().getZ());
        newNPC.addTag("XBuyerNPC");

    }

    @SubscribeEvent
    public void onInteract(NpcEvent.InteractEvent event) {

        Player clicker = (Player) event.player.getMCEntity();

        if (event.npc.hasTag("XBuyerNPC")) {

            new XSellDepositUI().makeForPlayer(clicker);
        }

    }

}
