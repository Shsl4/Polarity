package dev.sl4sh.polarity.events;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;

public abstract class PlayerWarpEvent extends AbstractEvent implements TargetPlayerEvent {

    @Nonnull
    Player targetPlayer;
    @Nonnull
    String warpName;
    @Nonnull
    Object source;

    public PlayerWarpEvent(@Nonnull Player targetPlayer, @Nonnull String warpName, @Nonnull Object source) {

        this.targetPlayer = targetPlayer;
        this.warpName = warpName;
        this.source = source;

    }

    @Nonnull
    public String getWarpName() {
        return warpName;
    }

    @Nonnull
    public World getWorld() {
        return targetPlayer.getWorld();
    }

    @Nonnull
    @Override
    public Cause getCause() {
        return Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, Sponge.getPluginManager().getPlugin("polarity").get()).build(), source);
    }

    @Nonnull
    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public Player getTargetEntity() {
        return targetPlayer;
    }

    public static class Pre extends PlayerWarpEvent implements Cancellable{

        boolean cancelled;
        Text reason = Text.of();

        public Pre(@Nonnull Player targetPlayer, @Nonnull String warpName, @Nonnull Object source) {
            super(targetPlayer, warpName, source);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }

        public void setCancelReason(Text reason) {
            this.reason = reason;
        }

        public Text getCancelReason() {
            return reason;
        }

    }

    public static class Post extends PlayerWarpEvent{

        public Post(@Nonnull Player targetPlayer, @Nonnull String warpName, @Nonnull Object source) {
            super(targetPlayer, warpName, source);
        }

    }



}
