/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.exceptions.NoSuchLocationException;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Inmate;
import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;

/**
 * A service that handles subject jailing.
 */
@NonnullByDefault
public interface NucleusJailService {

    /**
     * The permission context key that indicates a player is jailed.
     *
     * <p>The value of this context will always be true if set.</p>
     */
    String JAILED_CONTEXT = "nucleus_jailed";

    /**
     * The permission context key that indicates which jail a player is in.
     *
     * <p>The value of this context will be the name of the jail.</p>
     */
    String JAIL_CONTEXT = "nucleus_jail";

    /**
     * Sets a jail location in the world.
     *
     * @param name The name of the jail to use.
     * @param location The {@link Location} in a world for the jail.
     * @param rotation The rotation of the subject once in jail.
     * @return <code>true</code> if the creation of a jail point was a success.
     */
    boolean setJail(String name, Location<World> location, Vector3d rotation);

    /**
     * Gets the name of the jails on the server. All jails returned in this map exist.
     *
     * @return A {@link Map} of names to {@link NamedLocation}.
     */
    Map<String, NamedLocation> getJails();

    /**
     * Gets the location of a jail, if it exists.
     *
     * @param name The name of the jail to get. Case in-sensitive.
     * @return An {@link Optional} that potentially contains the {@link NamedLocation} if the jail exists.
     */
    Optional<NamedLocation> getJail(String name);

    /**
     * Removes a jail location from the list.
     *
     * @param name The name of the jail to remove.
     * @return <code>true</code> if successful.
     */
    boolean removeJail(String name);

    /**
     * Returns whether a subject is jailed.
     *
     * @param user The {@link User} to check.
     * @return <code>true</code> if the subject is jailed.
     */
    boolean isPlayerJailed(User user);

    /**
     * Returns information about why a subject is jailed, if they are indeed jailed.
     *
     * @param user The {@link User} to check
     * @return An {@link Optional} that will contain {@link Inmate} information if the subject is jailed.
     */
    Optional<Inmate> getPlayerJailData(User user);

    /**
     * Jails a subject if they are not currently jailed.
     *
     * @param victim The user to jail.
     * @param jail The jail to send the user to.
     * @param jailer The jailing entity.
     * @param reason The reason for jailing.
     * @return <code>true</code> if the subject was jailed successfully.
     * @throws NoSuchLocationException if the jail does not exist.
     */
    boolean jailPlayer(User victim, String jail, CommandSource jailer, String reason) throws NoSuchLocationException;

    /**
     * Unjails a subject if they are currently jailed.
     *
     * @param user The {@link User} to unjail.
     * @return <code>true</code> if the subject was unjailed successfully.
     */
    boolean unjailPlayer(User user);
}
