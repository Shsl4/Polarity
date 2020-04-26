/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import io.github.nucleuspowered.nucleus.api.nucleusdata.MuteInfo;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;

import java.time.Duration;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Provides methods for managing mutes within Nucleus
 */
public interface NucleusMuteService {

    /**
     * The permission context key that indicates a player is muted.
     *
     * <p>The value of this context will always be true if set.</p>
     */
    String MUTED_CONTEXT = "nucleus_muted";

    /**
     * Gets whether a {@link User} is muted.
     *
     * @param user The {@link User} to check.
     * @return <code>true</code> if so.
     */
    boolean isMuted(User user);

    /**
     * Gets the {@link MuteInfo} about a player.
     *
     * @param user The {@link User} to check
     * @return The {@link MuteInfo}, if applicable.
     */
    Optional<MuteInfo> getPlayerMuteInfo(User user);

    /**
     * Mutes a player.
     *
     * @param user The {@link User} to mute.
     * @param reason The reason to mute them for.
     * @param duration The length of time to mute for, or <code>null</code> for indefinite.
     * @param cause The {@link Cause} of the mute. The first {@link User} in the cause will be designated as the muter.
     * @return <code>true</code> if the user was muted, <code>false</code> if the user was already muted.
     */
    boolean mutePlayer(User user, String reason, @Nullable Duration duration, Cause cause);

    /**
     * Unmutes a player.
     *
     * @param user The {@link User} to unmute.
     * @param cause The {@link Cause}
     * @return <code>true</code> if the player was unmuted.
     */
    boolean unmutePlayer(User user, Cause cause);
}
