/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.api.exceptions.NucleusException;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Map;
import java.util.Optional;

/**
 * A service to get the current prices of an item.
 */
@NonnullByDefault
public interface NucleusServerShopService {

    /**
     * Gets all the server buy prices.
     *
     * <p>Note that the {@link CatalogType} could either be an {@link ItemType}
     * or a {@link BlockState}</p>
     *
     * @return The buy prices
     */
    Map<CatalogType, Double> getBuyPrices();

    /**
     * Gets all the server sell prices.
     *
     * <p>Note that the {@link CatalogType} could either be an {@link ItemType}
     * or a {@link BlockState}</p>
     *
     * @return The buy prices
     */
    Map<CatalogType, Double> getSellPrices();

    /**
     * Gets the buy price of an item (that is, how much a server will sell an item to a player for).
     *
     * @param type The item to check.
     * @return The price, or {@link Optional} if not being sold.
     */
    default Optional<Double> getBuyPrice(ItemType type) {
        Preconditions.checkNotNull(type);
        return getBuyPrice(ItemStack.of(type, 1));
    }

    /**
     * Gets the buy price of an item (that is, how much a server will sell an item to a player for).
     *
     * @param type The item to check.
     * @return The price, or {@link Optional} if not being sold.
     * @throws NucleusException if the {@link BlockState} does not map to an item.
     */
    Optional<Double> getBuyPrice(BlockState type) throws NucleusException;


    /**
     * Gets the buy price of an item (that is, how much a server will sell an item to a player for).
     *
     * @param itemStack The item to check.
     * @return The price, or {@link Optional} if not being sold.
     */
    default Optional<Double> getBuyPrice(ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack);
        return getBuyPrice(itemStack.createSnapshot());
    }

    /**
     * Gets the buy price of an item (that is, how much a server will sell an item to a player for).
     *
     * @param itemStackSnapshot The item to check.
     * @return The price, or {@link Optional} if not being sold.
     */
    Optional<Double> getBuyPrice(ItemStackSnapshot itemStackSnapshot);

    /**
     * Gets the sell price of an item (that is, how much a server will buy an item from a player for).
     *
     * @param type The item to check.
     * @return The price, or {@link Optional} if not being bought.
     */
    default Optional<Double> getSellPrice(ItemType type) {
        Preconditions.checkNotNull(type);
        return getSellPrice(ItemStack.of(type, 1));
    }

    /**
     * Gets the sell price of an item (that is, how much a server will buy an item from a player for).
     *
     * @param type The item to check.
     * @return The price, or {@link Optional} if not being bought.
     * @throws NucleusException if the {@link BlockState} does not map to an item.
     */
    Optional<Double> getSellPrice(BlockState type) throws NucleusException;

    /**
     * Gets the sell price of an item (that is, how much a server will buy an item from a player for).
     *
     * @param itemStack The item to check.
     * @return The price, or {@link Optional} if not being bought.
     */
    default Optional<Double> getSellPrice(ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack);
        return getSellPrice(itemStack.createSnapshot());
    }

    /**
     * Gets the sell price of an item (that is, how much a server will buy an item from a player for).
     *
     * @param itemStackSnapshot The item to check.
     * @return The price, or {@link Optional} if not being bought.
     */
    Optional<Double> getSellPrice(ItemStackSnapshot itemStackSnapshot);
}
