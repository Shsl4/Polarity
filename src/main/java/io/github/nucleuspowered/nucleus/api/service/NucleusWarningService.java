/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import io.github.nucleuspowered.nucleus.api.nucleusdata.Warning;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;

import java.time.Duration;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A service that allows plugins to obtain information about user warnings.
 */
public interface NucleusWarningService {

    /**
     * Adds a warning to a player.
     *
     * @param toWarn The user to warn.
     * @param warner The {@link CommandSource} to attribute the warning to.
     * @param reason The reason for the warning.
     * @param duration The {@link Duration} the warning should last for from this moment before it expires. Set to <code>null</code> if infinite.
     * @return <code>true</code> if successful.
     */
    boolean addWarning(User toWarn, CommandSource warner, String reason, @Nullable Duration duration);

    /**
     * Gets all warnings (active and expired) for a {@link User}
     *
     * @param user The {@link User} to get the warnings for.
     * @return The {@link Warning}s.
     */
    List<Warning> getWarnings(User user);

    /**
     * Expires (or removes) a warning.
     *
     * @param user The {@link User} to remove the warning from.
     * @param warning The {@link Warning} to remove.
     * @param cause The {@link Cause}. This should normally have your plugin as the root, but can be a player if it is due to a direct
     * action of that player.
     * @return <code>true</code> if successful.
     */
    boolean expireWarning(User user, Warning warning, Cause cause);
}
