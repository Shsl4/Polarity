/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.item.ItemType;

import java.util.Map;

/**
 * This service provides methods to support obtaining Nucleus' blacklist entries
 * for use by another plugin.
 *
 * @deprecated The Blacklist has been removed. Will be removed in a future version
 * of Nucleus.
 */
@Deprecated
public interface NucleusBlacklistMigrationService {

    /**
     * Gets all the {@link BlockState}s that had a restriction on them.
     *
     * @return The {@link BlockState} and the results.
     */
    Map<BlockState, Result> getBlacklistedBlockstates();

    /**
     * Gets all the {@link ItemType}s that had a restriction on them.
     *
     * @return The {@link ItemType}s and the results.
     */
    Map<ItemType, Result> getBlacklistedItemtypes();

    interface Result {

        /**
         * If true, Nucleus blocked this type from being placed/broken.
         *
         * @return <code>true</code> if blacklisted
         */
        boolean environment();

        /**
         * If true, Nucleus blocked this item type from being used.
         *
         * @return <code>true</code> if blacklisted
         */
        boolean use();

        /**
         * If true, Nucleus blocked this item type from being possessed.
         *
         * @return <code>true</code> if blacklisted
         */
        boolean possession();
    }
}
