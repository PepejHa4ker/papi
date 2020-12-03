package com.pepej.papi.metadata;

import com.google.common.collect.ImmutableMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A map which holds {@link MetadataKey}s, and maps them to persistent or transient (expiring / weak) values.
 *
 * Transient values will only be removed during map maintenance, which occurs during a housekeeping task running
 * every minute. Method signatures for the {@link #has(MetadataKey)} and equivalent remain accurate though.
 *
 * @see TransientValue
 */
public interface MetadataMap {

    @NonNull
    static MetadataMap create() {
        return new MetadataMapImpl();
    }

    /**
     * Adds a metadata key and corresponding value into this map, removing any previous value if present.
     *
     * If a MetadataKey with the same id is already contained within this map, a {@link ClassCastException} will be
     * thrown if they do not share the same type, and the existing value will remain.
     *
     * @param key the key to put
     * @param value the value to map against (non null)
     * @param <T> the value type
     * @throws ClassCastException if any existing key with the same id has a differing type to the key being add
     */
    <T> void put(@NonNull MetadataKey<T> key, @NonNull T value);

    /**
     * Adds a metadata key and corresponding value into this map, removing any previous value if present.
     *
     * If a MetadataKey with the same id is already contained within this map, a {@link ClassCastException} will be
     * thrown if they do not share the same type, and the existing value will remain.
     *
     * @param key the key to put
     * @param value the value to map against (non null)
     * @param <T> the value type
     * @throws ClassCastException if any existing key with the same id has a differing type to the key being add
     */
    <T> void put(@NonNull MetadataKey<T> key, @NonNull TransientValue<T> value);

    /**
     * Adds a metadata key and corresponding value into this map, removing any previous value if present.
     *
     * Unlike {@link #put(MetadataKey, Object)}, the existing value if present does not need to have the same type.
     *
     * @param key the key to put
     * @param value the value to map against (non null)
     * @param <T> the value type
     */
    <T> void forcePut(@NonNull MetadataKey<T> key, @NonNull T value);

    /**
     * Adds a metadata key and corresponding value into this map, removing any previous value if present.
     *
     * Unlike {@link #put(MetadataKey, Object)}, the existing value if present does not need to have the same type.
     *
     * @param key the key to put
     * @param value the value to map against (non null)
     * @param <T> the value type
     */
    <T> void forcePut(@NonNull MetadataKey<T> key, @NonNull TransientValue<T> value);

    /**
     * Adds a metadata key and corresponding value into this map, only if an existing key is not present.
     *
     * @param key the key to put
     * @param value the value to map against (non null)
     * @param <T> the value type
     * @return true if there wasn't an existing key, and the key was added
     */
    <T> boolean putIfAbsent(@NonNull MetadataKey<T> key, @NonNull T value);

    /**
     * Adds a metadata key and corresponding value into this map, only if an existing key is not present.
     *
     * @param key the key to put
     * @param value the value to map against (non null)
     * @param <T> the value type
     * @return true if there wasn't an existing key, and the key was added
     */
    <T> boolean putIfAbsent(@NonNull MetadataKey<T> key, @NonNull TransientValue<T> value);

    /**
     * Gets an optional value for the given key.
     *
     * @param key the metadata key to get
     * @param <T> the value type
     * @return an optional containing the value
     * @throws ClassCastException if there is a key held in the map with the same id but differing type to the given key.
     */
    @NonNull
    <T> Optional<T> get(@NonNull MetadataKey<T> key);

    /**
     * Attempts to get a value for the given key, and applies the action is present.
     *
     * @param key the metadata key to lookup
     * @param action the action to apply
     * @param <T> the value type
     * @return true if the action was applied
     * @throws ClassCastException if there is a key held in the map with the same id but differing type to the given key.
     */
    <T> boolean ifPresent(@NonNull MetadataKey<T> key, @NonNull Consumer<? super T> action);

    /**
     * Gets a value for the given key, or null if one isn't present.
     *
     * @param <T> the value type
     * @param key the metadata key to get
     * @return the value, or null if no key is present.
     * @throws ClassCastException if there is a key held in the map with the same id but differing type to the given key.
     */
    @Nullable
    <T> T getOrNull(@NonNull MetadataKey<T> key);

    /**
     * Gets a value for the given key, or returns the default if one isn't present.
     *
     * @param key the metadata key to get
     * @param def the default value
     * @param <T> the value type
     * @return the value, or the default if no key is present.
     * @throws ClassCastException if there is a key held in the map with the same id but differing type to the given key.
     */
    @NonNull
    <T> T getOrDefault(@NonNull MetadataKey<T> key, @Nullable T def);

    /**
     * Gets a value for the given key, or puts and returns the default if one isn't present.
     *
     * @param key the metadata key to get
     * @param def the default value
     * @param <T> the value type
     * @return the value, or the supplied value if no key is present.
     * @throws ClassCastException if there is a key held in the map with the same id but differing type to the given key.
     */
    @NonNull
    <T> T getOrPut(@NonNull MetadataKey<T> key, @NonNull Supplier<? extends T> def);

    /**
     * Gets a value for the given key, or puts and returns the default if one isn't present.
     *
     * @param key the metadata key to get
     * @param def the default value
     * @param <T> the value type
     * @return the value, or the supplied value if no key is present.
     * @throws ClassCastException if there is a key held in the map with the same id but differing type to the given key.
     */
    @NonNull
    <T> T getOrPutExpiring(@NonNull MetadataKey<T> key, @NonNull Supplier<? extends TransientValue<T>> def);

    /**
     * Returns if this map contains the given key, and the types of each key match.
     *
     * @param key the key to check for
     * @return true if this map contains the key
     */
    boolean has(@NonNull MetadataKey<?> key);

    /**
     * Removes the given key from this map
     *
     * @param key the key to remove
     * @return true if a value was removed from the map
     */
    boolean remove(@NonNull MetadataKey<?> key);

    /**
     * Clears the map
     */
    void clear();

    /**
     * Returns an immutable view of the backing map
     *
     * @return an immutable view of the backing map
     */
    @NonNull
    ImmutableMap<MetadataKey<?>, Object> asMap();

    /**
     * Returns if the map is empty
     *
     * @return true if the map is empty
     */
    boolean isEmpty();

    /**
     * Removes expired {@link TransientValue}s from this map.
     *
     * <p>Note that this method does need to be explicitly called - as cleanup happens naturally over time.</p>
     */
    void cleanup();

}
