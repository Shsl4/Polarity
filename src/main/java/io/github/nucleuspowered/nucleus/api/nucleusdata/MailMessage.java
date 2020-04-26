/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.nucleusdata;

import org.spongepowered.api.entity.living.player.User;

import java.time.Instant;
import java.util.Optional;

/**
 * Represents a mail message.
 */
public interface MailMessage {

    /**
     * The message that was sent.
     *
     * @return The message.
     */
    String getMessage();

    /**
     * The time the message was sent.
     *
     * @return The {@link Instant}
     */
    Instant getDate();

    /**
     * The sender of the message, or {@link Optional#empty()} if it wasn't a player.
     *
     * @return The sender.
     */
    Optional<User> getSender();
}
