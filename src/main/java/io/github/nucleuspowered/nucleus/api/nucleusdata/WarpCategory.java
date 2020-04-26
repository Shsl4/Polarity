/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.nucleusdata;

import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;

/**
 * Defines a warp category.
 */
public interface WarpCategory {

    /**
     * Gets the ID of the category.
     *
     * @return The ID.
     */
    String getId();

    /**
     * Gets the display name of the category. Defaults to the category name.
     *
     * @return The display name.
     */
    Text getDisplayName();

    /**
     * Gets the defined description of the category, if available.
     *
     * @return An {@link Optional} that might contain a description.
     */
    Optional<Text> getDescription();

    /**
     * Gets the warps associated with this category.
     *
     * @return The warps.
     */
    Collection<Warp> getWarps();
}
