package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.enums.PolarityColor;
import dev.sl4sh.polarity.enums.games.GameNotifications;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import dev.sl4sh.polarity.events.PlayerChangeDimensionEvent;
import dev.sl4sh.polarity.games.party.GameParty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.advancement.CriterionEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.CollisionRules;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.Visibilities;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class AbstractGameSession<T extends GameInstance> implements GameSession<T>{

    private final GameLobby lobby;
    private final T game;
    private GameSessionState state = GameSessionState.INACTIVE;
    private final SessionProperties properties;
    private final int sessionID;
    private Task sessionTask;
    private Task notificationTask;
    private final List<UUID> activePlayers = new ArrayList<>();
    private final List<UUID> spectatingPlayers = new ArrayList<>();
    private final List<Task> registeredTasks = new ArrayList<>();
    private final String sessionTasksName = "Session-" + UUID.randomUUID().toString();
    private final List<Team> internalPreTeams = new ArrayList<>();

    private Scoreboard sessionScoreboard = null;

    private int notificationTime;

    public AbstractGameSession(int sessionID, @Nonnull SessionProperties properties) throws IllegalStateException {
        this.sessionID = sessionID;
        this.properties = properties;

        if(this.getProperties().getMaxPlayers() <= 0) { throw new IllegalStateException("Tried to create a game session with a negative or null player capacity"); }
        if(this.getProperties().getMaxPlayers() < this.getProperties().getMinPlayers()) { throw new IllegalStateException("Tried to create a game session with a minimal player amount greater than the maximal player amount"); }

        try{

            this.game = createGame();

            try{

                this.lobby = createLobby();

            }
            catch(IllegalStateException ex){

                game.destroyGame();
                throw new IllegalStateException(ex.getMessage());

            }

        }
        catch (IllegalStateException ex){

            throw new IllegalStateException(ex.getMessage());

        }

        Sponge.getEventManager().registerListeners(Polarity.getPolarity(), this);

    }

    public GameManager getGameManager() {
        return Polarity.getGameManager();
    }

    /**
     * This method should return the session's assigned {@link GameLobby} object.
     *
     * @return The actual object
     */
    @Override
    public GameLobby getLobby() { return this.lobby; }

    /**
     * This method should return the session's assigned {@link GameInstance} object.
     *
     * @return The actual object
     */
    @Override
    public T getGame() { return this.game; }

    @Override
    public List<Team> getTeams() {

        if(getScoreboard() == null){

            return this.internalPreTeams;

        }

        return new ArrayList<>(getScoreboard().getTeams());
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.sessionScoreboard;
    }

    /**
     * This method should return the session's relevant world. (Example: The lobby world if waiting for players, the game world if playing...)
     * @return The relevant world
     */
    public Optional<World> getRelevantWorld() { if(getLobby().isValidLobby()) { return getLobby().getLobbyWorld(); } else { return getGame().getGameWorld(); } }

    /**
     * This method should return the session's active state (Example: Waiting for players, running...)
     *
     * @return The session's state
     */
    @Override
    public GameSessionState getState() { return this.state; }

    /**
     * This method should return the session's ID. A session ID may only be used on one session at a time so it can be identified by a {@link GameManager}.
     *
     * @return The session ID
     */
    @Override
    public int getSessionID() { return this.sessionID; }

    /**
     * This method should return a task that has been scheduled to run a session's essential function (Example: Start the game, End the game, Destroy the session)
     *
     * @return The session's task
     */
    @Nonnull
    @Override
    public Optional<Task> getSessionTask() { return Optional.ofNullable(sessionTask); }

    /**
     * This method should return a task that loops and calls {@link #notifyTime(int, GameNotifications)} every second until the {@link #getSessionTask()}'s execution, if scheduled.
     *
     * @return The notification task
     */
    @Nonnull
    @Override
    public Optional<Task> getNotificationTask() { return Optional.ofNullable(notificationTask); }

    @Override
    public List<UUID> getActivePlayers() {
        return activePlayers;
    }

    /**
     * This method should remove a player from the active players list.
     */
    @Override
    public void removeActivePlayer(Player player) {
        activePlayers.remove(player.getUniqueId());
    }

    /**
     * This method should return the players who will / are spectating the game
     *
     * @return The spectating players
     */
    @Override
    public List<UUID> getSpectatingPlayers() {

        return spectatingPlayers;

    }

    /**
     * This method should return a map containing all the players who are present in the session.
     *
     * @return The player list
     */
    @Nonnull
    @Override
    public List<UUID> getSessionPlayers() {

        List<UUID> newList = new ArrayList<>();
        newList.addAll(getActivePlayers());
        newList.addAll(getSpectatingPlayers());

        return newList;

    }

    /**
     * This method should return the session's properties (Should be final). See {@link SessionProperties}.
     *
     * @return The session's properties
     */
    @Nonnull
    @Override
    public SessionProperties getProperties() { return this.properties; }

    /**
     * This method should set the session's active state (Example: Waiting for players, running...)
     *
     * @param state The new {@link GameSessionState} value
     */
    @Override
    public void setState(GameSessionState state) { this.state = state; Polarity.getNPCManager().refreshGameSelectionUIs(); }

    /**
     * This method should create the lobby that will be fetched with {@link #getLobby()}.
     *
     * @return The created lobby
     * @throws IllegalStateException If a lobby construction error occurs
     */
    @Override
    public GameLobby createLobby() throws IllegalStateException { return new GameLobbyBase(SessionProperties.getRandomLobbyName()); }

    /**
     * This method should create the game that will be fetched with {@link #getGame()} ()}.
     *
     * @return The created game
     * @throws IllegalStateException If a game construction error occurs
     */
    @Override
    public abstract T createGame() throws IllegalStateException;

    /**
     * This method should schedule {@link #getSessionTask()} and {@link #getNotificationTask()} with {@param timeInSeconds} delay.
     *
     * @param timeInSeconds    The time in seconds before {@param runnable}'s execution
     * @param runnable         The action to execute at the end of the time
     * @param notificationType The type of notification that will be sent to {@link #notifyTime(int, GameNotifications)}
     */
    @Override
    public void scheduleSessionTask(int timeInSeconds, Runnable runnable, GameNotifications notificationType) {

        notificationTime = timeInSeconds;

        Utilities.ifNotNull(sessionTask, Task::cancel);
        Utilities.ifNotNull(notificationTask, Task::cancel);

        if(timeInSeconds <= 0) { return; }

        sessionTask = Task.builder().name(sessionTasksName).delay(timeInSeconds, TimeUnit.SECONDS).execute(runnable).submit(Polarity.getPolarity());
        notificationTask = Task.builder().name(sessionTasksName).interval(1, TimeUnit.SECONDS).delay(0, TimeUnit.SECONDS).execute(() ->{

            this.notifyTime(notificationTime, notificationType);
            notificationTime--;

        }).submit(Polarity.getPolarity());

        registerTask(sessionTask);
        registerTask(notificationTask);

    }

    @Override
    public void registerTask(Task task) {
        registeredTasks.add(task);
    }

    /**
     * This method should get called to make a player join the session.
     *
     * @param player            The target player
     * @param playerSessionRole The player's role
     */
    @Override
    public void joinSession(Player player, PlayerSessionRole playerSessionRole) {

        if(getSessionPlayers().contains(player.getUniqueId())) { return; }

        PlayerSessionRole localRole = playerSessionRole;
        List<UUID> actualPlayers = new ArrayList<>();

        if(Polarity.getPartyManager().getPlayerParty(player).isPresent()) {

            GameParty party = Polarity.getPartyManager().getPlayerParty(player).get();

            if(!party.getPartyOwner().equals(player.getUniqueId())){

                player.sendMessage(Text.of(TextColors.RED, "Only the party owner may join game lobbies."));
                return;

            }

            actualPlayers.addAll(party.getPartyPlayers());

        }
        else{

            actualPlayers.add(player.getUniqueId());

        }

        if(getLobby().getLobbyWorld().isPresent()){

            for(UUID actualPlayerID : actualPlayers){

                if(!Sponge.getServer().getPlayer(actualPlayerID).isPresent()) { continue; }

                Player actualPlayer = Sponge.getServer().getPlayer(actualPlayerID).get();

                makeTeamForPlayer(actualPlayer);

                if(!actualPlayer.setLocation(new Location<>(getLobby().getLobbyWorld().get(), getLobby().getLobbyWorld().get().getProperties().getSpawnPosition()))) { return; }

                Utilities.savePlayerInventory(actualPlayerID);
                Utilities.removePotionEffects(actualPlayerID);
                Utilities.restoreMaxHealth(actualPlayerID);
                Utilities.clearPlayerInventory(actualPlayerID);

                if(getActivePlayers().size() >= getProperties().getMaxPlayers()){

                    localRole = PlayerSessionRole.SPECTATOR;

                }

                switch (localRole){

                    case SPECTATOR:

                        getSpectatingPlayers().add(actualPlayerID);
                        Utilities.setGameMode(actualPlayerID, GameModes.SPECTATOR);
                        break;

                    case PLAYER:

                        activePlayers.add(actualPlayerID);

                        for(UUID sessionPlayerID : getSessionPlayers()){

                            Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((msgPlayer) -> msgPlayer.sendMessage(Text.of(getGame().getGameTintColor(), "[", getGame().getGameName(), "] | ", actualPlayer.getName(), " joined the lobby (", getActivePlayers().size(), "/", getProperties().getMaxPlayers(), ")")));

                        }
                        break;

                }

                onPlayerJoinedSession(actualPlayer, playerSessionRole);

            }

            if(getActivePlayers().size() == getProperties().getMaxPlayers()){

                setState(GameSessionState.LAUNCHING);
                scheduleSessionTask(10, this::launchGame, GameNotifications.LOBBY_LAUNCH);

            }
            else if(getActivePlayers().size() >= getProperties().getMinPlayers()){

                setState(GameSessionState.LAUNCHING);
                scheduleSessionTask(60, this::launchGame, GameNotifications.LOBBY_LAUNCH);

            }

        }
        else if(getGame().isValidGame()){

            for(UUID actualPlayerID : actualPlayers){

                getSpectatingPlayers().add(actualPlayerID);

                if(!Sponge.getServer().getPlayer(actualPlayerID).isPresent()) { return; }

                Player actualPlayer = Sponge.getServer().getPlayer(actualPlayerID).get();

                getGame().handlePlayerJoin(actualPlayer, playerSessionRole);
                onPlayerJoinedSession(actualPlayer, playerSessionRole);

            }

        }
        else{

            player.sendMessage(Text.of(TextColors.RED, "Failed to join lobby."));

        }

    }

    /**
     * This method should fire when a player joins the session.
     *
     * @param player            The player who joined.
     * @param playerSessionRole The role of the player
     */
    @Override
    public void onPlayerJoinedSession(Player player, PlayerSessionRole playerSessionRole) {}

    /**
     * This method should fire when a player left the session.
     *
     * @param player The player who left
     */
    @Override
    public void onPlayerLeftSession(Player player) {

        if(!getSessionPlayers().contains(player.getUniqueId())) { return; }

        if (!getLobby().isValidLobby() && getGame().isValidGame()) { getGame().handlePlayerLeft(player); }

        Utilities.clearPlayerInventory(player);
        Utilities.removePotionEffects(player);
        Utilities.restoreMaxHealth(player);
        Utilities.clearFireEffects(player);
        Utilities.resetAllVelocities(player);
        Utilities.clearArrows(player);
        Utilities.setGameMode(player, player.getWorld().getProperties().getGameMode());
        Utilities.setCanFly(player, false);

        getActivePlayers().remove(player.getUniqueId());
        getSpectatingPlayers().remove(player.getUniqueId());

        if(getLobby().isValidLobby()){

            for(UUID playerID : getSessionPlayers()){

                Utilities.getPlayerByUniqueID(playerID).ifPresent((sessionPlayer) -> sessionPlayer.sendMessage(Text.of(TextColors.RED, "[", getGame().getGameName(), "] | ", TextColors.LIGHT_PURPLE, player.getName(), TextColors.RED, " left the lobby.")));

            }

            if(getActivePlayers().size() < getProperties().getMinPlayers()){

                getSessionTask().ifPresent(Task::cancel);
                getNotificationTask().ifPresent(Task::cancel);

                sessionTask = null;
                notificationTask = null;

                setState(GameSessionState.WAITING_FOR_PLAYERS);

            }

        }

        if(getActivePlayers().size() <= 0){

            this.endSession(this);

        }

        printRestoreMessage(player);

    }

    /**
     * This method should fire every second when {@link #getSessionTask()} has been scheduled with the appropriate information.
     *
     * @param timeInSeconds    The time before {@link #getSessionTask()}'s execution
     * @param notificationType The notification type
     */
    @Override
    public void notifyTime(int timeInSeconds, GameNotifications notificationType) {

        // Create a default countdown system that fits for most games. The method can always be overridden to remove this implementation.
        if(!notificationType.equals(GameNotifications.LOBBY_LAUNCH) && getGame().isValidGame()){

            getGame().notifyTime(timeInSeconds, notificationType);

        }
        else{

            // Send titles to players when the times in seconds below are remaining before the game starts.
            if(Arrays.asList(60, 30, 10, 5, 4, 3, 2, 1).contains(timeInSeconds) && getLobby().isValidLobby()){

                for(Player player : getLobby().getLobbyWorld().get().getPlayers()){

                    player.sendTitle(Title.builder().title(Text.of(getGame().getGameTintColor(), getGame().getGameName()))
                            .subtitle(Text.of(getGame().getGameTintColor(), "Game starts in ", timeInSeconds, " seconds"))
                            .actionBar(Text.EMPTY).fadeIn(5).fadeOut(5).stay(30).build());

                    player.playSound(SoundTypes.BLOCK_NOTE_PLING, player.getPosition(), 0.5);

                }

            }

        }

    }

    boolean transferring = false;

    /**
     * This method should handle the game initialization logic
     */
    @Override
    public void launchGame() {

        this.transferring = true;

        sessionScoreboard = Scoreboard.builder().teams(internalPreTeams).build();
        getGame().setupPreGame();
        getLobby().destroyLobby();
        setupScoreboard();

        this.transferring = false;

    }

    /**
     * This method should destroy {@link #getGame()} & {@link #getLobby()} and mark the session as unusable
     *
     * @param source An optional source of the destruction
     */
    @Override
    public void endSession(@Nullable Object source) {

        // Basically invalidate and destroy everything that we can
        this.setState(GameSessionState.OVER);

        for(Task task : registeredTasks){

            task.cancel();

        }

        Utilities.ifNotNull(lobby, GameLobby::destroyLobby);
        Utilities.ifNotNull(game, GameInstance::destroyGame);

        notificationTask = null;
        sessionTask = null;
        activePlayers.clear();
        spectatingPlayers.clear();

        Utilities.ifNotNull(source, (object) -> getGameManager().removeSession(this));

        Task.builder().execute(() -> Sponge.getEventManager().unregisterListeners(this)).delay(5L, TimeUnit.SECONDS).submit(Polarity.getPolarity());

    }

    private void printRestoreMessage(Player player){

        if(Polarity.getInventoryBackups().getBackupForPlayer(player.getUniqueId()).isPresent() && Polarity.getInventoryBackups().getBackupForPlayer(player.getUniqueId()).get().getSnapshots().size() > 0){

            Text first = Text.builder().append(Text.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click here")).onClick(TextActions.executeCallback((src) -> {

                player.playSound(SoundTypes.UI_BUTTON_CLICK, player.getPosition(), .25);

                if(!Utilities.getOrCreateWorldInfo(player.getWorld()).isGameWorld()){

                    Utilities.restorePlayerInventory(player);

                }
                else{

                    player.sendMessage(Text.of(TextColors.RED, "You may only retrieve your items outside a game world."));

                }

            })).build();

            Text second = Text.of(TextStyles.RESET, TextColors.AQUA, " to restore your inventory (backed up before playing games).");

            player.sendMessage(Text.builder().append(first, second).build());

        }

    }

    protected abstract void setupScoreboard();

    protected void makeTeamForPlayer(Player player) {

        if(getProperties().getMaxTeamPlayers() <= 1) {

            Set<Text> playerSet = new HashSet<>();
            playerSet.add(Text.of(player.getName()));
            internalPreTeams.add(Team.builder().members(playerSet).name(player.getName() + "'s Team").color(TextColors.NONE).build());

        }

        for(TextColor teamColor : Arrays.asList(TextColors.DARK_PURPLE, TextColors.GOLD, TextColors.WHITE, TextColors.BLACK)){

            boolean exists = false;

            for(Team team : internalPreTeams){

                if(team.getColor().equals(teamColor) ){

                    exists = true;

                    if(team.getMembers().size() < getProperties().getMaxTeamPlayers()){

                        team.addMember(Text.of(player.getName()));
                        return;

                    }

                    break;


                }

            }
    
            if(!exists){

                Set<Text> playerSet = new HashSet<>();
                playerSet.add(Text.of(player.getName()));

                internalPreTeams.add(Team.builder().allowFriendlyFire(false)
                        .color(teamColor)
                        .collisionRule(CollisionRules.PUSH_OTHER_TEAMS)
                        .name(PolarityColor.colorNameFrom(teamColor) + " Team")
                        .nameTagVisibility(Visibilities.ALWAYS)
                        .canSeeFriendlyInvisibles(true)
                        .members(playerSet)
                        .build());

                return;

            }

        }

    }

    @Listener
    public final void onDimensionChange_Post(PlayerChangeDimensionEvent.Post event){

        if(getRelevantWorld().isPresent() && getRelevantWorld().get().getUniqueId().equals(event.getFromWorld().getUniqueId())){

            if(!transferring){

                onPlayerLeftSession(event.getTargetEntity());

            }

        }

    }

    @Listener
    public void onPlayerMove(MoveEntityEvent event, @First Player eventPlayer){

        if(getRelevantWorld().isPresent() &&  event.getToTransform().getExtent().getUniqueId().equals(getRelevantWorld().get().getUniqueId())){

            if(getState().equals(GameSessionState.PRE_GAME) && activePlayers.contains(eventPlayer.getUniqueId())){

                event.setToTransform(eventPlayer.getTransform());

            }

            if(event.getToTransform().getPosition().getY() <= 5.0f && !eventPlayer.gameMode().get().equals(GameModes.SPECTATOR)){

                if(getLobby().isValidLobby() || getState().equals(GameSessionState.FINISHING)){

                    event.getTargetEntity().setLocation(new Location<>(getRelevantWorld().get(), getRelevantWorld().get().getProperties().getSpawnPosition()));
                    return;

                }

                if(getGame().isValidGame()){

                    getGame().handlePlayerDeath(eventPlayer, eventPlayer);

                }

            }

        }

    }

    @Listener
    public void onDisconnect(@Nonnull ClientConnectionEvent.Disconnect event){

        if(getRelevantWorld().isPresent() && getRelevantWorld().get().getUniqueId().equals(event.getTargetEntity().getWorld().getUniqueId())){

            if(!PolarityWarp.warp(event.getTargetEntity(), "Hub", Polarity.getPolarity())){

                // If it fails for some reason, teleport the players to the default world
                // Getting a world by the server's default world name should always return a value
                World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
                event.getTargetEntity().setLocation(new Location<>(defaultWorld, defaultWorld.getProperties().getSpawnPosition()));

            }

            onPlayerLeftSession(event.getTargetEntity());

        }

    }

    @Listener
    public void onKick(KickPlayerEvent event){

        if(getRelevantWorld().isPresent() && getRelevantWorld().get().getUniqueId().equals(event.getTargetEntity().getWorld().getUniqueId())){

            if(!PolarityWarp.warp(event.getTargetEntity(), "Hub", Polarity.getPolarity())){

                // If it fails for some reason, teleport the players to the default world
                // Getting a world by the server's default world name should always return a value
                World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
                event.getTargetEntity().setLocation(new Location<>(defaultWorld, defaultWorld.getProperties().getSpawnPosition()));

            }

            onPlayerLeftSession(event.getTargetEntity());

        }

    }

    @Listener(order = Order.LAST)
    public void onPlayerDamage(DamageEntityEvent event) {

        if(!(event.getTargetEntity() instanceof Player)) { return; }

        if (getRelevantWorld().isPresent() && getRelevantWorld().get().getUniqueId().equals(event.getTargetEntity().getWorld().getUniqueId())) {

            if(getLobby().isValidLobby()){

                event.setCancelled(true);
                return;

            }

            if(getState().equals(GameSessionState.FINISHING) || getState().equals(GameSessionState.OVER)){

                event.setCancelled(true);
                return;

            }

            if(getSpectatingPlayers().contains(((Player)event.getTargetEntity()).getUniqueId())){

                event.setCancelled(true);
                return;

            }

            if(event.willCauseDeath()){

                event.setCancelled(true);

                if(event.getCause().first(Player.class).isPresent()){

                    getGame().handlePlayerDeath((Player)event.getTargetEntity(), event.getCause().first(Player.class).get());

                }
                else if(event.getCause().first(IndirectEntityDamageSource.class).isPresent() && event.getCause().first(IndirectEntityDamageSource.class).get().getIndirectSource() instanceof Player){

                    getGame().handlePlayerDeath((Player)event.getTargetEntity(), (Player)event.getCause().first(IndirectEntityDamageSource.class).get().getIndirectSource());

                }
                else{

                    getGame().handlePlayerDeath((Player)event.getTargetEntity(), event.getSource());

                }

            }

        }

    }

    /**
     * This method handles block break events. Cancels everything be default.
     * @param event The event
     */
    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player eventPlayer){

        for(Transaction<BlockSnapshot> transaction : event.getTransactions()){

            BlockSnapshot snap = transaction.getOriginal();

            if(getRelevantWorld().isPresent() && snap.getWorldUniqueId().equals(getRelevantWorld().get().getUniqueId())){

                event.setCancelled(true);

            }

        }

    }

    /**
     * This method listens for advancement events. Cancels everything: we don't want players to be able to fulfill advancements while they are playing games.
     * @param event The criterion event
     */
    @Listener
    public void onAdvancement(CriterionEvent.Grant event){

        if(getRelevantWorld().isPresent() && event.getTargetEntity().getWorld().getUniqueId().equals(getRelevantWorld().get().getUniqueId())){

            event.setCancelled(true);

        }

    }

}
