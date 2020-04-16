package dev.sl4sh.polarity.economy.shops.merchants;

import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.economy.shops.UI.SellDepositUI;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.event.NpcEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;



public class BuyerNPC {

    public static void summonNPC(World world, Player caller){

        if(!Utilities.getSpongeWorldToServerWorld(world).isPresent()) { return; }

        ICustomNpc newNPC = NpcAPI.Instance().spawnNPC(Utilities.getSpongeWorldToServerWorld(world).get(), (int)caller.getPosition().getX(), (int)caller.getPosition().getY(), (int)caller.getPosition().getZ());
        newNPC.addTag("BuyerNPC");

    }

    @SubscribeEvent
    public void onInteract(NpcEvent.InteractEvent event) {

        Player clicker = (Player) event.player.getMCEntity();

        if (event.npc.hasTag("BuyerNPC")) {

            new SellDepositUI().makeForPlayer(clicker);
        }

    }

}
