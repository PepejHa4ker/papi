package com.pepej.papi.network.redirect;

import com.pepej.papi.profiles.Profile;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Provides default parameters for outgoing {@link RedirectSystem.Request}s.
 */
public interface RedirectParameterProvider {

    /**
     * Provides some default parameters for the given profile.
     *
     * @param profile the profile
     * @return the params
     */
    @NotNull
    Map<String, String> provide(@NotNull Profile profile, @NotNull String serverId);

}
