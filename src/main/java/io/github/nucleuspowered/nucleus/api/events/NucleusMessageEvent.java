/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import io.github.nucleuspowered.nucleus.api.annotations.MightOccurAsync;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

/**
 * An event that is posted when a player uses /m in NucleusPlugin
 */
@MightOccurAsync
public interface NucleusMessageEvent extends Event, Cancellable {

    /**
     * The sender.
     *
     * @return The {@link CommandSource} that sent the message.
     */
    CommandSource getSender();

    /**
     * The recipient.
     *
     * @return The {@link CommandSource} that receives the message.
     */
    CommandSource getRecipient();

    /**
     * The message that was sent.
     *
     * @return The message.
     */
    String getMessage();
}
