package dev.sl4sh.polarity;

import com.flowpowered.math.vector.Vector3i;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.data.InventoryBackup;
import dev.sl4sh.polarity.data.containers.WorldsInfoContainer;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.*;

public class Utilities {

    /**
     * Just a static accessor for {@link WorldsInfoContainer#getOrCreateWorldInfo(World)}
     * The saved objects are held in the {@link Polarity} plugin instance. See {@link Polarity#getWorldsInfo()}
     * @param world The world to get the custom info of
     * @return The fetched or newly created {@link WorldInfo}
     */
    public static WorldInfo getOrCreateWorldInfo(World world){

        return Polarity.getWorldsInfo().getOrCreateWorldInfo(world);

    }

    /**
     * Just a static accessor for {@link WorldsInfoContainer#removeWorldInfo(World)}
     * The saved objects are held in the {@link Polarity} plugin instance. See {@link Polarity#getWorldsInfo()}
     * @param world The world to get the custom info removed
     * @return Whether the object was removed or not
     */
    public static boolean removeWorldInfo(World world){

        boolean value = Polarity.getWorldsInfo().removeWorldInfo(world);
        Polarity.getPolarity().writeAllConfig();
        return value;

    }

    /**
     * Checks whether a location is spawn / server protected or not
     * @param target The location to check
     * @return The location is protected or not
     */
    public static boolean isLocationProtected(Location<World> target){

        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(target.getExtent());

        return worldInfo.isWorldProtected() || worldInfo.getWorldProtectedChunks().contains(target.getChunkPosition());

    }

    /**
     * Safely give an ItemStack to a player. Offers the ItemStack in the player's inventory if not full, otherwise summon a TileEntity of the item at the player's location.
     * @param player The player to give an ItemStack to
     * @param stack The ItemStack to give
     * @return Whether the operation succeeded or not
     */
    public static boolean givePlayer(Player player, ItemStack stack){

        InventoryTransactionResult result = player.getInventory().offer(stack);

        if(result.getType().equals(InventoryTransactionResult.Type.FAILURE) ||
                result.getType().equals(InventoryTransactionResult.Type.ERROR)){

            Entity item = player.getWorld().createEntity(EntityTypes.ITEM, player.getPosition());
            item.offer(Keys.REPRESENTED_ITEM, stack.createSnapshot());

            try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {

                frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
                return player.getWorld().spawnEntity(item);

            }
            catch(Exception e){

                return false;

            }

        }

        return true;

    }

    /**
     * Deposits an amount of money to a player's bank account
     * @param target The player to give money to
     * @param amount The amount of money to give
     * @param cause The cause of the deposit
     */
    public static void depositToPlayer(Player target, BigDecimal amount, Object cause){

        Optional<PolarityEconomyService> optService = Polarity.getEconomyService();

        if(!optService.isPresent()) { return; }

        Optional<UniqueAccount> optAccount = optService.get().getOrCreateAccount(target.getUniqueId());

        if(!optAccount.isPresent()) { return; }

        optAccount.get().deposit(new PolarityCurrency(), amount, Cause.of(EventContext.empty(), cause));

    }

    /**
     * Saves a player's inventory to an InventoryBackupsContainer {@link Polarity#getInventoryBackups()} WITHOUT emptying it
     * @param player The player who must get it's inventory saved
     */
    public static void savePlayerInventory(Player player){

        // Create a new list which will contain all of our stacks to save
        List<ItemStackSnapshot> snaps = new ArrayList<>();

        // Add each slot's content to our list
        for(Inventory inv : player.getInventory().slots()){

            Optional<ItemStack> optStack = inv.peek();

            optStack.ifPresent(itemStack -> snaps.add(itemStack.createSnapshot()));

        }

        // If the inventory wasn't empty, save it.
        if(snaps.size() > 0){

            Polarity.getInventoryBackups().add(new InventoryBackup(player, snaps));
            Polarity.getPolarity().writeAllConfig();

        }

    }

    /**
     * Restores a player's inventory saved with {@link #savePlayerInventory(Player)}
     * @param player The player who must get it's inventory restored
     */
    public static void restorePlayerInventory(Player player){

        Optional<InventoryBackup> optBackup = Polarity.getInventoryBackups().getBackupForPlayer(player.getUniqueId());

        if(optBackup.isPresent()){

            optBackup.get().getSnapshots().removeIf(snap -> Utilities.givePlayer(player, snap.createStack()));
            Polarity.getPolarity().writeAllConfig();

        }

    }

    /**
     * Returns a list containing every existing factions names
     * @return The existing faction names
     */
    @Nonnull
    public static List<String> getExistingFactionsNames(){

        List<String> returnList = new ArrayList<>();

        for(Faction faction : Polarity.getFactions().getList()){

            returnList.add(faction.getName());

        }

        return returnList;

    }

    /**
     * Returns an optional {@link Faction} value for the unique identifier provided
     * @param factionID The requested faction's unique identifier
     * @return An optional {@link Faction} value
     */
    public static Optional<Faction> getFactionByUniqueID(UUID factionID){

        for(Faction faction : Polarity.getFactions().getList()){

            if(faction.getUniqueId().equals(factionID)){

                return Optional.of(faction);

            }

        }

        return Optional.empty();

    }

    /**
     * Gets a Sponge world as a Minecraft ServerWorld
     * @param world The Sponge world to convert
     * @return An optional value of the ServerWorld
     */
    public static Optional<WorldServer> getSpongeWorldToServerWorld(World world){

        for(IWorld iWorld : NpcAPI.Instance().getIWorlds()){

            if(iWorld.getName().equals(world.getName())){

                return Optional.of(iWorld.getMCWorld());

            }

        }

        return Optional.empty();

    }

    /**
     * Fully heals a player
     * @param player The player to heal
     */
    public static void restoreMaxHealth(Player player){

        if (player.supports(Keys.HEALTH)) {

            double maxHealth = player.get(Keys.MAX_HEALTH).get();
            player.offer(Keys.HEALTH, maxHealth);

        }

    }

    /**
     * Removes a player's active potion effects
     * @param player The player which should have its effects removed
     */
    public static void removePotionEffects(Player player){

        player.offer(Keys.POTION_EFFECTS, new ArrayList<>());

    }

    public static Optional<World> getFactionHomeWorld(UUID factionID){

        for(WorldInfo worldInfo : Polarity.getWorldsInfo().getList()){

            if(worldInfo.getFactionHome(factionID).isPresent()){

                return worldInfo.getTargetWorld();

            }

        }

        return Optional.empty();

    }

    static public Boolean doesFactionExistByUniqueID(UUID factionName) {

        List<Faction> factionsContainer = Polarity.getFactions().getList();

        for(Faction faction : factionsContainer){

            if(faction.getUniqueId().equals(factionName)){

                return true;

            }

        }

        return false;

    }

    static public Boolean doesFactionExistByName(String factionName) {

        List<Faction> factionsContainer = Polarity.getFactions().getList();

        for(Faction faction : factionsContainer){

            if(faction.getName().equals(factionName)){

                return true;

            }

        }

        return false;

    }

    static public List<Vector3i> getFactionClaimsInWorld(UUID factionUUID, World world){

        WorldInfo worldInfo = getOrCreateWorldInfo(world);
        return worldInfo.getFactionClaimedChunks(factionUUID);

    }

    static public Map<Vector3i, World> getAllFactionClaims(UUID factionUUID){

        Map<Vector3i, World> returnMap = new LinkedHashMap<>();

        for(WorldInfo worldInfo : Polarity.getWorldsInfo().getList()){

            if(worldInfo.getTargetWorld().isPresent()){

                for(Vector3i chunkPos : worldInfo.getFactionClaimedChunks(factionUUID)){

                    returnMap.put(chunkPos, worldInfo.getTargetWorld().get());

                }

            }

        }

        return returnMap;

    }

    static public Optional<FactionPermissionData> getPlayerFactionPermissions(Player ply) {

        if (getPlayerFaction(ply).isPresent()) {

            for (FactionMemberData mbData : getPlayerFaction(ply).get().getMemberDataList()) {

                if (mbData.getPlayerUniqueID().equals(ply.getUniqueId())) {

                    return Optional.ofNullable(mbData.getPermissions());

                }

            }

        }

        return Optional.empty();

    }

    static public Optional<FactionMemberData> getMemberDataForPlayer(Player ply) {

        if (getPlayerFaction(ply).isPresent()) {

            for (FactionMemberData mbData : getPlayerFaction(ply).get().getMemberDataList()) {

                if (mbData.getPlayerUniqueID().equals(ply.getUniqueId())) {

                    return Optional.of(mbData);

                }

            }

        }

        return Optional.empty();

    }

    public static Optional<Faction> getFactionByName(String factionName){

        for(Faction faction : Polarity.getFactions().getList()){

            if(faction.getName().equals(factionName)){

                return Optional.of(faction);

            }

        }

        return Optional.empty();

    }


    static public Optional<Faction> getPlayerFaction(Player player){

        List<Faction> factionsContainer = Polarity.getFactions().getList();

        for(Faction faction : factionsContainer){

            for(FactionMemberData memberData : faction.getMemberDataList()){

                if(memberData.getPlayerUniqueID().equals(player.getUniqueId())){

                    return Optional.of(faction);

                }

            }

        }

        return Optional.empty();

    }

    public static Optional<Player> getPlayerByName(String PlayerName){

        return Sponge.getServer().getPlayer(PlayerName);

    }

    public static Optional<Player> getPlayerByUniqueID(UUID uuid){

        return Sponge.getServer().getPlayer(uuid);

    }

    /**
     * Gets a string without the color / style modifying characters (ampersand and \u00a7) and their associated code
     * @param input The string to edit
     * @return The edited string
     */
    public static String getStringWithoutModifiers(String input){

        StringBuilder strBld = new StringBuilder(input);

        int ampSep = input.indexOf("&");

        while(ampSep != -1){

            strBld.deleteCharAt(ampSep);
            strBld.deleteCharAt(ampSep);
            ampSep = strBld.toString().indexOf("&");

        }

        int symSep = input.indexOf("\u00a7");

        while(symSep != -1){

            strBld.deleteCharAt(symSep);
            strBld.deleteCharAt(symSep);
            symSep = strBld.toString().indexOf("\u00a7");

        }

        return strBld.toString();

    }

}
