package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.enums.games.GameNotifications;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GameSession<T extends GameInstance> {

    /**
     * This method should return the session's assigned {@link GameLobby} object.
     * @return The actual object
     */
    GameLobby getLobby();

    /**
     * This method should return the session's assigned {@link GameInstance} object.
     * @return The actual object
     */
    T getGame();

    /**
     * This method should return the session's relevant world. (Example: The lobby world if waiting for players, the game world if playing...)
     * @return The relevant world
     */
    World getRelevantWorld();

    /**
     * This method should return the session's active state (Example: Waiting for players, running...)
     * @return The session's state
     */
    GameSessionState getState();

    /**
     * This method should return the session's ID. A session ID may only be used on one session at a time so it can be identified by a {@link GameManager}.
     * @return The session ID
     */
    int getSessionID();

    /**
     * This method should return a task that has been scheduled to run a session's essential function (Example: Start the game, End the game, Destroy the session)
     * @return The session's task
     */
    @Nonnull
    Optional<Task> getSessionTask();

    /**
     * This method should return a task that loops and calls {@link #notifyTime(int, GameNotifications)} every second until the {@link #getSessionTask()}'s execution, if scheduled.
     * @return The notification task
     */
    @Nonnull
    Optional<Task> getNotificationTask();


    /**
     * This method should return the {@link #getGame()}'s actively participating players. (Example: Alive players, Waiting for respawn players...)
     * @return The active players.
     */
    List<Player> getActivePlayers();

    /**
     * This method should return the active players associated with their team ID
     * @return The players
     */
    Map<Player, Integer> getPlayerTeams();

    /**
     * This method should remove a player from the active players list.
     * @param player The player to remove
     */
    void removeActivePlayer(Player player);

    /**
     * This method should return the players who will / are spectating the game
     * @return The spectating players
     */
    List<Player> getSpectatingPlayers();

    /**
     * This method should return a map containing all the players who are present in the session.
     * @return The player list
     */
    @Nonnull
    List<Player> getSessionPlayers();

    /**
     * This method should return the session's properties (Should be final). See {@link SessionProperties}.
     * @return The session's properties
     */
    @Nonnull
    SessionProperties getProperties();

    /**
     * This method should set the session's active state (Example: Waiting for players, running...)
     * @param state The new {@link GameSessionState} value
     */
    void setState(GameSessionState state);

    /**
     * This method should create the lobby that will be fetched with {@link #getLobby()}.
     * @return The created lobby
     * @throws IllegalStateException If a lobby construction error occurs
     */
    GameLobby createLobby() throws IllegalStateException;

    /**
     * This method should create the game that will be fetched with {@link #getGame()} ()}.
     * @return The created game
     * @throws IllegalStateException If a game construction error occurs
     */
    T createGame() throws IllegalStateException;

    /**
     * This method should schedule {@link #getSessionTask()} and {@link #getNotificationTask()} with {@param timeInSeconds} delay.
     * @param timeInSeconds The time in seconds before {@param runnable}'s execution
     * @param runnable The action to execute at the end of the time
     * @param notificationType The type of notification that will be sent to {@link #notifyTime(int, GameNotifications)}
     */
    void scheduleTask(int timeInSeconds, Runnable runnable, GameNotifications notificationType);

    /**
     * This method should get called to make a player join the session.
     * @param player The target player
     * @param playerSessionRole The player's role
     */
    void joinSession(Player player, PlayerSessionRole playerSessionRole);

    /**
     * This method should fire when a player joins the session.
     * @param player The player who joined.
     */
    void onPlayerJoinedSession(Player player, PlayerSessionRole playerSessionRole);

    /**
     * This method should fire when a player left the session.
     * @param player The player who left
     */
    void onPlayerLeftSession(Player player);

    /**
     * This method should fire every second when {@link #getSessionTask()} has been scheduled with the appropriate information.
     * @param timeInSeconds The time before {@link #getSessionTask()}'s execution
     * @param notificationType The notification type
     */
    void notifyTime(int timeInSeconds, GameNotifications notificationType);

    /**
     * This method should handle the game initialization logic
     */
    void launchGame();

    /**
     * This method should destroy {@link #getGame()} & {@link #getLobby()} and mark the session as unusable
     * @param source An optional source of the destruction
     */
    void endSession(@Nullable Object source);

}
