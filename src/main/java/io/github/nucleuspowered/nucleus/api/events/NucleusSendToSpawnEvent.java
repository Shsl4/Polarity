/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.user.TargetUserEvent;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.World;

/**
 * Called when Nucleus has been requested to send a {@link User} to spawn, either now, or on their next login.
 */
@NonnullByDefault
public interface NucleusSendToSpawnEvent extends TargetUserEvent, Cancellable {

    /**
     * The {@link Transform} to send the {@link User} to.
     *
     * @return The {@link Transform}
     */
    Transform<World> getTransformTo();

    /**
     * The original {@link Transform} Nucleus was going to send the {@link User} to, in the event it's changed.
     *
     * @return The {@link Transform}
     */
    Transform<World> getOriginalTransformTo();

    /**
     * The {@link Transform} to redirect the {@link User} to.
     *
     * @param transform The {@link Transform}
     */
    void setTransformTo(Transform<World> transform);

    /**
     * If cancelled, the reason to return to the requestee.
     *
     * @param reason The reason for cancelling.
     */
    void setCancelReason(String reason);

    /**
     * The {@link User} being spawned.
     *
     * @return The {@link User}
     */
    User getTargetUser();

    /**
     * The type of the spawn event.
     */
    enum Type {COMMAND, DEATH, HOME_ON_DEATH, PLUGIN}
}
