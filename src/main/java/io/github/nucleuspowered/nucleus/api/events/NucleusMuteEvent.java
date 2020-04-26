/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import io.github.nucleuspowered.nucleus.api.annotations.MightOccurAsync;
import org.spongepowered.api.event.user.TargetUserEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.time.Duration;
import java.util.Optional;

/**
 * Events that occur whilst muted.
 *
 * <p>
 *     These events might occur async!
 * </p>
 */
@NonnullByDefault
public interface NucleusMuteEvent extends TargetUserEvent {

    /**
     * Fired when a player is muted.
     */
    @MightOccurAsync
    interface Muted extends NucleusMuteEvent {

        /**
         * Gets the duration of the mute, if any.
         *
         * @return The duration.
         */
        Optional<Duration> getDuration();

        /**
         * The reason given for the mute.
         *
         * @return The reason.
         */
        Text getReason();
    }

    /**
     * Fired when a player is unmuted.
     *
     * <p>
     *     Note that the {@link #getCause()} of the event will be the {@link PluginContainer} for the plugin if the event lapsed naturally.
     * </p>
     */
    @MightOccurAsync
    interface Unmuted extends NucleusMuteEvent {

        /**
         * Whether the mute simply expired.
         *
         * @return <code>true</code> if so.
         */
        boolean expired();
    }
}
