/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import io.github.nucleuspowered.nucleus.api.chat.NucleusChatChannel;
import org.spongepowered.api.text.channel.MessageChannel;

/**
 * Provides a way to get the Staff Chat message channel instance.
 */
public interface NucleusStaffChatService {

    /**
     * Gets the staff chat message channel.
     *
     * @return The {@link MessageChannel}
     */
    NucleusChatChannel.StaffChat getStaffChat();

}
