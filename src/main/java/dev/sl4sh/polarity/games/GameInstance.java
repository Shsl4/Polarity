package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.enums.games.GameNotifications;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * This interface is the base object for {@link Polarity}'s games.
 * No implementation is provided and it is recommended to inherit {@link AbstractGameInstance} instead if you want to create a custom game
 */
public interface GameInstance {

    Team EMPTY_TEAM = Team.builder().name("INVALID").build();

    /**
     * This method should return the world the game is occurring in
     * @return The game's world
     */
    Optional<World> getGameWorld();

    /**
     * This method should return the game's name. Used for displaying
     * @return The game's name
     */
    @Nonnull
    String getGameName();

    /**
     * This method should return a reference to the {@link GameSession} this game instance is part of.
     * @return The {@link GameSession}
     */
    @Nonnull
    GameSession<?> getSession();

    /**
     * This method should return the unique game type identifier.
     * @return The game type ID
     */
    int getGameID();

    /**
     * This method should return how many time the game lasts (in seconds)
     * @return The game time in seconds
     */
    int getGameTimeInSeconds();

    void rewardPlayers();

    /**
     * This method should return a color that will be used to color displayed texts
     * @return The game's color
     */
    TextColor getGameTintColor();

    /**
     * This method should return the GameMode players should play in
     * @return The GameMode
     */
    GameMode getMode();

    /**
     * This method should return the GameMode spectators should spectate in
     * @return The GameMode
     */
    GameMode getSpectatorMode();

    /**
     * This method should set the players spawn locations when the game starts
     */
    void setPlayersSpawnLocations();

    /**
     * This method should set the spectators spawn locations when the game starts
     */
    void setSpectatorsSpawnLocations();

    /**
     * This method should handle when a player joins the game.
     * @param player The player who joined.
     */
    void handlePlayerJoin(Player player, PlayerSessionRole mode);

    /**
     * This method should handle when a player leaves the game.
     * @param player The player who left.
     */
    void handlePlayerLeft(Player player);

    /**
     * This method should handle when a player dies in the game dimension.
     * @param player The player who died.
     */
    void handlePlayerDeath(Player player, Object source);

    /**
     * This method should handle the game start
     */
    void handleGameStart();

    /**
     * This method should handle the game end.
     */
    void handleGameEnd();

    /**
     * This method will get fired from the game session {@link GameSession#notifyTime(int, GameNotifications)}. Anything could be done here.
     * @param timeInSeconds The time before {@link GameSession#getSessionTask()}'s execution
     * @param notificationType The notification type
     */
    void notifyTime(int timeInSeconds, GameNotifications notificationType);

    /**
     * This method should handle a player's elimination
     * @param player The eliminated player
     * @param hasLeft Whether the player has left the game
     */
    void eliminatePlayer(Player player, Object source, boolean hasLeft);

    /**
     * This method should handle the game's initialization
     */
    void setupPreGame();

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
