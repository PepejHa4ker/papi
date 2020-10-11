package com.pepej.papi;

import com.pepej.papi.environment.ScriptEnvironment;


/**
 * Service interface for papi-js.
 */
public interface PapiJs {

    /**
     * Gets the papi-js {@link ScriptController}.
     *
     * @return the controller
     */
    ScriptController getController();

    /**
     * Gets the {@link ScriptEnvironment} representing the papi-js environment.
     *
     * @return the environment
     */
    ScriptEnvironment getEnvironment();


}
