/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.user.TargetUserEvent;

/**
 * Fired when a player's inventory is cleared. Should be used to clear other, secondary, inventories
 *
 * <p>Note that the target user may be a {@link org.spongepowered.api.entity.living.player.Player}</p>
 */
public interface NucleusClearInventoryEvent extends TargetUserEvent {

    /**
     * Gets whether the entire inventory is being cleared, or the
     * {@link org.spongepowered.api.item.inventory.entity.MainPlayerInventory} only.
     *
     * @return whether the entire inventory is being cleared or not
     */
    boolean isClearingAll();

    /**
     * Called before any clearing takes effect. May be cancelled. Should not be used to clear
     * inventories (use {@link Post} for that).
     */
    interface Pre extends NucleusClearInventoryEvent, Cancellable { }

    /**
     * Called when standard inventories have been cleared and should be used to clear other
     * inventories.
     */
    interface Post extends NucleusClearInventoryEvent { }

}
