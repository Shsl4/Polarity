/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import io.github.nucleuspowered.nucleus.api.exceptions.NoModuleException;
import io.github.nucleuspowered.nucleus.api.exceptions.UnremovableModuleException;
import org.spongepowered.api.event.Event;

import java.util.Map;


/**
 * A set of events that fire at various points of the NucleusPlugin lifecycle.
 */
public interface NucleusModuleEvent extends Event {

    /**
     * Gets the module list, along with the {@link ModuleEnableState}
     *
     * @return The
     */
    Map<String, ModuleEnableState> getModuleList();

    /**
     * Fired just before modules are loaded. This is the last chance to disable modules.
     */
    interface AboutToConstruct extends NucleusModuleEvent {

        /**
         * Disables the named module.
         *
         * @param module The id of the module to disable.
         * @param plugin The plugin that is requesting to disable the module. Used for logging purposes - telling the
         *               user who is disabling the plugin.
         * @throws UnremovableModuleException Thrown if the module has been marked "cannot be disabled". Plugins are expected
         *         to honour this, NucleusPlugin does not mark any module (apart from the core) as "unable to be disabled" by default,
         *         so plugin authors are requested to not try to override any behaviour that a user has explicitly turned on.
         * @throws NoModuleException Thrown if the module does not exist.
         */
        void disableModule(String module, Object plugin) throws UnremovableModuleException, NoModuleException;
    }

    /**
     * Fires when the Pre-Enable state of a module is about to be fired. No modules have been loaded at this point,
     * though their main config file configuration adapters have, but they are ready to start the enabling process.
     */
    interface AboutToEnable extends NucleusModuleEvent {

    }

    /**
     * Fires when the Pre-Enable state of a module has completed, but is yet to be declared enabled. Services are
     * registered at this point.
     */
    interface PreEnable extends NucleusModuleEvent {

    }

    /**
     * Fires when the Enable state of a module has completed, and the module is declared enabled. Some operations may
     * need to run after this state, but all API related functions should be operational.
     */
    interface Enabled extends NucleusModuleEvent {

    }

    /**
     * Fires when all modules have fully loaded and the module loader has nothing more to do.
     */
    interface Complete extends NucleusModuleEvent {

    }

    enum ModuleEnableState {
        /**
         * Indicates that the module has been disabled and will not be/has not been loaded.
         */
        DISABLED,

        /**
         * Indicates that the module is currently enabled and will be, or has been, loaded.
         *
         * <p>
         *    Modules in this state can be disabled any time before {@link NucleusModuleEvent.AboutToEnable} completes, using
         *    {@link NucleusModuleEvent.AboutToConstruct#disableModule(String, Object)}.
         * </p>
         */
        ENABLED,

        /**
         * Indicates that the module is currently enabled and cannot be disabled. It will be, or has been, loaded.
         */
        FORCE_ENABLED
    }
}
