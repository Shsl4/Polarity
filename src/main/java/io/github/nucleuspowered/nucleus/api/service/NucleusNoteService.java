/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import com.google.common.collect.ImmutableList;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Note;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;

/**
 * A service that determines whether a subject has notes.
 */
public interface NucleusNoteService {

    /**
     * Gets all notes for a specific user
     *
     * @param user The {@link User} to check.
     * @return A list of {@link Note}s.
     */
    ImmutableList<Note> getNotes(User user);

    /**
     * Adds a note to a subject.
     *
     * @param user The {@link User} to add a note to.
     * @param source The {@link CommandSource} that is adding the note.
     * @param note The note to add.
     * @return <code>true</code> if the note was added.
     */
    boolean addNote(User user, CommandSource source, String note);

    /**
     * Removes a note from a subject.
     *
     * @param user The {@link User} to remove a note from.
     * @param note The {@link Note} to remove.
     * @return <code>true</code> if the note was removed.
     */
    boolean removeNote(User user, Note note);

    /**
     * Clears all notes from a subject.
     *
     * @param user The {@link User} to remove all notes from.
     * @return <code>true</code> if all notes were removed.
     */
    boolean clearNotes(User user);
}
