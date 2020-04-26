/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import io.github.nucleuspowered.nucleus.api.annotations.MightOccurAsync;
import org.spongepowered.api.event.Event;

/**
 * An event fired when a name is added or removed from the name ban list.
 */
public interface NucleusNameBanEvent extends Event {

    /**
     * The entry that was affected.
     *
     * @return The entry.
     */
    String getEntry();

    /**
     * The reason for the ban.
     *
     * @return The reason.
     */
    String getReason();

    /**
     * Fired when a regular expression is banned.
     */
    @MightOccurAsync
    interface Banned extends NucleusNameBanEvent {}

    /**
     * Fired when a regular expression is unbanned.
     */
    @MightOccurAsync
    interface Unbanned extends NucleusNameBanEvent {}
}
