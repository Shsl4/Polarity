/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import io.github.nucleuspowered.nucleus.api.exceptions.ModulesLoadedException;
import io.github.nucleuspowered.nucleus.api.exceptions.NoModuleException;
import io.github.nucleuspowered.nucleus.api.exceptions.UnremovableModuleException;

import java.util.Set;

/**
 * A service that allows plugins to request certain modules are not loaded.
 */
public interface NucleusModuleService {
    /**
     * Gets the ids of the modules to load, or the modules that have been loaded.
     *
     * @return The modules that are to be loaded, or are being loaded.
     */
    Set<String> getModulesToLoad();

    /**
     * Returns whether modules can be disabled.
     *
     * @return <code>true</code> if so.
     */
    boolean canDisableModules();

    /**
     * Removes a module from NucleusPlugin programmatically, so plugins can override the behaviour if required. This method will
     * only work during pre-init and init - while {@link #canDisableModules()} is <code>true</code>.
     *
     * @param module The id of the module to disable.
     * @param plugin The plugin that is requesting to disable the module. Used for logging purposes - telling the
     *               user who is disabling the plugin.
     * @throws ModulesLoadedException Thrown if the modules have now been loaded and can no longer be removed.
     * @throws UnremovableModuleException Thrown if the module has been marked "cannot be disabled". Plugins are expected
     *         to honour this, NucleusPlugin does not mark any module (apart from the core) as "unable to be disabled" by default,
     *         so plugin authors are requested to not try to override any behaviour that a user has explicitly turned on.
     * @throws NoModuleException Thrown if the module does not exist.
     */
    void removeModule(String module, Object plugin) throws ModulesLoadedException, UnremovableModuleException, NoModuleException;
}
