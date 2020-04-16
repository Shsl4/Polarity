package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.Polarity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * This interface is the base object for {@link Polarity}'s game lobbies.
 * No implementation is provided and it is recommended to inherit {@link AbstractGameLobby} instead if you want to create a game lobby
 * @param <T> The lobby's associated {@link GameBase} object
 */
public interface GameLobbyBase<T extends GameBase> {

    /**
     * This method should return the world the lobby is located in
     * @return The lobby's world
     */
    World getLobbyWorld();

    /**
     * This method should return the lobby's name. Used for displaying
     * @return The lobby's name
     */
    @Nonnull
    String getLobbyName();

    /**
     * This method should return a list containing all the players who will be playing the game
     * @return The player list
     */
    @Nonnull
    List<Player> getRegisteredPlayers();

    /**
     * This method should return the game object associated with the lobby
     * @return The actual game object
     */
    @Nonnull
    T getGame();

    /**
     * This method should return a scheduler {@link Task} which should call {@link #launchGame()} on execution
     * @return An optional value of a task
     */
    @Nonnull
    Optional<Task> getGameLaunchTask();

    /**
     * This method should fire when a player joins the lobby
     * @param player The joining player
     */
    void onPlayerJoinedLobby(Player player);

    /**
     * This method should fire when a player leaves the lobby
     * @param player The leaving player
     */
    void onPlayerLeftLobby(Player player);

    /**
     * This method should be called to notify when a certain amount of time is remaining before the game launches
     * @param timeInSeconds The actual time remaining before the game should launch
     */
    void notifyTimeBeforeLaunch(double timeInSeconds);

    /**
     * This method should handle the game initialization logic
     */
    void launchGame();

    /**
     * This method should handle the lobby destruction
     */
    void destroyLobby();

    /**
     * This method should try to register a player who will participate in the game
     * @param player The actual player
     * @return Whether the player was successfully registered or not
     */
    boolean registerPlayer(Player player);

    /**
     * This method should try to unregister a player who was supposed to participate in the game
     * @param player The actual player
     * @return Whether the player was successfully unregistered or not
     */
    boolean unRegisterPlayer(Player player);

    /**
     * This method should return the maximal amount of players in the game
     * @return The maximal player amount
     */
    int getMaxPlayers();

    /**
     * This method should return the minimal amount of players before starting scheduling the {@link GameLobbyBase#getGameLaunchTask()}
     * @return The minimal player amount
     */
    int getMinPlayersToStart();

    /**
     * This method should return true if the lobby can be used in any way
     * @return Whether the lobby is considered valid or not
     */
    boolean isValidLobby();

    /**
     * This method should declare the lobby as unusable / invalid {@link #isValidLobby()}
     */
    void invalidateLobby();

}
