/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.annotations.MightOccurAsync;
import io.github.nucleuspowered.nucleus.api.exceptions.NucleusException;
import io.github.nucleuspowered.nucleus.api.service.NucleusMessageTokenService;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.text.Text;

import java.util.Collection;

/**
 * Event when messages are sent using {@link NucleusTextTemplate}s.
 */
@MightOccurAsync
public interface NucleusTextTemplateEvent extends Event, Cancellable {

    /**
     * Get the {@link NucleusTextTemplate} that will be parsed and sent to
     * players.
     *
     * @return The message
     */
    NucleusTextTemplate getMessage();

    /**
     * Get the original {@link NucleusTextTemplate}.
     *
     * @return The message
     */
    NucleusTextTemplate getOriginalMessage();

    /**
     * Sets the message to send to the users {@link #getRecipients()}
     *
     * @param message The message to send.
     */
    void setMessage(NucleusTextTemplate message);

    /**
     * Attempts to set the NucleusTextTemplate message using a string.
     *
     * <p>See {@link NucleusMessageTokenService#createFromString(String)} for
     * creating the tokens. Also see {@link #setMessage(NucleusTextTemplate)}.</p>
     *
     * @param message The message to send.
     */
    default void setMessage(String message) {
        try {
            setMessage(NucleusAPI.getMessageTokenService().createFromString(message));
        } catch (NucleusException e) {
            throw new IllegalArgumentException("Could not create the NucleusTextTemplate", e);
        }
    }

    /**
     * Get the original recipients to send the message to.
     *
     * @return The original recipients of the message.
     */
    Collection<CommandSource> getOriginalRecipients();

    /**
     * Get the recipients to send the message to.
     *
     * @return The recipients.
     */
    Collection<CommandSource> getRecipients();

    /**
     * Set the recipients to send the message to.
     *
     * @param recipients The recipients.
     */
    void setRecipients(Collection<? extends CommandSource> recipients);

    /**
     * Whether the message contains tokens that may be replaced.
     *
     * @return true if so.
     */
    default boolean containsTokens() {
        return getMessage().containsTokens();
    }

    /**
     * Gets the message that would be sent to the specified
     * {@link CommandSource}.
     *
     * @param source The source
     * @return The message for the specific source
     */
    default Text getMessageFor(CommandSource source) {
        return getMessage().getForCommandSource(source);
    }

    /**
     * Raised when the text being sent originated as a broadcast.
     */
    interface Broadcast extends NucleusTextTemplateEvent {

        /**
         * Resets the the broadcast to the original recipients - all players
         */
        default void sendToAll() {
            setRecipients(getOriginalRecipients());
        }
    }
}
