/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.nucleusdata;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides information about a player's mute.
 */
public interface MuteInfo extends TimedEntry {

    /**
     * The reason for the mute.
     * @return The reason.
     */
    String getReason();

    /**
     * The {@link UUID} of the muter, or {@link Optional#empty()} if the muter was not a player.
     * @return The {@link UUID} of the muter, if applicable.
     */
    Optional<UUID> getMuter();

    /**
     * Gets the {@link Instant} this player was muted, if this information was recorded.
     *
     * @since 0.27
     *
     * @return The instant, if known.
     */
    Optional<Instant> getCreationInstant();
}
