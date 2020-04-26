/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.user.TargetUserEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.time.Duration;
import java.util.Optional;

/**
 * Called when a {@link User} is jailed or unjailed.
 */
@NonnullByDefault
public interface NucleusJailEvent extends TargetUserEvent {

    /**
     * Fired when a player is jailed.
     */
    interface Jailed extends NucleusJailEvent {

        /**
         * The name of the jail the player is sent to.
         *
         * @return The name.
         */
        String getJailName();

        /**
         * How long the player is jailed for.
         *
         * @return The {@link Duration}, if applicable.
         */
        Optional<Duration> getDuration();

        /**
         * The reason for the jailing.
         *
         * @return The reason.
         */
        Text getReason();
    }

    /**
     * Fired when a player is unjailed.
     */
    interface Unjailed extends NucleusJailEvent {}
}
