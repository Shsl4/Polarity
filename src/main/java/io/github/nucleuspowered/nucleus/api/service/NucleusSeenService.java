/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This service allows plugins to register handlers that will display information when a player runs the /seen command.
 *
 * <p>
 *     Plugins are expected to only register <strong>one</strong> {@link SeenInformationProvider}.
 * </p>
 *
 * <p>
 *     Consumers of this API should also note that this might run <strong>asynchronously</strong>. No methods that would
 *     require the use of a synchronous API should be used here.
 * </p>
 */
@NonnullByDefault
public interface NucleusSeenService {

    /**
     * Registers a {@link SeenInformationProvider} with Nucleus.
     *
     * @param plugin The plugin registering the service.
     * @param seenInformationProvider The {@link SeenInformationProvider}
     * @throws IllegalArgumentException Thrown if the plugin has either
     * <ul>
     *  <li>Registered a {@link SeenInformationProvider} already</li>
     *  <li>Not provided the {@link org.spongepowered.api.plugin.Plugin} annotated class</li>
     * </ul>
     */
    void register(Object plugin, SeenInformationProvider seenInformationProvider) throws IllegalArgumentException;

    /**
     * Registers a {@link SeenInformationProvider} with Nucleus that can be constructed using a functional programming style.
     *
     * @param plugin The plugin registering the service.
     * @param permissionCheck A {@link Predicate} that checks that the {@link CommandSource} has permission to view the information provided.
     * @param informationGetter A {@link BiFunction} that accepts a {@link CommandSource} that wants to view information about a {@link User}
     *                          and returns a {@link Collection} of {@link Text} to view.
     * @throws IllegalArgumentException Thrown if the plugin has either
     * <ul>
     *  <li>Registered a {@link SeenInformationProvider} already</li>
     *  <li>Not provided the {@link org.spongepowered.api.plugin.Plugin} annotated class</li>
     * </ul>
     */
    void register(Object plugin,
        Predicate<CommandSource> permissionCheck,
        BiFunction<CommandSource, User, Collection<Text>> informationGetter) throws IllegalArgumentException;

    /**
     * A {@link SeenInformationProvider} object can hook into the {@code seen} command and provide extra information on a player.
     *
     * <p>
     *     This must be registered with the {@link NucleusSeenService}
     * </p>
     */
    interface SeenInformationProvider {

        /**
         * Gets whether the requesting {@link CommandSource} has permission to request the provided information for the
         * requested {@link User}.
         *
         * @param source The {@link CommandSource} who ran the {@code seen} command.
         * @param user The {@link User} that information has been requested about.
         * @return {@code true} if the command should show the user this information.
         */
        boolean hasPermission(@Nonnull CommandSource source, @Nonnull User user);

        /**
         * Gets the information to display to the {@link CommandSource} about the {@link User}
         *
         * @param source The {@link CommandSource} who ran the {@code seen} command.
         * @param user The {@link User} that information has been requested about.
         * @return The {@link Collection} containing the {@link Text} to display to the user, or an empty iterable. It is
         *         recommended, for obvious reasons, that this is ordered! May return {@code null} if there is nothing
         *         to return.
         */
        @Nullable Collection<Text> getInformation(@Nonnull CommandSource source, @Nonnull User user);
    }
}
