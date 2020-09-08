package dev.sl4sh.polarity;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import dev.sl4sh.polarity.data.InventoryBackup;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.data.containers.WorldsInfoContainer;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
import dev.sl4sh.polarity.data.registration.UIStack.UIStackData;
import dev.sl4sh.polarity.data.registration.player.TransientPlayerData;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.enums.ChannelTypes;
import dev.sl4sh.polarity.games.GameSession;
import dev.sl4sh.polarity.games.PositionSnapshot;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

public class Utilities {

    /**
     * Just a static accessor for {@link WorldsInfoContainer#getOrCreate(World)}
     * The saved objects are held in the {@link Polarity} plugin instance. See {@link Polarity#getWorldsInfo()}
     * @param world The world to get the custom info of
     * @return The fetched or newly created {@link WorldInfo}
     */
    public static WorldInfo getOrCreateWorldInfo(World world){

        return Polarity.getWorldsInfo().getOrCreate(world);

    }

    /**
     * Just a static accessor for {@link WorldsInfoContainer#removeWorldInfo(World)}
     * The saved objects are held in the {@link Polarity} plugin instance. See {@link Polarity#getWorldsInfo()}
     * @param world The world to get the custom info removed
     */
    public static void removeWorldInfo(World world){

        Polarity.getWorldsInfo().removeWorldInfo(world);
        Polarity.getPolarity().writeAllConfig();

    }

    /**
     * Checks whether a location is spawn / server protected or not
     * @param target The location to check
     * @return The location is protected or not
     */
    public static boolean isLocationProtected(Location<World> target){

        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(target.getExtent());

        return worldInfo.isWorldProtected() || worldInfo.getWorldProtectedChunks().contains(target.getChunkPosition()) || worldInfo.isGameWorld();

    }

    /**
     * Safely give an ItemStack to a player. Offers the ItemStack in the player's inventory if not full, otherwise summon a TileEntity of the item at the player's location if {@param drop} is true.
     * @param player The player to give an ItemStack to
     * @param stack The ItemStack to give
     * @param drop Should drop the item if the inventory is full
     * @return Whether the operation succeeded or not
     */
    public static boolean givePlayer(Player player, ItemStack stack, boolean drop){

        InventoryTransactionResult result = player.getInventory().offer(stack);

        if(result.getType().equals(InventoryTransactionResult.Type.FAILURE) ||
                result.getType().equals(InventoryTransactionResult.Type.ERROR)){

            if(!drop) { return false; }

            Entity item = player.getWorld().createEntity(EntityTypes.ITEM, player.getPosition());
            item.offer(Keys.REPRESENTED_ITEM, stack.createSnapshot());

            try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {

                frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
                return player.getWorld().spawnEntity(item);

            }

        }

        return true;

    }

    public static void spawnItem(Location<World> location, ItemStackSnapshot snapshot){

        Entity item = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
        item.offer(Keys.REPRESENTED_ITEM, snapshot);

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {

            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            location.getExtent().spawnEntity(item);

        }

    }

    public static void spawnItem(Location<World> location, ItemStack stack){

        Entity item = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
        item.offer(Keys.REPRESENTED_ITEM, stack.createSnapshot());

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {

            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            location.getExtent().spawnEntity(item);

        }

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
     * Saves a player's inventory to an InventoryBackupsContainer {@link Polarity#getInventoryBackups()} WITHOUT clearing it
     * @param player The player who must get it's inventory saved
     */
    public static void savePlayerInventory(Player player){

        // Create a new list which will contain all of our stacks to save
        List<ItemStackSnapshot> snaps = new ArrayList<>();

        // Add each slot's content to our list
        for(Inventory inv : player.getInventory().slots()){

            Optional<ItemStack> optStack = inv.peek();

            if(optStack.isPresent()){

                // Check if the stack was a ui stack (prevent UI items from being saved as this method can be called when the player has a ui opened)
                if(!optStack.get().get(UIStackData.class).isPresent()){

                    optStack.ifPresent(itemStack -> snaps.add(itemStack.createSnapshot()));

                }

            }


        }

        // If the inventory wasn't empty, save it.
        if(snaps.size() > 0){

            Polarity.getInventoryBackups().add(new InventoryBackup(player, snaps));
            Polarity.getPolarity().writeAllConfig();

        }

    }

    /**
     * Saves a player's inventory to an InventoryBackupsContainer {@link Polarity#getInventoryBackups()} WITHOUT clearing it
     * @param playerID The UUID of the player who must get it's inventory saved
     */
    public static void savePlayerInventory(UUID playerID){

        if(!Sponge.getServer().getPlayer(playerID).isPresent()) { return; }

        Player player = Sponge.getServer().getPlayer(playerID).get();

        Utilities.savePlayerInventory(player);

    }

    /**
     * Compares two ItemStacks by their display name, enchantments, durability, type, variant but NOT quantity.
     * @param stack1 The first stack to test
     * @param stack2 The second stack to test
     * @return Whether the stacks are equal or not
     */
    public static boolean compareStacksNoSize(ItemStack stack1, ItemStack stack2){

        DataContainer snapDamage = stack1.toContainer();
        DataContainer testDamage = stack2.toContainer();

        int snapVal = (int)snapDamage.get(DataQuery.of("UnsafeDamage")).get();
        int testVal = (int)testDamage.get(DataQuery.of("UnsafeDamage")).get();

        Optional<Text> snapName = stack1.get(Keys.DISPLAY_NAME);
        Optional<Text> testName = stack2.get(Keys.DISPLAY_NAME);

        Optional<List<Enchantment>> snapEnchantments = stack1.get(Keys.ITEM_ENCHANTMENTS);
        Optional<List<Enchantment>> testEnchantments = stack2.get(Keys.ITEM_ENCHANTMENTS);

        if(ItemStackComparators.TYPE.compare(stack1, stack2) == 0 &&
                ItemStackComparators.PROPERTIES.compare(stack1, stack2) == 0 &&
                snapName.equals(testName) &&
                snapEnchantments.equals(testEnchantments) &&
                snapVal == testVal){

            return true;

        }

        return false;

    }

    public static Optional<NpcAPI> getNPCsAPI(){

        return Optional.ofNullable(NpcAPI.Instance());

    }

    public static boolean listContainsStack(List<ItemStack> stackList, ItemStack testStack){

        for(ItemStack stack : stackList){

            if(compareStacksNoSize(stack, testStack)){

                return true;

            }

        }

        return false;

    }

    /**
     * Restores a player's inventory saved with {@link #savePlayerInventory(Player)}
     * @param player The player who must get it's inventory restored
     */
    public static void restorePlayerInventory(Player player){

        Optional<InventoryBackup> optBackup = Polarity.getInventoryBackups().getBackupForPlayer(player.getUniqueId());

        if(optBackup.isPresent()){

            optBackup.get().getSnapshots().removeIf(snap -> Utilities.givePlayer(player, snap.createStack(), false));
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

        for(IWorld iWorld : Utilities.getNPCsAPI().get().getIWorlds()){

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
     * Fully heals a player
     * @param playerID The UUID of the player to heal
     */
    public static void restoreMaxHealth(UUID playerID){

        if(!Utilities.getPlayerByUniqueID(playerID).isPresent()) { return; }

        Player player = Utilities.getPlayerByUniqueID(playerID).get();

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

        Utilities.setPotionEffects(player, new ArrayList<>());

    }

    /**
     * Removes a player's active potion effects
     * @param playerID The UUID of the player which should have its effects removed
     */
    public static void removePotionEffects(UUID playerID){

        if(!Sponge.getServer().getPlayer(playerID).isPresent()) { return; }

        Player player = Sponge.getServer().getPlayer(playerID).get();

        Utilities.setPotionEffects(player, new ArrayList<>());

    }


    public static void setPotionEffects(Player player, List<PotionEffect> potionEffects){

        player.offer(Keys.POTION_EFFECTS, potionEffects);

    }

    /**
     * Sets a player's game mode
     * @param player The player which should have its GameMode changed
     * @param gameMode The new GameMode
     */
    public static void setGameMode(Player player, GameMode gameMode){

        player.offer(Keys.GAME_MODE, gameMode);

    }

    /**
     * Sets a player's game mode
     * @param playerID The UUID of the player which should have its GameMode changed
     * @param gameMode The new GameMode
     */
    public static void setGameMode(UUID playerID, GameMode gameMode){

        if(!Sponge.getServer().getPlayer(playerID).isPresent()) { return; }

        Player player = Sponge.getServer().getPlayer(playerID).get();

        player.offer(Keys.GAME_MODE, gameMode);

    }

    public static void clearPlayerInventory(Player player){

        player.getInventory().clear();

    }

    public static void clearFireEffects(Player player){

        player.offer(Keys.FIRE_TICKS, 0);

    }

    public static void resetAllVelocities(Player player){

        player.offer(Keys.VELOCITY, Vector3d.ZERO);
        player.offer(Keys.FALL_DISTANCE, 0.0f);
        player.offer(Keys.FALL_TIME, 0);

    }

    public static void clearArrows(Player player){

        player.offer(Keys.STUCK_ARROWS, 0);

    }

    public static void clearPlayerInventory(UUID playerID){

        if(!Sponge.getServer().getPlayer(playerID).isPresent()) { return; }

        Player player = Sponge.getServer().getPlayer(playerID).get();

        player.getInventory().clear();

    }

    public static <T, X> List<T> getKeysByValue(Map<T, X> map, X value){

        List<T> keys = new ArrayList<>();

        for(T key : map.keySet()){

            if (map.get(key).equals(value)){

                keys.add(key);

            }

        }

        return keys;

    }

    public static ItemStack makeUIStack(ItemType type, int count, Text displayName, List<Text> lore, boolean enchanted){

        List<Enchantment> enchants = new ArrayList<>();

        if(enchanted){

            enchants.add(Enchantment.builder().type(EnchantmentTypes.EFFICIENCY).level(1).build());

        }

        ItemStack stack = ItemStack.builder().itemType(type)
                .add(Keys.ITEM_ENCHANTMENTS, enchants)
                .add(Keys.DISPLAY_NAME, displayName)
                .add(Keys.ITEM_LORE, lore)
                .add(Keys.HIDE_ENCHANTMENTS, true)
                .add(Keys.HIDE_ATTRIBUTES, true)
                .add(Keys.HIDE_MISCELLANEOUS, true)
                .add(Keys.HIDE_UNBREAKABLE, true)
                .add(Keys.HIDE_CAN_DESTROY, true)
                .add(Keys.HIDE_CAN_PLACE, true)
                .quantity(count).build();

        stack.offer(new UIStackData());

        return stack;

    }

    public static boolean isUIStack(ItemStack stack){

        return stack.get(UIStackData.class).isPresent();

    }

    public static List<PositionSnapshot> getPositionSnapshotsByTag(World world, String tag){

        List<PositionSnapshot> list = new ArrayList<>();

        for(PositionSnapshot snap : Polarity.getWorldsInfo().getOrCreate(world).getPositionSnapshots()){

            if(snap.getTag().equals(tag)){

                list.add(snap);

            }

        }

        return list;

    }

    public static void delayOneTick(Runnable runnable){

        Task.builder().execute(runnable).delayTicks(1L).submit(Polarity.getPolarity());

    }

    public static void createWorldInfoFrom(World world, World from){

        Polarity.getWorldsInfo().createFrom(world, from);

    }

    public static boolean isValidSessionID(int id){

        return Polarity.getGameManager().doesSessionExistsByID(id);

    }

    public static Optional<GameSession<?>> getGameSessionByID(int id){

        return Polarity.getGameManager().getGameSessionByID(id);

    }

    public static int getNextFreeWrapperID(){

        return Polarity.getGameManager().getNextFreeSessionID();

    }

    public static boolean isValidGameID(int id){

        return Polarity.getGameManager().getValidGameIDs().contains(id);

    }

    public static List<GameSession<?>> getGameSessionsByGameID(int id){

        return Polarity.getGameManager().getGameSessionsByGameID(id);

    }

    public static <T> void ifNotNull(T object, Consumer<T> runnable){

        if(object != null){

            runnable.accept(object);

        }

    }

    public static <T> void ifNull(T object, Runnable runnable){

        if(object == null){

            runnable.run();

        }

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

    public static void closePlayerInventory(Player player){

        Task.builder().delayTicks(1L).execute(player::closeInventory).submit(Polarity.getPolarity());

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

    static public void removeAllFactionClaims(UUID factionID){

        for(WorldInfo worldInfo : Polarity.getWorldsInfo().getList()){

            worldInfo.getWorldFactionClaims().remove(factionID);

        }

    }

    static public ChannelTypes getPreferredChannel(Player player){

        if(player.supports(Polarity.Keys.PREFERRED_CHANNEL)){

            if(player.get(Polarity.Keys.PREFERRED_CHANNEL).get().equals(ChannelTypes.FACTION_CHANNEL) && !Utilities.getPlayerFaction(player).isPresent()){

                return ChannelTypes.WORLD_CHANNEL;

            }

            return player.get(Polarity.Keys.PREFERRED_CHANNEL).get();

        }
        else{

            player.offer(new TransientPlayerData(ChannelTypes.WORLD_CHANNEL));
            return ChannelTypes.WORLD_CHANNEL;

        }

    }

    static public void setPreferredChannel(Player player, ChannelTypes channel){

        if(!player.supports(Polarity.Keys.PREFERRED_CHANNEL)){

            player.offer(new TransientPlayerData());

        }

        player.offer(Polarity.Keys.PREFERRED_CHANNEL, channel);

    }

    static public MessageChannel getChannelForPlayer(Player player){

        switch(Utilities.getPreferredChannel(player)){

            case WORLD_CHANNEL:
                return Utilities.getOrCreateWorldInfo(player.getWorld()).getMessageChannel();
            case GENERAL_CHANNEL:
                return Polarity.getGeneralChannel();
            case FACTION_CHANNEL:
                if(getPlayerFaction(player).isPresent()){
                    return getPlayerFaction(player).get().getFactionChannel();
                }

        }

        return Utilities.getOrCreateWorldInfo(player.getWorld()).getMessageChannel();

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

    public static Optional<Player> getPlayerByUniqueID(UUID uuid){

        return Sponge.getServer().getPlayer(uuid);

    }

    public static Optional<Player> getPlayerByName(String name){

        return Sponge.getServer().getPlayer(name);

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

    public static void setCanFly(Player player, boolean value) {

        player.offer(Keys.CAN_FLY, value);

    }

    public static void resetPlayer(Player player) {

        Utilities.clearPlayerInventory(player);
        Utilities.setGameMode(player, player.getWorld().getProperties().getGameMode());
        Utilities.removePotionEffects(player);
        Utilities.restoreMaxHealth(player);
        Utilities.clearFireEffects(player);
        Utilities.resetAllVelocities(player);
        Utilities.clearArrows(player);
        Utilities.setCanFly(player, false);
        player.setScoreboard(Scoreboard.builder().teams(new ArrayList<>()).build());

    }
}
