package dev.sl4sh.polarity.games.rush;

import dev.sl4sh.polarity.games.AbstractGameSession;
import dev.sl4sh.polarity.games.GameManager;
import dev.sl4sh.polarity.games.SessionProperties;
import dev.sl4sh.polarity.games.arena.ArenaGameInstance;

import javax.annotation.Nonnull;
import java.util.Random;

public class RushGameSession extends AbstractGameSession<RushGameInstance> {

    public RushGameSession(GameManager gameManager, int sessionID, @Nonnull SessionProperties properties) throws IllegalStateException {
        super(gameManager, sessionID, properties);
    }

    /**
     * This method should create the game that will be fetched with {@link #getGame()} ()}.
     *
     * @return The created game
     * @throws IllegalStateException If a game construction error occurs
     */
    @Override
    public RushGameInstance createGame() throws IllegalStateException {
        return new RushGameInstance(this.getProperties().getGameMapNames().get(new Random().nextInt(this.getProperties().getGameMapNames().size())), this);
    }
}
