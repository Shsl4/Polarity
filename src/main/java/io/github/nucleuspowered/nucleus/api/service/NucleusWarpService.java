/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Warp;
import io.github.nucleuspowered.nucleus.api.nucleusdata.WarpCategory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * Gets a service that allows users to warp about using defined warp.
 */
@NonnullByDefault
public interface NucleusWarpService {

    /**
     * Gets the location for the specified warp.
     *
     * @param warpName The name of the warp to check.
     * @return The {@link Location} of the warp, or {@link Optional#empty()} otherwise.
     */
    Optional<Warp> getWarp(String warpName);

    /**
     * Removes a warp.
     *
     * @param warpName The name of the warp.
     * @return <code>true</code> if the warp was there and removed, <code>false</code> if the warp never existed.
     */
    boolean removeWarp(String warpName);

    /**
     * Sets a warp, will not overwrite current warp.
     *
     * @param warpName The name of the warp to set.
     * @param location The location of the warp.
     * @param rotation The rotation of the warp.
     * @return <code>true</code> if set, <code>false</code> otherwise.
     */
    boolean setWarp(String warpName, Location<World> location, Vector3d rotation);

    /**
     * Gets all the warps available.
     *
     * <p>If you just want to display the names, use {@link #getWarpNames()} instead.</p>
     *
     * @return All warps in Nucleus.
     */
    List<Warp> getAllWarps();

    /**
     * Get all warps that have not been given a category.
     *
     * @return The {@link Warp}s without a category.
     */
    List<Warp> getUncategorisedWarps();

    /**
     * Gets all warps that hae been given the specified category.
     *
     * @param category The category.
     * @return The warps.
     */
    List<Warp> getWarpsForCategory(String category);

    /**
     * Gets all warps that have categories.
     *
     * @return The warps.
     */
    default Map<WarpCategory, List<Warp>> getWarpsWithCategories() {
        return getWarpsWithCategories(x -> true);
    }

    /**
     * Gets all warps that have categories.
     *
     * @param warpDataPredicate The filtering predicate to return the subset of warps required.
     * @return The warps.
     */
    Map<WarpCategory, List<Warp>> getWarpsWithCategories(Predicate<Warp> warpDataPredicate);

    /**
     * Removes the cost of a warp.
     *
     * @param warpName The name of the warp to remove the cost from.
     * @return <code>true</code> if the cost removal succeeds.
     */
    boolean removeWarpCost(String warpName);

    /**
     * Sets the cost of a warp.
     *
     * @param warpName The name of the warp to change the cost of.
     * @param cost The cost to use the warp. Set to zero (or negative) to disable.
     * @return <code>true</code> if the cost is set, <code>false</code> otherwise.
     */
    boolean setWarpCost(String warpName, double cost);

    /**
     * Sets a warp's category
     *
     * @param warpName The name of the warp.
     * @param category The name of the category.
     * @return {@code true} if successful
     */
    boolean setWarpCategory(String warpName, @Nullable String category);


    /**
     * Sets a warp's description.
     *
     * @param warpName The name of the warp.
     * @param description The description, or <code>null</code> to clear.
     * @return {@code true} if successful
     */
    boolean setWarpDescription(String warpName, @Nullable Text description);

    /**
     * Gets the names of all the warp that are available.
     *
     * @return A set of warp.
     */
    Set<String> getWarpNames();

    /**
     * Gets whether a warp exists.
     *
     * @param name The name to check for.
     * @return <code>true</code> if it exists, <code>false</code> otherwise.
     */
    default boolean warpExists(String name) {
        return getWarp(name).isPresent();
    }

    /**
     * Gets the data associated with a warp category.
     *
     * @param category The name of the category to get.
     * @return An {@link Optional} containing the category, if it exists.
     */
    Optional<WarpCategory> getWarpCategory(String category);

    /**
     * Sets the display name of a warp category.
     *
     * @param category The name of the category.
     * @param displayName The display name. Set to null to revert to the category name.
     * @return <code>true</code> if the category exists and this is successful.
     */
    boolean setWarpCategoryDisplayName(String category, @Nullable Text displayName);

    /**
     * Sets the description of a warp category.
     *
     * @param category The name of the category.
     * @param description The description. Set to null to remove the description.
     * @return <code>true</code> if the category exists and this is successful.
     */
    boolean setWarpCategoryDescription(String category, @Nullable Text description);
}
