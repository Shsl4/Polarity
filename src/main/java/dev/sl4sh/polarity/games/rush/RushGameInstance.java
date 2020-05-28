package dev.sl4sh.polarity.games.rush;

import com.flowpowered.math.vector.Vector3i;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.games.rush.RushShopSelectionUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.enums.PolarityColor;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import dev.sl4sh.polarity.games.AbstractGameInstance;
import dev.sl4sh.polarity.games.GameSession;
import dev.sl4sh.polarity.games.PositionSnapshot;
import noppes.npcs.api.entity.ICustomNpc;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.CraftItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RushGameInstance extends AbstractGameInstance {

    private final Map<UUID, Vector3i> respawnLocations = new HashMap<>();
    private final Map<UUID, Double> damageDealtMap = new HashMap<>();
    private final Map<UUID, Integer> killsMap = new HashMap<>();
    private final Map<UUID, Integer> blocksPlaced = new HashMap<>();

    /**
     * The game system works by copying an existing world template (which should have been prepared on the server before utilization),
     * then use this whole new separate world to handle everything as we want. Once the game is over, the game world gets destroyed {@link #destroyGame()}} and
     * the game instance is marked invalid {@link #invalidateGame()}.
     *
     * @param gameWorldName The world template name
     * @param session       The game's assigned session.
     * @throws IllegalStateException If a creation error occurs. Use {@link Exception#getMessage()} to figure out what.
     */
    protected RushGameInstance(String gameWorldName, @Nonnull GameSession<?> session) throws IllegalStateException {
        super(gameWorldName, session);
    }

    /**
     * This method should return the game's name. Used for displaying.
     *
     * @return The game's name
     */
    @Nonnull
    @Override
    public String getGameName() {
        return "Rush";
    }

    /**
     * This method should return the unique game type identifier.
     *
     * @return The game type ID
     */
    @Override
    public int getGameID() {
        return 2;
    }

    /**
     * This method should return how many time the game lasts (in seconds)
     *
     * @return The game time in seconds
     */
    @Override
    public int getGameTimeInSeconds() {
        return 0;
    }

    @Override
    public void rewardPlayers() {

        if(!Polarity.getEconomyService().isPresent()) { return; }

        PolarityEconomyService service = Polarity.getEconomyService().get();
        PolarityCurrency currency = new PolarityCurrency();

        for(UUID playerID : getSession().getSessionPlayers()){

            double rewardValue = 0.0d;

            if(killsMap.get(playerID) != null){

                // Give 0.5 dollar per kill
                rewardValue += killsMap.get(playerID) * .5d;

            }

            if(rewardValue > 0){

                double finalRewardValue = rewardValue;
                service.getOrCreateAccount(playerID).ifPresent(account -> account.deposit(currency, BigDecimal.valueOf(finalRewardValue), Cause.of(EventContext.empty(), this)));

            }

        }

    }

    /**
     * This method should return a color that will be used to color displayed texts
     *
     * @return The game's color
     */
    @Override
    public TextColor getGameTintColor() {
        return TextColors.LIGHT_PURPLE;
    }

    /**
     * This method should handle when a player joins the game.
     *
     * @param player The player who joined.
     * @param role   The player's role
     */
    @Override
    public void handlePlayerJoin(Player player, PlayerSessionRole role) {
        super.handlePlayerJoin(player, role);
    }

    /**
     * This method should handle when a player leaves the game.
     *
     * @param player The player who joined.
     */
    @Override
    public void handlePlayerLeft(Player player) {
        super.handlePlayerLeft(player);
    }

    /**
     * This method should handle when a player dies in the game dimension.
     *
     * @param player The player who died.
     */
    @Override
    public void handlePlayerDeath(Player player, Object source) {

        if(!getGameWorld().isPresent()) { return; }

        if(source instanceof Player && player != source){

            Player causer = (Player)source;

            if(killsMap.containsKey(causer.getUniqueId())){

                Integer kills = killsMap.get(causer.getUniqueId());
                kills++;
                killsMap.put(causer.getUniqueId(), kills);

            }
            else{

                killsMap.put(causer.getUniqueId(), 1);

            }

        }

        for(Inventory slot : player.getInventory().slots()){

            Optional<ItemStack> stack = slot.poll();

            if(stack.isPresent() && !stack.get().getType().equals(ItemTypes.LEATHER_HELMET)
                    && !stack.get().getType().equals(ItemTypes.LEATHER_CHESTPLATE)
                    && !stack.get().getType().equals(ItemTypes.LEATHER_LEGGINGS)
                    && !stack.get().getType().equals(ItemTypes.LEATHER_BOOTS)){

                Utilities.spawnItem(new Location<>(getGameWorld().get(), player.getPosition()), stack.get());

            }

        }

        Team killedPlayerTeam = getSession().getPlayerTeam(player);

        if(respawnLocations.containsKey(player.getUniqueId())){

            Utilities.setGameMode(player, GameModes.SPECTATOR);

            if(source instanceof Player) {

                Player causer = (Player) source;

                if(player != source){

                    Team killerTeam = getSession().getPlayerTeam(causer);

                    for(UUID sessionPlayerID : getSession().getSessionPlayers()){

                        Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> sessionPlayer.sendMessage(Text.of(killedPlayerTeam.getColor(), player.getName(), getGameTintColor(), " was slain by ", killerTeam.getColor(), causer.getName(), getGameTintColor(), ".")));

                    }

                }
                else{

                    for(UUID sessionPlayerID : getSession().getSessionPlayers()){

                        Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> sessionPlayer.sendMessage(Text.of(killedPlayerTeam.getColor(), player.getName(), getGameTintColor(), " killed himself.")));

                    }

                }

            }
            else{

                for(UUID sessionPlayerID : getSession().getSessionPlayers()){

                    Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> sessionPlayer.sendMessage(Text.of(killedPlayerTeam.getColor(), player.getName(), getGameTintColor(), " died.")));

                }

            }

            Vector3i respawnLocation = respawnLocations.get(player.getUniqueId());
            player.offer(Keys.POTION_EFFECTS, Collections.singletonList(PotionEffect.builder().potionType(PotionEffectTypes.BLINDNESS).duration(120).amplifier(2).build()));
            player.sendTitle(Title.builder().title(Text.of(getSession().getPlayerTeam(player).getColor(), getGameName())).subtitle(Text.of(getSession().getPlayerTeam(player).getColor(), "Respawning in 5 seconds")).actionBar(Text.EMPTY).build());
            player.playSound(SoundTypes.BLOCK_NOTE_BASS, player.getPosition(), 0.25f);

            getSession().registerTask(Task.builder().delay(5, TimeUnit.SECONDS).execute(() -> {

                player.setLocation(new Location<>(getGameWorld().get(), respawnLocation));

                Utilities.delayOneTick(() -> {

                    Utilities.setGameMode(player, GameModes.SURVIVAL);
                    setupDefaultInventory(player);
                    Utilities.restoreMaxHealth(player);

                });



            }).submit(Polarity.getPolarity()));

        }
        else{

            eliminatePlayer(player, player, false);

        }

    }

    /**
     * This method should return the GameMode players should play in
     *
     * @return The GameMode
     */
    @Override
    public GameMode getMode() {
        return GameModes.SURVIVAL;
    }

    private void setupDefaultInventory(Player player){

        List<Enchantment> armorEnchantments = new ArrayList<>();
        armorEnchantments.add(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(1).build());

        Team team = getSession().getPlayerTeam(player);
        
        Color color = PolarityColor.rawColorFrom(team.getColor());

        ItemStack helmStack = ItemStack.builder().itemType(ItemTypes.LEATHER_HELMET).build();
        helmStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        helmStack.offer(Keys.COLOR, color);
        helmStack.offer(Keys.DISPLAY_NAME, Text.of(getGameTintColor(), "Rush Helmet"));

        ItemStack chestStack = ItemStack.builder().itemType(ItemTypes.LEATHER_CHESTPLATE).build();
        chestStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        chestStack.offer(Keys.COLOR, color);
        chestStack.offer(Keys.DISPLAY_NAME, Text.of(getGameTintColor(), "Rush Chestplate"));

        ItemStack legsStack = ItemStack.builder().itemType(ItemTypes.LEATHER_LEGGINGS).build();
        legsStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        legsStack.offer(Keys.COLOR, color);
        legsStack.offer(Keys.DISPLAY_NAME, Text.of(getGameTintColor(), "Rush Leggings"));

        ItemStack bootsStack = ItemStack.builder().itemType(ItemTypes.LEATHER_BOOTS).build();
        bootsStack.offer(Keys.ITEM_ENCHANTMENTS, armorEnchantments);
        bootsStack.offer(Keys.COLOR, color);
        bootsStack.offer(Keys.DISPLAY_NAME, Text.of(getGameTintColor(), "Rush Boots"));

        player.setHelmet(helmStack);
        player.setChestplate(chestStack);
        player.setLeggings(legsStack);
        player.setBoots(bootsStack);

    }

    /**
     * This method should handle the game start
     */
    @Override
    public void handleGameStart() {

        super.handleGameStart();

        Task brickSpawnTask = Task.builder().delay(1, TimeUnit.SECONDS).interval(1, TimeUnit.SECONDS).execute(() -> generateOre(ItemTypes.BRICK)).submit(Polarity.getPolarity());
        Task ironSpawnTask = Task.builder().delay(10, TimeUnit.SECONDS).interval(10, TimeUnit.SECONDS).execute(() -> generateOre(ItemTypes.IRON_INGOT)).submit(Polarity.getPolarity());
        Task goldSpawnTask = Task.builder().delay(20, TimeUnit.SECONDS).interval(20, TimeUnit.SECONDS).execute(() -> generateOre(ItemTypes.GOLD_INGOT)).submit(Polarity.getPolarity());
        Task emeraldSpawnTask = Task.builder().delay(40, TimeUnit.SECONDS).interval(40, TimeUnit.SECONDS).execute(() -> generateOre(ItemTypes.EMERALD)).submit(Polarity.getPolarity());
        Task diamondSpawnTask = Task.builder().delay(60, TimeUnit.SECONDS).interval(60, TimeUnit.SECONDS).execute(this::generateDiamond).submit(Polarity.getPolarity());

        getSession().registerTask(brickSpawnTask);
        getSession().registerTask(ironSpawnTask);
        getSession().registerTask(goldSpawnTask);
        getSession().registerTask(emeraldSpawnTask);
        getSession().registerTask(diamondSpawnTask);

    }

    private void generateOre(ItemType type){

        if(!getGameWorld().isPresent()) { return; }

        List<PositionSnapshot> snaps = Utilities.getPositionSnapshotsByTag(getGameWorld().get(), PositionSnapshot.Tags.ORE_SPAWN);

        for(PositionSnapshot snap : snaps){

            Utilities.spawnItem(new Location<>(getGameWorld().get(), snap.getLocation()), ItemStack.builder().itemType(type).build().createSnapshot());

        }

    }

    private void generateDiamond(){

        if(!getGameWorld().isPresent()) { return; }

        List<PositionSnapshot> snaps = Utilities.getPositionSnapshotsByTag(getGameWorld().get(), PositionSnapshot.Tags.DIAMOND_SPAWN);

        for(PositionSnapshot snap : snaps){

            Utilities.spawnItem(new Location<>(getGameWorld().get(), snap.getLocation()), ItemStack.builder().itemType(ItemTypes.DIAMOND).build().createSnapshot());

        }

    }

    /**
     * This method should handle the game end.
     */
    @Override
    public void handleGameEnd() {

        if(getSession().getState().equals(GameSessionState.PRE_GAME)) { getSession().endSession(this); return; }

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        if(getSession().getActiveTeams().size() == 1){

            Team winningTeam = getSession().getActiveTeams().get(0);
            TextColor color = winningTeam.getColor();
            String teamName = winningTeam.getName();

            for(UUID sessionPlayerID : getSession().getSessionPlayers()){

                Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> {

                    sessionPlayer.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, sessionPlayer.getPosition(), 0.25);
                    sessionPlayer.sendTitle(Title.builder().actionBar(Text.EMPTY).title(Text.of(color, teamName, " wins!")).subtitle(Text.of(color, "Make the GGs rain!")).build());

                });

            }

            printStats(winningTeam);

            super.handleGameEnd();

        }
        else if(getSession().getActiveTeams().size() <= 0){

            for(UUID sessionPlayerID : getSession().getSessionPlayers()){

                Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> sessionPlayer.sendTitle(Title.builder().actionBar(Text.EMPTY).title(Text.of(TextColors.GRAY, "It's a tie!")).subtitle(Text.of(TextColors.GRAY, "No one wins on this one.")).build()));

            }

            printStats(null);

            super.handleGameEnd();

        }



    }

    /**
     * This method should handle a player's elimination
     * @param player The eliminated player
     * @param source The source of the elimination
     * @param hasLeft Whether the [layer has left the game
     */
    @Override
    public void eliminatePlayer(Player player, Object source, boolean hasLeft) {

        if(!getSession().getActivePlayers().contains(player.getUniqueId())) { return; }

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        for(UUID sessionPlayerID : getSession().getSessionPlayers()){

            Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> {

                sessionPlayer.playSound(SoundTypes.ENTITY_WITHER_DEATH, sessionPlayer.getPosition(), 0.25);
                sessionPlayer.sendMessage(Text.of(getGameTintColor(), "[", getGameName(), "] | ", getSession().getPlayerTeam(player).getColor(), player.getName(), getGameTintColor(), " is eliminated! (", getSession().getActivePlayers().size() - 1, "/", getSession().getProperties().getMaxPlayers(), ")"));

            });

        }

        super.eliminatePlayer(player, source, hasLeft);

        if(getSession().getActiveTeams().size() <= 1){

            this.handleGameEnd();

        }

    }

    /**
     * This method should handle the game's initialization
     */
    @Override
    public void setupPreGame() {

        super.setupPreGame();

        for(UUID playerID : getSession().getActivePlayers()){

            Utilities.getPlayerByUniqueID(playerID).ifPresent((player) -> {

                this.setupDefaultInventory(player);

                Team team = getSession().getPlayerTeam(player);
                
                ItemStack bedStack = ItemStack.builder().itemType(ItemTypes.BED).build();
                bedStack.offer(Keys.DYE_COLOR, PolarityColor.dyeColorFrom(team.getColor()));
                bedStack.offer(Keys.DISPLAY_NAME, Text.of(team.getColor(), player.getName(), "'s Bed"));
                Utilities.givePlayer(player, bedStack, true);

            });

        }

        spawnTraders();

    }

    private void spawnTraders(){

        if(!getGameWorld().isPresent()) { return; }

        for(PositionSnapshot snap : Utilities.getPositionSnapshotsByTag(getGameWorld().get(), PositionSnapshot.Tags.RUSH_SHOP)){

            Optional<Entity> optNPC = Polarity.getNPCManager().makeGameShopNPC(new Location<>(getGameWorld().get(), snap.getLocation()), new RushShopSelectionUI());

            if(optNPC.isPresent()){

                if(Utilities.getNPCsAPI().isPresent()){

                    ICustomNpc<?> npc = (ICustomNpc<?>) Utilities.getNPCsAPI().get().getIEntity((net.minecraft.entity.Entity)optNPC.get());
                    npc.setName("\u00a75Rush Shop");
                    npc.getDisplay().setSkinTexture("customnpcs:textures/entity/humanmale/villagersteve.png");

                }

            }

        }

    }

    private void printStats(Team winningTeam){

        List<Double> damagesDealt = new ArrayList<>(damageDealtMap.values());
        List<Integer> mostBlocks = new ArrayList<>(blocksPlaced.values());
        List<Integer> kills = new ArrayList<>(killsMap.values());

        Collections.sort(damagesDealt);
        Collections.sort(mostBlocks);
        Collections.sort(kills);

        Double highestDamage = damagesDealt.size() > 0 ? damagesDealt.get(damagesDealt.size() - 1) : 0.0;
        Integer highestBlocks = mostBlocks.size() > 0 ? mostBlocks.get(mostBlocks.size() - 1) : 0;
        Integer highestKills = kills.size() > 0 ? kills.get(kills.size() - 1) : 0;

        UUID mostKills = UUID.randomUUID();
        UUID mostBuilds = UUID.randomUUID();
        UUID mostDamage = UUID.randomUUID();

        for(UUID key : damageDealtMap.keySet()){

            if(damageDealtMap.get(key).equals(highestDamage)){

                mostDamage = key;
                break;

            }

        }

        for(UUID key : blocksPlaced.keySet()){

            if(blocksPlaced.get(key).equals(highestBlocks)){

                mostBuilds = key;
                break;

            }

        }

        for(UUID key : killsMap.keySet()){

            if(killsMap.get(key).equals(highestKills)){

                mostKills = key;
                break;

            }

        }

        // Lambdas require final variables
        final UUID finalMostKills = mostKills;
        final UUID finalMostDamage = mostDamage;
        final UUID finalMostBuilds = mostBuilds;

        for(UUID sessionPlayerID : getSession().getSessionPlayers()){

            long finalGameTimeInMinutes = getTimeElapsed(TimeUnit.MINUTES);
            Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((player) -> {

                player.sendMessage(Text.EMPTY);

                player.sendMessage(Text.of(TextStyles.UNDERLINE, getGameTintColor(), "Game Results:"));

                player.sendMessage(Text.EMPTY);

                player.sendMessage(Text.of(TextStyles.UNDERLINE, getGameTintColor(), "Time Elapsed:", TextStyles.RESET, " ", finalGameTimeInMinutes, " minutes"));

                Utilities.ifNotNull(winningTeam, (team -> player.sendMessage(Text.of(TextStyles.UNDERLINE, getGameTintColor(), "Winning Team:", TextStyles.RESET, " ", winningTeam.getColor(), winningTeam.getDisplayName()))));

                player.sendMessage(Text.EMPTY);

                Utilities.getPlayerByUniqueID(finalMostKills).ifPresent((killer) -> player.sendMessage(Text.of(TextStyles.UNDERLINE, getGameTintColor(), "Most Kills:", TextStyles.RESET, " ", getSession().getPlayerTeam(killer).getColor(), highestKills, " (", killer.getName(), ")")));

                Utilities.getPlayerByUniqueID(finalMostDamage).ifPresent((damager) -> player.sendMessage(Text.of(TextStyles.UNDERLINE, getGameTintColor(), "Most damage:", TextStyles.RESET, " ", getSession().getPlayerTeam(damager).getColor(), highestDamage.intValue(), " (", damager.getName(), ")")));

                Utilities.getPlayerByUniqueID(finalMostBuilds).ifPresent((builder) -> player.sendMessage(Text.of(TextStyles.UNDERLINE, getGameTintColor(), "Most blocks placed:", TextStyles.RESET, " ", getSession().getPlayerTeam(builder).getColor(), highestBlocks, " (", builder.getName(), ")")));

            });

        }

    }

    @Listener(order = Order.FIRST)
    public void onPlayerDealtDamage(DamageEntityEvent event){

        if(event.getTargetEntity() instanceof Player ){

            Player target = (Player) event.getTargetEntity();
            Player causer = null;

            if(event.getCause().first(Player.class).isPresent()){

                causer = event.getCause().first(Player.class).get();

            }
            else if(event.getCause().first(IndirectEntityDamageSource.class).isPresent() && event.getCause().first(IndirectEntityDamageSource.class).get().getIndirectSource() instanceof Player){

                causer = (Player)event.getCause().first(IndirectEntityDamageSource.class).get().getIndirectSource();

            }

            if(causer != null){

                if(damageDealtMap.containsKey(causer.getUniqueId())){

                    Double damage = damageDealtMap.get(causer.getUniqueId());

                    if(!event.willCauseDeath()){

                        damage += event.getFinalDamage();

                    }
                    else{

                        damage += target.get(Keys.HEALTH).get();

                    }

                    damageDealtMap.put(causer.getUniqueId(), damage);

                }
                else{

                    damageDealtMap.put(causer.getUniqueId(), event.getFinalDamage());

                }

            }

        }

    }

    @Listener
    public void onPlayerInteract(InteractBlockEvent.Secondary event, @First Player player){

        if(!getGameWorld().isPresent()) { return; }

        if(event.getTargetBlock().getLocation().isPresent() && event.getTargetBlock().getLocation().get().getExtent().getUniqueId().equals(getGameWorld().get().getUniqueId())){

            if(event.getTargetBlock().getState().getType().equals(BlockTypes.CRAFTING_TABLE)){

                event.setCancelled(true);

            }
            else if(event.getTargetBlock().getState().getType().equals(BlockTypes.BED)){

                event.setCancelled(true);

            }

        }

    }

    @Listener(order = Order.LAST)
    public void onBlockPlaced(ChangeBlockEvent.Place event, @First Player player){

        if(!getGameWorld().isPresent()) { return; }

        if(player.getWorld().getUniqueId().equals(getGameWorld().get().getUniqueId())) {

            for(Transaction<BlockSnapshot> snap : event.getTransactions()){

                if(snap.getFinal().getExtendedState().getType().equals(BlockTypes.BED)){

                    respawnLocations.put(player.getUniqueId(), snap.getFinal().getPosition());
                    player.sendTitle(Title.builder().title(Text.EMPTY).subtitle(Text.EMPTY).actionBar(Text.of(getSession().getPlayerTeam(player).getColor(), "Saved your respawn location")).build());
                    player.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, player.getPosition(), 0.25);

                }

            }

            if(blocksPlaced.containsKey(player.getUniqueId())){

                Integer placedCount = blocksPlaced.get(player.getUniqueId());
                placedCount++;
                blocksPlaced.put(player.getUniqueId(), placedCount);

            }
            else{

                blocksPlaced.put(player.getUniqueId(), 1);

            }

        }

    }

    @Listener(order = Order.LAST)
    public void onBlockDestroyed(ChangeBlockEvent.Break event){

        if(!getGameWorld().isPresent()) { return; }

        for(Transaction<BlockSnapshot> transaction : event.getTransactions()){

            if(transaction.getFinal().getLocation().isPresent() && transaction.getFinal().getLocation().get().getExtent().getUniqueId().equals(getGameWorld().get().getUniqueId())){

                if(respawnLocations.containsValue(transaction.getFinal().getPosition())){

                    List<UUID> list = new ArrayList<>(respawnLocations.keySet());

                    for(UUID targetID : list){

                        if(transaction.getFinal().getPosition().equals(respawnLocations.get(targetID))){

                            if(respawnLocations.remove(targetID) != null){

                                Utilities.getPlayerByUniqueID(targetID).ifPresent(player -> player.sendTitle(Title.builder().title(Text.EMPTY).subtitle(Text.EMPTY).actionBar(Text.of(TextColors.RED, "Your bed has been destroyed. You won't respawn if you die now.")).build()));

                            }

                        }

                    }

                }

            }

        }

    }

    @Listener
    public void onCraft(CraftItemEvent event){

        if(!getGameWorld().isPresent() || !(event.getSource() instanceof Player)) { return; }

        if(((Player)event.getSource()).getWorld().getUniqueId().equals(getGameWorld().get().getUniqueId())) {

            event.setCancelled(true);

        }

    }

}
