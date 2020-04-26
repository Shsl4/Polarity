/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Events for RTP
 */
public interface NucleusRTPEvent extends TargetPlayerEvent {

    /**
     * Fired when the RTP system has selected a location
     *
     * <p>Cancelling this event will cause the RTP system
     * to look for another location</p>
     */
    interface SelectedLocation extends NucleusRTPEvent, Cancellable {

        /**
         * Gets the proposed location
         *
         * @return The location
         */
        Location<World> getLocation();

    }
}
