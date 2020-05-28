package dev.sl4sh.polarity;

import dev.sl4sh.polarity.UI.SharedUI;
import dev.sl4sh.polarity.UI.games.LobbySelectionUI;
import dev.sl4sh.polarity.UI.shops.SellDepositUI;
import dev.sl4sh.polarity.UI.shops.ShopUI;
import dev.sl4sh.polarity.UI.shops.user.MasterUserShopUI;
import dev.sl4sh.polarity.data.registration.npcdata.NPCData;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.enums.NPCTypes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.event.NpcEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class NPCManager {

    public List<LobbySelectionUI> lobbySelectionUIs = new ArrayList<>();

    public void refreshGameSelectionUIs(){

        for(LobbySelectionUI lobbyUI : lobbySelectionUIs){

            lobbyUI.refreshUI();

        }

    }

    public void makeBuyerNPC(Location<World> location){

        Optional<Entity> entity = makeEntity(location, EntityTypes.HUMAN);

        NPCData data = new NPCData(Collections.singletonList("BuyerNPC"), NPCTypes.BUYER_NPC, null, new ShopProfile(), new ArrayList<>());

        entity.ifPresent(value -> value.offer(data));

    }

    public void makeGameSelectionNPC(Location<World> location, int gameID, int pageID){

        Optional<Entity> entity = makeEntity(location, EntityTypes.HUMAN);

        LobbySelectionUI selectionUI = new LobbySelectionUI(gameID, pageID);

        NPCData data = new NPCData(Arrays.asList("GameSelection", String.valueOf(gameID), String.valueOf(pageID)), NPCTypes.GAME_SELECTION_NPC, selectionUI, new ShopProfile(), new ArrayList<>());

        Polarity.getNPCManager().lobbySelectionUIs.add(selectionUI);

        entity.ifPresent(value -> value.offer(data));

    }

    public void makeAdminShopNPC(Player caller, String dataName){

        Optional<ShopProfile> optShopProfile = Polarity.getShopProfiles().getShopProfileByName(dataName);

        if(!optShopProfile.isPresent()) { caller.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Unable to load shop profile")); return; }

        Optional<Entity> entity = makeEntity(caller.getLocation(), EntityTypes.HUMAN);

        NPCData data = new NPCData(Collections.singletonList(dataName), NPCTypes.ADMINSHOP_NPC, null, new ShopProfile(), new ArrayList<>());

        entity.ifPresent(value -> value.offer(data));

    }

    public Optional<Entity> makeGameShopNPC(Location<World> location, SharedUI ui){

        Optional<Entity> entity = makeEntity(location, EntityTypes.HUMAN);

        NPCData data = new NPCData(new ArrayList<>(), NPCTypes.ADMINSHOP_NPC, ui, new ShopProfile(), new ArrayList<>());

        entity.ifPresent(value -> value.offer(data));

        return entity;

    }

    public void makeUserShopNPC(Location<World> location){

        Optional<Entity> entity = makeEntity(location, EntityTypes.HUMAN);

        NPCData data = new NPCData(new ArrayList<>(), NPCTypes.USERSHOP_NPC, null, new ShopProfile(), new ArrayList<>());

        if(entity.isPresent()){

            entity.get().offer(data);

            if(Utilities.getNPCsAPI().isPresent()){

                ICustomNpc npc = (ICustomNpc)Utilities.getNPCsAPI().get().getIEntity((net.minecraft.entity.Entity)entity.get());
                npc.getDisplay().setName("Buy Shop");

            }
            else{

                entity.get().offer(Keys.DISPLAY_NAME, Text.of("Buy Shop"));

            }

        }


    }

    private Optional<Entity> makeEntity(Location<World> location, EntityType type){

        World world = location.getExtent();

        if(Utilities.getNPCsAPI().isPresent() && Utilities.getSpongeWorldToServerWorld(world).isPresent()){

            ICustomNpc<?> npc = Utilities.getNPCsAPI().get().spawnNPC(Utilities.getSpongeWorldToServerWorld(world).get(), (int)location.getPosition().getX(), (int)location.getPosition().getY(), (int)location.getPosition().getZ());
            npc.getAdvanced().setLine(0, 0, "", "");

            return Optional.of((Entity) npc.getMCEntity());

        }

        Entity entity = world.createEntity(type, location.getPosition());
        entity.offer(Keys.INVULNERABLE, true);

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {

            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            return world.spawnEntity(entity) ? Optional.of(entity) : Optional.empty();

        }
        catch(Exception e){

            return Optional.empty();

        }

    }

    @SubscribeEvent
    public void onInteract(NpcEvent.InteractEvent event) {

        Player clicker = (Player)event.player.getMCEntity();
        Entity entity = (Entity)event.npc.getMCEntity();

        if(clicker.hasPermission("*") && clicker.getItemInHand(HandTypes.MAIN_HAND).isPresent() && clicker.getItemInHand(HandTypes.MAIN_HAND).get().getType().equals(ItemTypes.STICK)){

            event.npc.getDisplay().setName("\u00a7" + event.npc.getDisplay().getName());
            return;

        }

        handleInteraction(clicker, entity);

    }

    @Listener
    public void onInteract(InteractEntityEvent.Secondary event){

        if(!(event.getSource() instanceof Player)) { return; }

        Player clicker = (Player)event.getSource();
        Entity entity = event.getTargetEntity();

        handleInteraction(clicker, entity);

    }

    private static void handleInteraction(Player clicker, Entity entity){

        if(entity.get(NPCData.class).isPresent()) {

            if (entity.get(Polarity.Keys.NPC.TYPE).get().equals(NPCTypes.BUYER_NPC)) {

                new SellDepositUI(clicker.getUniqueId()).open();
                return;

            }

            if (entity.get(Polarity.Keys.NPC.TYPE).get().equals(NPCTypes.GAME_SELECTION_NPC)) {

                if(entity.get(Polarity.Keys.NPC.SHARED_UI).isPresent() && entity.get(Polarity.Keys.NPC.SHARED_UI).get().isPresent()){

                    entity.get(Polarity.Keys.NPC.SHARED_UI).get().get().openFor(clicker);
                    return;

                }
                else{

                    LobbySelectionUI ui = new LobbySelectionUI(Integer.parseInt(entity.get(Polarity.Keys.NPC.TAGS).get().get(1)), Integer.parseInt(entity.get(Polarity.Keys.NPC.TAGS).get().get(2)));
                    Polarity.getNPCManager().lobbySelectionUIs.add(ui);
                    entity.offer(Polarity.Keys.NPC.SHARED_UI, Optional.of(ui));
                    ui.openFor(clicker);

                }

            }

            if(entity.get(Polarity.Keys.NPC.TYPE).get().equals(NPCTypes.ADMINSHOP_NPC)){

                if(entity.get(Polarity.Keys.NPC.SHARED_UI).isPresent() && entity.get(Polarity.Keys.NPC.SHARED_UI).get().isPresent()){

                    entity.get(Polarity.Keys.NPC.SHARED_UI).get().get().openFor(clicker);

                }
                else{

                    Optional<ShopProfile> shopProfile = Polarity.getShopProfiles().getShopProfileByName(entity.get(Polarity.Keys.NPC.TAGS).get().get(0));

                    if(shopProfile.isPresent()){

                        ShopUI ui = new ShopUI(shopProfile.get(), entity);
                        entity.offer(Polarity.Keys.NPC.SHARED_UI, Optional.of(ui));
                        ui.openFor(clicker);

                    }

                }

            }

            if(entity.get(Polarity.Keys.NPC.TYPE).get().equals(NPCTypes.USERSHOP_NPC)){

                if(entity.get(Polarity.Keys.NPC.SHARED_UI).get().isPresent()){

                    entity.get(Polarity.Keys.NPC.SHARED_UI).get().get().openFor(clicker);

                }
                else{

                    MasterUserShopUI ui = new MasterUserShopUI(entity);
                    entity.offer(Polarity.Keys.NPC.SHARED_UI, Optional.of(ui));
                    ui.openFor(clicker);

                }

            }

        }

    }

}
