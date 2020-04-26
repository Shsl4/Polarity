/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

/**
 * Fired on a player's first time on the server.
 *
 * <p>
 *     This will fire during {@link ClientConnectionEvent.Join} at the {@link Order#LATE} priority. Also note that these methods
 *     wrap around the {@link ClientConnectionEvent.Join} event methods, so any changes to these parameters will be fed back to
 *     the original event.
 * </p>
 */
public interface NucleusFirstJoinEvent extends TargetPlayerEvent, MessageChannelEvent {}
