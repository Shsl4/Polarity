/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.text.Text;

import java.util.Optional;

import javax.annotation.Nullable;

public interface CancelMessage extends Cancellable {

    /**
     * The message to send to the player if the event is cancelled, if any.
     *
     * @return The message.
     */
    Optional<Text> getCancelMessage();

    /**
     * The message to display to the user if the event is cancelled, or <code>null</code> to clear.
     *
     * @param message The message.
     */
    void setCancelMessage(@Nullable Text message);
}
