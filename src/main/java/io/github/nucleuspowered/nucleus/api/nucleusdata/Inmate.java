/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.nucleusdata;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents information about a player's jailing.
 */
public interface Inmate extends TimedEntry {

    /**
     * The reason a player was jailed.
     *
     * @return The reason.
     */
    String getReason();

    /**
     * The jail the player is jailed in.
     *
     * @return The name of the jail.
     */
    String getJailName();

    /**
     * The {@link UUID} of the jailer, or {@link Optional#empty()} if it was the console.
     *
     * @return The jailing entities {@link UUID}, or {@link Optional#empty()} if it was the console.
     */
    Optional<UUID> getJailer();

    /**
     * The previous location of the player - which is where they will be sent to upon unjailing.
     *
     * @return The previous location of the player, if known.
     */
    Optional<Location<World>> getPreviousLocation();

    /**
     * Gets the {@link Instant} this inmate was jailed, if this information was recorded.
     *
     * @since 0.27
     *
     * @return The instant, if known.
     */
    Optional<Instant> getCreationInstant();
}
