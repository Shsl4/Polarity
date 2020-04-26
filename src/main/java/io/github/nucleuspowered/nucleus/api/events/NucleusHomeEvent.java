/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import io.github.nucleuspowered.nucleus.api.annotations.MightOccurAsync;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Home;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.user.TargetUserEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Events when a player's home changes.
 */
public interface NucleusHomeEvent extends Cancellable, CancelMessage, Event {

    /**
     * Gets the name of the home.
     *
     * @return The name of the home.
     */
    String getName();

    /**
     * Gets the owner of the house
     *
     * @return The owner.
     */
    User getUser();

    /**
     * Gets the {@link Location} of the home.
     *
     * @return The location. It might not exist if the world does not exist any more.
     */
    Optional<Location<World>> getLocation();

    /**
     * Fired when a home is created.
     */
    @MightOccurAsync
    interface Create extends NucleusHomeEvent {}

    /**
     * Fired when a home is moved.
     */
    @MightOccurAsync
    interface Modify extends NucleusHomeEvent {

        /**
         * Gets the existing home.
         *
         * @return The {@link Home}
         */
        Home getHome();

        /**
         * Gets the original {@link Location} of the home. To get the proposed new
         * location, see {@link #getLocation()}
         *
         * @return The location. It might not exist if the world does not exist any more.
         */
        Optional<Location<World>> getOriginalLocation();
    }

    /**
     * Fired when a home is deleted.
     */
    @MightOccurAsync
    interface Delete extends NucleusHomeEvent {

        /**
         * Gets the existing home.
         *
         * @return The {@link Home}
         */
        Home getHome();
    }

    /**
     * Fired when a {@link User} is warped to a home.  Target user is the user being warped.
     *
     * <p>
     *     Note that the user does not necessarily need to be online.
     * </p>
     */
    interface Use extends NucleusHomeEvent, TargetUserEvent {

        /**
         * Gets the existing home.
         *
         * @return The {@link Home}
         */
        Home getHome();
    }
}
