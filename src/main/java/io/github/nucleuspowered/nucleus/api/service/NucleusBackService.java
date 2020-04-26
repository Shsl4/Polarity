/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * A service that handles the subject's last location before a warp, that is, the location they will warp to if they
 * run /back.
 *
 * <p>
 *     A subject's last location may not be set. It is not saved across server restarts, and may be discarded at any
 *     time the user is not online.
 * </p>
 */
public interface NucleusBackService {

    /**
     * Gets the location of the subject before they executed the last warp that was marked as Returnable.
     *
     * @param user The {@link User}
     * @return If it exists, an {@link Optional} containing the {@link Transform}
     */
    Optional<Transform<World>> getLastLocation(User user);

    /**
     * Sets the location that the subject will be warped to if they execute /back
     *  @param user The {@link User}
     * @param location The {@link Location} to set as the /back target.
     */
    void setLastLocation(User user, Transform<World> location);

    /**
     * Removes the last location from the subject, so that /back will not work for them.
     *
     * @param user The {@link User}
     */
    void removeLastLocation(User user);

    /**
     * Gets a value indicating whether the user will have their last location logged.
     *
     * @param user The {@link User}
     *
     * @return <code>true</code> if it is being logged.
     */
    boolean isLoggingLastLocation(User user);

    /**
     * Sets whether the user will have their last location logged.
     *
     * @param user The {@link User}
     * @param log Whether to log the user's last location.
     */
    void setLoggingLastLocation(User user, boolean log);
}
