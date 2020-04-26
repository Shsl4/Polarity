/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import org.spongepowered.api.scheduler.Task;

import java.util.UUID;

/**
 * Manages the warmups.
 */
public interface NucleusWarmupManagerService {

    /**
     * Adds a warmup to the warmup manager
     *
     * @param player The {@link UUID} of the player to add a warmup for.
     * @param task The {@link Task} that was submitted and is running.
     */
    void addWarmup(UUID player, Task task);

    /**
     * Removes a user's warmup.
     *
     * @param player The {@link UUID} of the user to remove the warmpup from.
     * @return <code>true</code> if the warmup exists and was canceled.
     */
    boolean removeWarmup(UUID player);

    /**
     * Removes warmups that have completed.
     */
    void cleanup();
}
