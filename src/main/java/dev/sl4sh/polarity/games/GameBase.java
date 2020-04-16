package dev.sl4sh.polarity.games;

import com.flowpowered.math.vector.Vector3d;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.enums.games.GameState;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * This interface is the base object for {@link Polarity}'s games.
 * No implementation is provided and it is recommended to inherit {@link AbstractGame} instead if you want to create a custom game
 */
public interface GameBase {

    /**
     * This method should return the world the game is occurring in
     * @return The game's world
     */
    World getGameWorld();

    /**
     * This method should return a list containing all the players who are actively playing (alive / participating)
     * @return The player list
     */
    @Nonnull
    List<Player> getActivePlayers();

    /**
     * This method should return a list containing all the players who are spectating the game
     * @return The player list
     */
    @Nonnull
    List<Player> getSpectatingPlayers();

    /**
     * This method should return a valid spawn location for a player when the game starts
     * @param player The actual player
     * @return The spawn location
     */
    @Nonnull
    Vector3d getSpawnLocationForPlayer(Player player);

    /**
     * This method should return the game's name. Used for displaying
     * @return The game's name
     */
    @Nonnull
    String getGameName();

    /**
     * This method should return a scheduler {@link Task} which should call {@link #onGameEnd()} on execution
     * @return An optional value of a task
     */
    Optional<Task> getGameTimerTask();

    /**
     * This method should fire when a player joins the game
     * @param player The joining player
     */
    void onPlayerJoinedGame(Player player);

    /**
     * This method should fire when a player leaves the game
     * @param player The leaving player
     */
    void onPlayerLeftGame(Player player);

    /**
     * This method should be called to notify when a certain amount of time is remaining before the game starts
     * @param timeInSeconds The actual time remaining before the game should start
     */
    void notifyTimeBeforeStart(double timeInSeconds);

    /**
     * This method should be called to notify when a certain amount of time is remaining before the game ends
     * @param timeInSeconds The actual time remaining before the game should end
     */
    void notifyTimeBeforeEnd(double timeInSeconds);

    /**
     * This method should handle a player's elimination
     * @param player The eliminated player
     */
    void eliminatePlayer(Player player);

    /**
     * This method should return how many time the game lasts (in seconds)
     * @return The game time in seconds
     */
    long getGameTimeInSeconds();

    /**
     * This method should handle the game's initialization
     * @param players The participating players
     */
    void setupPreGame(List<Player> players);

    /**
     * This method should handle the game start
     */
    void startGame();

    /**
     * This method should handle the game end.
     */
    void onGameEnd();

    /**
     * This method should return the game's active state
     * @return The game's state
     */
    GameState getState();

    /**
     * This method should return the maximal amount of players in the game
     * @return The maximal player amount
     */
    int getMaxPlayers();

    /**
     * This method should return true if the game can be used in any way
     * @return Whether the game is considered valid or not
     */
    boolean isValidGame();

    /**
     * This method should declare the game as unusable / invalid {@link #isValidGame()}
     */
    void invalidateGame();

    /**
     * This method should handle the game destruction
     */
    void destroyGame();

}
