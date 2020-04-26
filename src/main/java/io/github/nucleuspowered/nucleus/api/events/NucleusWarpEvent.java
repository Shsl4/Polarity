/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import io.github.nucleuspowered.nucleus.api.annotations.MightOccurAsync;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Warp;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.user.TargetUserEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Events when a server warp changes.
 */
public interface NucleusWarpEvent extends Cancellable, CancelMessage, Event {

    /**
     * Get the name of the warp.
     *
     * @return The name of the warp.
     */
    String getName();

    /**
     * Fired when a warp is created.
     */
    @MightOccurAsync
    interface Create extends NucleusWarpEvent {

        /**
         * Gets the proposed {@link Location} of the warp.
         *
         * @return The location.
         */
        Location<World> getLocation();
    }

    /**
     * Fired when a warp is deleted.
     */
    @MightOccurAsync
    interface Delete extends NucleusWarpEvent {

        /**
         * Gets the {@link Warp}
         *
         * @return The warp.
         */
        Warp getWarp();

        /**
         * Gets the {@link Location} of the warp.
         *
         * @return The location. It might not exist if the world does not exist any more.
         */
        Optional<Location<World>> getLocation();
    }

    /**
     * Fired when a {@link User} tries to teleport to a warp. The {@link Cause} of the event
     * is who requests the warp, and is not necessarily the {@link #getTargetUser()} who is
     * being warped.
     *
     * <p>
     *     Note that the user does not necessarily need to be online.
     * </p>
     */
    interface Use extends TargetUserEvent, NucleusWarpEvent {

        /**
         * Gets the {@link Warp}
         *
         * @return The warp.
         */
        Warp getWarp();

        /**
         * Gets the {@link Location} of the warp.
         *
         * @return The location.
         */
        Location<World> getLocation();
    }
}
