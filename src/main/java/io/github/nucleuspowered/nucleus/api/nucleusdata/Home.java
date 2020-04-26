/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.nucleusdata;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.UUID;

/**
 * Represents a home in Nucleus.
 */
public interface Home extends NamedLocation {

    /**
     * The {@link UUID} of the user.
     *
     * @return The {@link UUID}
     */
    UUID getOwnersUniqueId();

    /**
     * Gets the {@link User} who owns this home.
     *
     * @return The {@link User}
     */
    default User getUser() {
        return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(getOwnersUniqueId()).orElseThrow(() -> new IllegalStateException("user"));
    }
}
