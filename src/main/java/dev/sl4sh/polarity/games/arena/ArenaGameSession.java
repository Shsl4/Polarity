package dev.sl4sh.polarity.games.arena;

import dev.sl4sh.polarity.games.AbstractGameSession;
import dev.sl4sh.polarity.games.GameManager;
import dev.sl4sh.polarity.games.SessionProperties;

import javax.annotation.Nonnull;
import java.util.Random;

public class ArenaGameSession extends AbstractGameSession<ArenaGameInstance> {

    public ArenaGameSession(GameManager gameManager, int sessionID, @Nonnull SessionProperties properties) throws IllegalStateException {
        super(sessionID, properties);
    }

    @Override
    public ArenaGameInstance createGame() throws IllegalStateException {
        return new ArenaGameInstance(this.getProperties().getGameMapNames().get(new Random().nextInt(this.getProperties().getGameMapNames().size())), this);
    }

    @Override
    protected void setupScoreboard() {

    }

}
