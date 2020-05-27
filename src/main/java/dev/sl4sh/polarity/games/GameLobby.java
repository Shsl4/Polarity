package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.Polarity;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * This interface is the base object for {@link Polarity}'s game lobbies.
 * No implementation is provided and it is recommended to inherit {@link GameLobbyBase} instead if you want to create a game lobby
 */
public interface GameLobby {

    /**
     * This method should return the world the lobby is located in
     * @return The lobby's world
     */
    Optional<World> getLobbyWorld();

    /**
     * This method should handle the lobby destruction
     */
    void destroyLobby();

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
