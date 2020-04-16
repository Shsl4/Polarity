package dev.sl4sh.polarity.events;

import dev.sl4sh.polarity.games.GameLobbyBase;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

import javax.annotation.Nonnull;

public class LobbyDestructionEvent extends AbstractEvent {

    @Nonnull
    private final GameLobbyBase<?> targetLobby;
    @Nonnull
    private final Object source;

    public LobbyDestructionEvent(@Nonnull GameLobbyBase<?> targetLobby, @Nonnull Object source) {

        this.targetLobby = targetLobby;
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
    public GameLobbyBase<?> getTargetLobby() {
        return targetLobby;
    }
}
