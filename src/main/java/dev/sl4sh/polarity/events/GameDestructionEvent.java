package dev.sl4sh.polarity.events;

import dev.sl4sh.polarity.games.GameBase;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

import javax.annotation.Nonnull;

public class GameDestructionEvent extends AbstractEvent {

    @Nonnull
    private final GameBase targetGame;
    @Nonnull
    private final Object source;

    public GameDestructionEvent(@Nonnull GameBase targetGame, @Nonnull Object source) {

        this.targetGame = targetGame;
        this.source = source;

    }

    @Nonnull
    @Override
    public Cause getCause() {
        return Cause.of(EventContext.empty(), source);
    }

    @Nonnull
    @Override
    public Object getSource() {
        return source;
    }

    @Nonnull
    public GameBase getTargetGame() {
        return targetGame;
    }

}
