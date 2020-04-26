package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.PolarityWarp;
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
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
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
    private final GameManager gameManager;
    private final SessionProperties properties;
    private final int sessionID;
    private Task sessionTask;
    private Task notificationTask;
    private final Map<Player, Integer> activePlayers = new HashMap<>();
    private final List<Player> spectatingPlayers = new ArrayList<>();
    
    private int notificationTime;

    public AbstractGameSession(GameManager gameManager, int sessionID, @Nonnull SessionProperties properties) throws IllegalStateException {

        this.gameManager = gameManager;
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
        return gameManager;
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

    /**
     * This method should return the session's relevant world. (Example: The lobby world if waiting for players, the game world if playing...)
     * @return The relevant world
     */
    public World getRelevantWorld() { if(getLobby().isValidLobby()) { return getLobby().getLobbyWorld(); } else { return getGame().getGameWorld(); } }

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

    /**
     * This method should return the {@link #getGame()}'s actively participating players. (Example: Alive players, Waiting for respawn players...)
     *
     * @return The active players.
     */
    @Override
    public List<Player> getActivePlayers() {

        return new ArrayList<>(activePlayers.keySet());

    }

    /**
     * This method should return the active players associated with their team ID
     *
     * @return The players
     */
    @Override
    public Map<Player, Integer> getPlayerTeams() {
        return activePlayers;
    }

    /**
     * This method should remove a player from the active players list.
     */
    @Override
    public void removeActivePlayer(Player player) {
        activePlayers.remove(player);
    }

    /**
     * This method should return the players who will / are spectating the game
     *
     * @return The spectating players
     */
    @Override
    public List<Player> getSpectatingPlayers() {
        return spectatingPlayers;
    }

    /**
     * This method should return a map containing all the players who are present in the session.
     *
     * @return The player list
     */
    @Nonnull
    @Override
    public List<Player> getSessionPlayers() {

        List<Player> newList = new ArrayList<>();
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
    public void scheduleTask(int timeInSeconds, Runnable runnable, GameNotifications notificationType) {

        notificationTime = timeInSeconds;

        Utilities.ifNotNull(sessionTask, Task::cancel);
        Utilities.ifNotNull(notificationTask, Task::cancel);

        if(timeInSeconds <= 0) { return; }

        sessionTask = Task.builder().delay(timeInSeconds, TimeUnit.SECONDS).execute(runnable).submit(Polarity.getPolarity());
        notificationTask = Task.builder().interval(1, TimeUnit.SECONDS).delay(0, TimeUnit.SECONDS).execute(() ->{

            this.notifyTime(notificationTime, notificationType);
            notificationTime--;

        }).submit(Polarity.getPolarity());

    }

    /**
     * This method should get called to make a player join the session.
     *
     * @param player            The target player
     * @param playerSessionRole The player's role
     */
    @Override
    public void joinSession(Player player, PlayerSessionRole playerSessionRole) {

        if(getSessionPlayers().contains(player)) { return; }

        List<Player> actualPlayers = new ArrayList<>();

        if(Polarity.getPartyManager().getPlayerParty(player).isPresent()) {

            GameParty party = Polarity.getPartyManager().getPlayerParty(player).get();

            actualPlayers.addAll(party.getPartyPlayers());

        }
        else{

            actualPlayers.add(player);

        }

        if(getLobby().isValidLobby()){

            if(playerSessionRole.equals(PlayerSessionRole.PLAYER)){

                if(actualPlayers.size() + getActivePlayers().size() > getProperties().getMaxPlayers()){

                    if(Polarity.getPartyManager().getPlayerParty(player).isPresent()) {

                        player.sendMessage(Text.of(TextColors.RED, "There are too many players in your party to join this lobby"));

                    }
                    else {

                        player.sendMessage(Text.of(TextColors.RED, "This lobby is full. You may only join as a spectator"));

                    }

                    return;

                }

            }

            Integer teamID = getNextFreeTeamID();

            for(Player actualPlayer : actualPlayers){

                if(!actualPlayer.setLocation(new Location<>(getLobby().getLobbyWorld(), getLobby().getLobbyWorld().getProperties().getSpawnPosition()))) { return; }

                Utilities.savePlayerInventory(actualPlayer);
                Utilities.removePotionEffects(actualPlayer);
                Utilities.restoreMaxHealth(actualPlayer);
                player.getInventory().clear();

                switch (playerSessionRole){

                    case SPECTATOR:

                        getSpectatingPlayers().add(actualPlayer);
                        Utilities.setGameMode(actualPlayer, GameModes.SPECTATOR);
                        break;

                    case PLAYER:

                        activePlayers.put(actualPlayer, teamID);
                        for(Player presentPlayer : getSessionPlayers()){

                            presentPlayer.sendMessage(Text.of(TextColors.AQUA, "[", getGame().getGameName(), "] | ", actualPlayer.getName(), " joined the lobby (", getActivePlayers().size(), "/", getProperties().getMaxPlayers(), ")"));

                        }
                        break;

                }

                onPlayerJoinedSession(actualPlayer, playerSessionRole);

            }

            if(getActivePlayers().size() == getProperties().getMaxPlayers()){

                setState(GameSessionState.LAUNCHING);
                scheduleTask(10, this::launchGame, GameNotifications.LOBBY_LAUNCH);

            }
            else if(getActivePlayers().size() >= getProperties().getMinPlayers()){

                setState(GameSessionState.LAUNCHING);
                scheduleTask(60, this::launchGame, GameNotifications.LOBBY_LAUNCH);

            }

        }
        else if(getGame().isValidGame()){

            for(Player actualPlayer : actualPlayers){

                getSpectatingPlayers().add(actualPlayer);
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

        if(!getSessionPlayers().contains(player)) { return; }

        getPlayerTeams().remove(player);
        getSpectatingPlayers().remove(player);

        if(getLobby().isValidLobby()){

            for(Player sessionPlayer : getSessionPlayers()){

                sessionPlayer.sendMessage(Text.of(TextColors.RED, "[", getGame().getGameName(), "] | ", player.getName(), " left the lobby."));

            }

            if(getActivePlayers().size() < getProperties().getMinPlayers()){

                getSessionTask().ifPresent(Task::cancel);
                getNotificationTask().ifPresent(Task::cancel);

                sessionTask = null;
                notificationTask = null;

                setState(GameSessionState.WAITING_FOR_PLAYERS);

            }

        }
        else if (getGame().isValidGame()) { getGame().handlePlayerLeft(player); }

        player.getInventory().clear();
        Utilities.removePotionEffects(player);
        Utilities.restoreMaxHealth(player);

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
            if(Arrays.asList(60, 30, 10, 5, 4, 3, 2, 1).contains(timeInSeconds)){

                for(Player player : getLobby().getLobbyWorld().getPlayers()){

                    player.sendTitle(Title.builder().title(Text.of(TextColors.AQUA, getGame().getGameName()))
                            .subtitle(Text.of(TextColors.AQUA, "Game starts in ", timeInSeconds, " seconds"))
                            .actionBar(Text.EMPTY).fadeIn(5).fadeOut(5).stay(30).build());

                    player.playSound(SoundTypes.BLOCK_NOTE_PLING, player.getPosition(), 0.5);

                }

            }

        }

    }

    /**
     * This method should handle the game initialization logic
     */
    @Override
    public void launchGame() {

        getLobby().destroyLobby();
        getGame().setupPreGame(activePlayers, getSpectatingPlayers());

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

        Sponge.getEventManager().unregisterListeners(this);

        Utilities.ifNotNull(notificationTask, Task::cancel);
        Utilities.ifNotNull(sessionTask, Task::cancel);
        Utilities.ifNotNull(lobby, GameLobby::destroyLobby);
        Utilities.ifNotNull(game, GameInstance::destroyGame);

        notificationTask = null;
        sessionTask = null;

        Utilities.ifNotNull(source, (object) -> getGameManager().removeSession(this));

        for(Player player: this.getSessionPlayers()){

            Utilities.removePotionEffects(player);
            this.printRestoreMessage(player);

        }

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

    private Integer getNextFreeTeamID(){

        List<Integer> existingIDs = new ArrayList<>(activePlayers.values());

        Collections.sort(existingIDs);

        int old = -1;

        for(Integer num : existingIDs){

            Polarity.getLogger().info(String.valueOf(num));

            if(old + 1 != num){

                return old + 1;

            }

            old = num;

        }

        return old + 1;

    }

    @Listener
    public final void onDimensionChange_Post(PlayerChangeDimensionEvent.Post event){

        if(getRelevantWorld().getUniqueId().equals(event.getFromWorld().getUniqueId())){

            onPlayerLeftSession(event.getTargetEntity());

        }

    }

    @Listener
    public void onPlayerMove(MoveEntityEvent event, @First Player eventPlayer){

        if(event.getToTransform().getExtent().getUniqueId().equals(getRelevantWorld().getUniqueId())){

            if(getState().equals(GameSessionState.PRE_GAME)){

                event.setToTransform(eventPlayer.getTransform());

            }

            if(event.getToTransform().getPosition().getY() <= 5.0f){

                event.getTargetEntity().setLocation(new Location<>(getRelevantWorld(), getRelevantWorld().getProperties().getSpawnPosition()));

                if(getGame().isValidGame()){

                    getGame().eliminatePlayer(eventPlayer, Cause.of(EventContext.empty(), getGame()));

                }

            }

        }

    }

    @Listener
    public void onDisconnect(@Nonnull ClientConnectionEvent.Disconnect event){

        if(getRelevantWorld().getUniqueId().equals(event.getTargetEntity().getWorld().getUniqueId())){

            PolarityWarp.warp(event.getTargetEntity(), "Hub", Polarity.getPolarity());
            onPlayerLeftSession(event.getTargetEntity());

        }

    }

    @Listener
    public void onKick(KickPlayerEvent event){

        if(getRelevantWorld().getUniqueId().equals(event.getTargetEntity().getWorld().getUniqueId())){

            PolarityWarp.warp(event.getTargetEntity(), "Hub", Polarity.getPolarity());
            onPlayerLeftSession(event.getTargetEntity());

        }

    }

    @Listener
    public void onPlayerDamage(DamageEntityEvent event, @First Player eventPlayer) {

        if (getRelevantWorld().getUniqueId().equals(eventPlayer.getWorld().getUniqueId())) {

            if(getState().equals(GameSessionState.FINISHING) || getState().equals(GameSessionState.OVER)){

                event.setCancelled(true);
                return;

            }

            if (getGame().isValidGame() && eventPlayer.health().get() <= event.getFinalDamage()) {

                event.setCancelled(true);
                getGame().handlePlayerDeath(eventPlayer);

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

            if(snap.getWorldUniqueId().equals(getRelevantWorld().getUniqueId())){

                event.setCancelled(true);

            }

        }

    }

}
