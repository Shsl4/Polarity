/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.chat;

import org.spongepowered.api.text.channel.MessageChannel;

/**
 * This interface is a marker to indicate to the Chat module that messages through this channel might not be formatted.
 */
public interface NucleusNoFormatChannel extends MessageChannel {

    /**
     * Determines whether Nucleus should try to format the message.
     *
     * @return {@code true} if it should, {@code false} otherwise (the default)
     */
    default boolean formatMessages() {
        return false;
    }

    /**
     * Determines whether Nucleus should remove the prefix from the message. Ignored if
     * {@link #formatMessages()} is true.
     *
     * @return {@code true} if it should (the default), {@code false} otherwise.
     */
    default boolean removePrefix() {
        return true;
    }
}
