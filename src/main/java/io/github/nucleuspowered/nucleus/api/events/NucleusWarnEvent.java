/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.user.TargetUserEvent;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Events for when players are warned.
 */
@NonnullByDefault
public interface NucleusWarnEvent extends TargetUserEvent {

    /**
     * The reason for the warning.
     *
     * @return The reason
     */
    String getReason();

    /**
     * Fired when a player has been warned.
     */
    interface Warned extends NucleusWarnEvent {

        /**
         * If applicable, how long until the warning expires.
         *
         * @return The time until expiry for the warning.
         */
        Optional<Duration> getTimeUntilExpiration();
    }

    /**
     * Fired when a warning expires.
     *
     * <p>
     *     Note: the {@link #getCause()} will return an instance of the plugin if the cause was automatic, else it will contain the
     *     {@link CommandSource} expiring the warning.
     * </p>
     */
    interface Expired extends NucleusWarnEvent {

        /**
         * The UUID of the entity that originally warned the user, or {@link Optional#empty()} if the
         * console warned the user.
         *
         * @return The {@link UUID}, or an empty
         */
        Optional<UUID> getWarner();
    }
}
