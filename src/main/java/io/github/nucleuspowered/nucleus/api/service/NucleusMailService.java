/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.api.nucleusdata.MailMessage;
import org.spongepowered.api.entity.living.player.User;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * A service that handles sending and retrieving mail.
 */
public interface NucleusMailService {

    /**
     * Gets mail for a specific player, optionally including a list of filters.
     *
     * @param player The {@link User} of the player to get the mail of.
     * @param filters The {@link MailFilter}s
     * @return A list of mail.
     */
    List<MailMessage> getMail(User player, MailFilter... filters);

    /**
     * Removes a specific mail for a specific player.
     *
     * @param player The {@link User} to remove the mail from.
     * @param mailData The {@link MailMessage} to remove from the player.
     * @return <code>true</code> if the mail was removed, <code>false</code> if the player didn't have the mail.
     */
    boolean removeMail(User player, MailMessage mailData);

    /**
     * Sends mail to a subject, addressed from another subject.
     *
     * @param playerFrom The {@link User} of the player to send the message from.
     * @param playerTo The {@link User} of the player to send the message to.
     * @param message The message.
     */
    void sendMail(User playerFrom, User playerTo, String message);

    /**
     * Sends mail to a player, addressed from the console.
     *
     * @param playerTo The {@link User} of the player to send the message to.
     * @param message The message.
     */
    void sendMailFromConsole(User playerTo, String message);

    /**
     * Clears the player's mail.
     *
     * @param player The {@link UUID} of the player.
     * @return If there was any mail cleared
     */
    boolean clearUserMail(User player);

    /**
     * Create a filter that restricts the mail to the senders provided.
     *
     * <p>
     *     Multiple player filters can be provided - this will return messages authored by all specified players.
     * </p>
     *
     * @param includeConsole If <code>true</code>, include the console/plugins in any returned mail.
     * @param player The {@link UUID}s of the players.
     * @return The {@link MailFilter}
     */
    default MailFilter createSenderFilter(boolean includeConsole, UUID... player) {
        return createSenderFilter(includeConsole, Arrays.asList(player));
    }

    /**
     * Create a filter that restricts the mail to the senders provided.
     *
     * <p>
     *     Multiple player filters can be provided - this will return messages authored by all specified players.
     * </p>
     *
     * @param includeConsole If <code>true</code>, include the console/plugins in any returned mail.
     * @param player The {@link UUID}s of the players.
     * @return The {@link MailFilter}
     */
    default MailFilter createSenderFilter(boolean includeConsole, final Collection<UUID> player) {
        return m -> m.getSender().map(x -> player.contains(x.getUniqueId())).orElse(includeConsole);
    }

    /**
     * Create a filter that restricts the mail to a certain time period. One parameter may be
     * null, but not both. The times on the instants will be ignored.
     *
     * <p>
     *     Only <strong>one</strong> of these filters can be used at a time.
     * </p>
     *
     * @param after The {@link Instant} which indicates the earliest date to return.
     * @param before The {@link Instant} which indicates the latest date to return.
     * @return The {@link MailFilter}
     */
    default MailFilter createDateFilter(@Nullable Instant after, @Nullable Instant before) {
        Preconditions.checkArgument(after != null || before != null);
        final Instant inAfter = after == null ? Instant.ofEpochMilli(0) : after;
        final Instant inBefore = before == null ? Instant.now() : before;

        return m -> inAfter.isBefore(m.getDate()) && inBefore.isAfter(m.getDate());
    }

    /**
     * Create a filter that restricts the messages returned to the provided substring.
     *
     * <p>
     *     If multiple strings are set, all need to match.
     * </p>
     *
     * @param caseSensitive Whether this filter is case sensitive.
     * @param message The message.
     * @return The {@link MailFilter}
     */
    default MailFilter createMessageFilter(final boolean caseSensitive, String... message) {
        return createMessageFilter(caseSensitive, Arrays.asList(message));
    }

    /**
     * Create a filter that restricts the messages returned to the provided substring.
     *
     * <p>
     *     If multiple strings are set, all need to match.
     * </p>
     *
     * @param caseSensitive Whether this filter is case sensitive.
     * @param message The message.
     * @return The {@link MailFilter}
     */
    default MailFilter createMessageFilter(final boolean caseSensitive, Collection<String> message) {
        final List<String> strings = message.stream().map(x -> !caseSensitive ? x.toLowerCase() : x).collect(Collectors.toList());
        return m -> {
            String mm = caseSensitive ? m.getMessage() : m.getMessage().toLowerCase();
            return strings.stream().allMatch(mm::contains);
        };
    }

    /**
     * This {@link MailFilter} is simply to prevent compiler warnings with varargs, and is functionally the same as a
     * {@link Predicate}
     */
    @FunctionalInterface
    interface MailFilter extends Predicate<MailMessage> {}
}
