/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.chat;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

/**
 * This interface holds chat channels that Nucleus uses.
 */
public interface NucleusChatChannel extends MessageChannel {

    /**
     * Returns how this channel will format a message bound for the specified
     * {@link MessageReceiver} from the specified {@link CommandSource} sender.
     *
     * @param receiver The receiver.
     * @param text The message to format.
     * @return The formatted text.
     */
    default Text format(CommandSource sender, MessageReceiver receiver, Text text) {
        return text;
    }

    /**
     * Indicates that the channel is Staff Chat.
     */
    interface StaffChat extends NucleusChatChannel, NucleusNoFormatChannel, NucleusNoIgnoreChannel {}

    /**
     * Indicates that the channel is a /me message.
     */
    interface ActionMessage extends NucleusChatChannel, NucleusNoFormatChannel {}

    /**
     * Indicates that the channel is a /helpop messages
     */
    interface HelpOp extends NucleusChatChannel, NucleusNoFormatChannel {}
}