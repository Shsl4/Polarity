/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import io.github.nucleuspowered.nucleus.api.annotations.MightOccurAsync;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.World;

/**
 * These events are fired <em>before</em> teleportation actually happens.
 */
@NonnullByDefault
public interface NucleusTeleportEvent extends TargetPlayerEvent, CancelMessage {

    /**
     * Indicates that a teleport request (such as through <code>/tpa</code>) is being sent.
     */
    @MightOccurAsync
    interface Request extends NucleusTeleportEvent {

        /**
         * The recipient of the request.
         *
         * @return The {@link Player} in question.
         */
        @Override Player getTargetEntity();

        /**
         * Called when the root cause wants to teleport to the target player (like /tpa).
         */
        interface CauseToPlayer extends Request {}

        /**
         * Called when the root cause wants the target player to teleport to them (like /tpahere).
         */
        interface PlayerToCause extends Request {}
    }

    /**
     * Called when a player is about to be teleported through the Nucleus system.
     */
    interface AboutToTeleport extends NucleusTeleportEvent {

        /**
         * Gets the proposed location of the entity that will be teleported.
         *
         * @return The {@link Transform} that the player would teleport to. Note that for {@link Request}, this might change when the
         * teleportation gets underway - any changes should be made during the {@link MoveEntityEvent.Teleport} event.
         */
        Transform<World> getToTransform();

        /**
         * The {@link Player} to be teleported.
         *
         * @return The {@link Player} in question.
         */
        @Override Player getTargetEntity();
    }

}