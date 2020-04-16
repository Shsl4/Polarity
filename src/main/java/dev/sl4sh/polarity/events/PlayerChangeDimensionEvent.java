package dev.sl4sh.polarity.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;

public abstract class PlayerChangeDimensionEvent extends AbstractEvent implements TargetPlayerEvent {

    @Nonnull
    private final Player targetPlayer;
    @Nonnull
    private final World fromWorld;
    @Nonnull
    private final World toWorld;
    @Nonnull
    private final Object source;

    public PlayerChangeDimensionEvent(@Nonnull Player targetPlayer, @Nonnull World fromWorld, @Nonnull World toWorld, @Nonnull Object source) {

        this.targetPlayer = targetPlayer;
        this.fromWorld = fromWorld;
        this.toWorld = toWorld;
        this.source = source;

    }

    @Override
    public Player getTargetEntity() {
        return targetPlayer;
    }

    @Nonnull
    public World getFromWorld() {
        return fromWorld;
    }

    @Nonnull
    public World getToWorld() {
        return toWorld;
    }

    @Override
    public Cause getCause() {
        return Cause.of(EventContext.empty(), source);
    }

    @Override
    @Nonnull
    public Object getSource() {
        return source;
    }

    public static class Pre extends PlayerChangeDimensionEvent implements Cancellable{

        public Pre(@Nonnull Player targetPlayer, @Nonnull World fromWorld, @Nonnull World toWorld, @Nonnull Object source) {
            super(targetPlayer, fromWorld, toWorld, source);
        }

        private boolean isCancelled;
        private Text reason = Text.of();

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            isCancelled = cancel;
        }

        public void setCancelReason(Text reason) {
            this.reason = reason;
        }

        public Text getCancelReason() {
            return reason;
        }

    }

    public static class Post extends PlayerChangeDimensionEvent {
        public Post(@Nonnull Player targetPlayer, @Nonnull World fromWorld, @Nonnull World toWorld, @Nonnull Object source) {
            super(targetPlayer, fromWorld, toWorld, source);
        }
    }

}
