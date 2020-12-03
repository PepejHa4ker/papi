package com.pepej.papi.profiles;

import com.pepej.papi.promise.Promise;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

/**
 * A repository of profiles, which can get or lookup {@link Profile} instances
 * for given unique ids or names.
 *
 * <p>Methods which are prefixed with <b>get</b> perform a quick local search,
 * and return no result if no value is cached locally.</p>
 *
 * <p>Methods which are prefixed with <b>lookup</b> perform a more complete search,
 * usually querying an underlying database.</p>
 */
public interface ProfileRepository {

    /**
     * Gets a profile from this repository, using the unique id as the base for
     * the request.
     *
     * <p>If this repository does not contain a profile matching the unique id, a
     * profile will still be returned, but will not be populated with a name.</p>
     *
     * @param uniqueId the unique id to get a profile for
     * @return a profile for the uuid
     */
    @NonNull
    Profile getProfile(@NonNull UUID uniqueId);

    /**
     * Gets a profile from this repository, using the name as the base
     * for the request.
     *
     * <p>If this repository does not contain a profile matching the name, an
     * empty optional will be returned.</p>
     *
     * <p>In the case that there is more than one profile in the repository
     * matching the name, the most up-to-date record is returned.</p>
     *
     * @param name the name to get a profile for
     * @return a profile for the name
     */
    @NonNull
    Optional<Profile> getProfile(@NonNull String name);

    /**
     * Gets a collection of profiles known to the repository.
     *
     * <p>Returned profiles will always be populated with both a unique id
     * and a username.</p>
     *
     * @return a collection of known profiles
     */
    @NonNull
    Collection<Profile> getKnownProfiles();

    /**
     * Populates a map of unique id to profile for the given iterable of unique ids.
     *
     * <p>The map will only contain an entry for each given unique id if there is a
     * corresponding profile for the unique id in the repository.</p>
     *
     * @param uniqueIds the unique ids to get profiles for
     * @return a map of uuid to profile, where possible, for each uuid in the iterable
     * @see #getProfile(UUID)
     */
    @NonNull
    default Map<UUID, Profile> getProfiles(@NonNull Iterable<UUID> uniqueIds) {
        Objects.requireNonNull(uniqueIds, "uniqueIds");
        Map<UUID, Profile> ret = new HashMap<>();
        for (UUID uniqueId : uniqueIds) {
            Profile profile = getProfile(uniqueId);
            if (profile.getName().isPresent()) {
                ret.put(uniqueId, profile);
            }
        }
        return ret;
    }

    /**
     * Populates a map of name to profile for the given iterable of names.
     *
     * <p>The map will only contain an entry for each given name if there is a
     * corresponding profile for the name in the repository.</p>
     *
     * @param names the names to get profiles for
     * @return a map of name to profile, where possible, for each name in the iterable
     * @see #getProfile(String)
     */
    @NonNull
    default Map<String, Profile> getProfilesByName(@NonNull Iterable<String> names) {
        Objects.requireNonNull(names, "names");
        Map<String, Profile> ret = new HashMap<>();
        for (String name : names) {
            getProfile(name).ifPresent(p -> ret.put(name, p));
        }
        return ret;
    }

    /**
     * Gets a profile from this repository, using the unique id as the base for
     * the request.
     *
     * <p>If this repository does not contain a profile matching the unique id, a
     * profile will still be returned, but will not be populated with a name.</p>
     *
     * @param uniqueId the unique id to get a profile for
     * @return a profile for the uuid
     */
    @NonNull
    Promise<Profile> lookupProfile(@NonNull UUID uniqueId);

    /**
     * Gets a profile from this repository, using the name as the base
     * for the request.
     *
     * <p>If this repository does not contain a profile matching the name, an
     * empty optional will be returned.</p>
     *
     * <p>In the case that there is more than one profile in the repository
     * matching the name, the most up-to-date record is returned.</p>
     *
     * @param name the name to get a profile for
     * @return a profile for the name
     */
    @NonNull
    Promise<Optional<Profile>> lookupProfile(@NonNull String name);

    /**
     * Gets a collection of profiles known to the repository.
     *
     * <p>Returned profiles will always be populated with both a unique id
     * and a username.</p>
     *
     * @return a collection of known profiles
     */
    @NonNull
    Promise<Collection<Profile>> lookupKnownProfiles();

    /**
     * Populates a map of unique id to profile for the given iterable of unique ids.
     *
     * <p>The map will only contain an entry for each given unique id if there is a
     * corresponding profile for the unique id in the repository.</p>
     *
     * @param uniqueIds the unique ids to get profiles for
     * @return a map of uuid to profile, where possible, for each uuid in the iterable
     * @see #getProfile(UUID)
     */
    @NonNull
    Promise<Map<UUID, Profile>> lookupProfiles(@NonNull Iterable<UUID> uniqueIds);

    /**
     * Populates a map of name to profile for the given iterable of names.
     *
     * <p>The map will only contain an entry for each given name if there is a
     * corresponding profile for the name in the repository.</p>
     *
     * @param names the names to get profiles for
     * @return a map of name to profile, where possible, for each name in the iterable
     * @see #getProfile(String)
     */
    @NonNull
    Promise<Map<String, Profile>> lookupProfilesByName(@NonNull Iterable<String> names);

}
