/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import io.github.nucleuspowered.nucleus.api.annotations.MightOccurAsync;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.Optional;

import javax.annotation.Nullable;

public interface NucleusAFKEvent extends TargetPlayerEvent {

    /**
     * Gets the original message that would have been sent to players, if any.
     *
     * @return The message, if any
     */
    Optional<Text> getOriginalMessage();

    /**
     * Gets the message to send to players
     *
     * @return The message, if any.
     */
    Optional<Text> getMessage();

    /**
     * Sets the message to send to players, if any.
     *
     * @param message The message. A null message suppresses the message.
     */
    void setMessage(@Nullable Text message);

    /**
     * Gets the original message channel to send the message to
     *
     * @return The {@link MessageChannel}
     */
    MessageChannel getOriginalChannel();

    /**
     * Gets the channel to send the message to
     *
     * @return The {@link MessageChannel}
     */
    MessageChannel getChannel();

    /**
     * Sets the message channel to send the message to
     *
     * @param channel The {@link MessageChannel}
     */
    void setChannel(MessageChannel channel);

    /**
     * Fired when a player goes AFK.
     *
     * <p>
     *     <strong>This event might fire async!</strong>
     * </p>
     */
    @MightOccurAsync
    interface GoingAFK extends NucleusAFKEvent {}

    /**
     * Fired when a player returns from AFK.
     *
     * <p>
     *     <strong>This event might fire async!</strong>
     * </p>
     */
    @MightOccurAsync
    interface ReturningFromAFK extends NucleusAFKEvent {}

    /**
     * Fired when a player is about to be kicked due to inactivity.
     *
     * <p>
     *     If this event is cancelled, the player will not be kicked for inactivity until the player comes back from AFK and goes AFK again.
     * </p>
     * <p>
     *     <strong>This event might fire async!</strong>
     * </p>
     */
    @MightOccurAsync
    interface Kick extends NucleusAFKEvent, Cancellable {}

    /**
     * Fired when the target of a command is AFK and the command is marked
     * as one that should notify the sender.
     */
    @MightOccurAsync
    interface NotifyCommand extends TargetPlayerEvent {

        /**
         * Gets the original message that would have been sent to players, if any.
         *
         * @return The message, if any
         */
        Optional<Text> getOriginalMessage();

        /**
         * Gets the message to send to the command invoker
         *
         * @return The message, if any.
         */
        Optional<Text> getMessage();

        /**
         * Sets the message to send to the command invoker, if any.
         *
         * @param message The message. A null message suppresses the message.
         */
        void setMessage(@Nullable Text message);
    }
}
