/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api;

import io.github.nucleuspowered.nucleus.api.events.NucleusSendToSpawnEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

/**
 * Contexts that may appear in the {@link Cause} of some events.
 */
public class EventContexts {

    private EventContexts() {}

    /**
     * A context that indicates whether the Nucleus chat events will perform its own formatting.
     *
     * <p>
     *     For the ID, see {@link Identifiers#SHOULD_FORMAT_CHANNEL}
     * </p>
     */
    public static final EventContextKey<Boolean> SHOULD_FORMAT_CHANNEL =
            DummyObjectProvider.createExtendedFor(EventContextKey.class, "SHOULD_FORMAT_CHANNEL");

    /**
     * A context that indicates the source of the Nucleus redirectable spawn events.
     *
     * <p>
     *     For the ID, see {@link Identifiers#SPAWN_EVENT_TYPE}
     * </p>
     */
    public static final EventContextKey<NucleusSendToSpawnEvent.Type> SPAWN_EVENT_TYPE =
            EventContextKey.builder(NucleusSendToSpawnEvent.Type.class).id(Identifiers.SPAWN_EVENT_TYPE).name("SPAWN_EVENT_TYPE").build();

    public static class Identifiers {

        private Identifiers() {}

        /**
         * ID for {@link EventContexts#SHOULD_FORMAT_CHANNEL}
         */
        public static final String SHOULD_FORMAT_CHANNEL = "nucleus:should_format_channel";

        /**
         * ID for {@link EventContexts#SPAWN_EVENT_TYPE}
         */
        public static final String SPAWN_EVENT_TYPE = "nucleus:spawn_event_type";

    }

}
