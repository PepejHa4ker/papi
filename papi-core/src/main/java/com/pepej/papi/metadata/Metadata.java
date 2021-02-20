package com.pepej.papi.metadata;


import com.pepej.papi.events.Events;
import com.pepej.papi.metadata.type.*;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.serialize.BlockPosition;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides access to {@link MetadataRegistry} instances bound to players, entities, blocks and worlds.
 */
public final class Metadata {

    private static final AtomicBoolean SETUP = new AtomicBoolean(false);

    // lazily load
    private static void ensureSetup() {
        if (SETUP.get()) {
            return;
        }

        if (!SETUP.getAndSet(true)) {

            // remove player metadata when they leave the server
            Events.subscribe(PlayerQuitEvent.class, EventPriority.MONITOR)
                  .handler(e -> StandardMetadataRegistries.PLAYER.remove(e.getPlayer().getUniqueId()));

            // cache housekeeping task
            Schedulers.builder()
                      .async()
                      .afterAndEvery(1, TimeUnit.MINUTES)
                      .run(() -> {
                          for (MetadataRegistry<?> registry : StandardMetadataRegistries.values()) {
                              registry.cleanup();
                          }
                      });
        }
    }

    /**
     * Gets the {@link MetadataRegistry} for {@link Player}s.
     *
     * @return the {@link PlayerMetadataRegistry}
     */
    public static PlayerMetadataRegistry players() {
        ensureSetup();
        return StandardMetadataRegistries.PLAYER;
    }

    /**
     * Gets the {@link MetadataRegistry} for {@link Entity}s.
     *
     * @return the {@link EntityMetadataRegistry}
     */
    public static EntityMetadataRegistry entities() {
        ensureSetup();
        return StandardMetadataRegistries.ENTITY;
    }

    /**
     * Gets the {@link MetadataRegistry} for {@link Block}s.
     *
     * @return the {@link BlockMetadataRegistry}
     */
    public static BlockMetadataRegistry blocks() {
        ensureSetup();
        return StandardMetadataRegistries.BLOCK;
    }

    /**
     * Gets the {@link MetadataRegistry} for {@link World}s.
     *
     * @return the {@link WorldMetadataRegistry}
     */
    public static WorldMetadataRegistry worlds() {
        ensureSetup();
        return StandardMetadataRegistries.WORLD;
    }

    /**
     * Produces a {@link MetadataMap} for the given object.
     *
     * A map will only be returned if the object is an instance of
     * {@link Player}, {@link UUID}, {@link Entity}, {@link Block} or {@link World}.
     *
     * @param obj the object
     * @return a metadata map
     */
    @NonNull
    public static MetadataMap provide(@NonNull Object obj) {
        Objects.requireNonNull(obj, "obj");
        if (obj instanceof Player) {
            return provideForPlayer(((Player) obj));
        } else if (obj instanceof UUID) {
            return provideForPlayer(((UUID) obj));
        } else if (obj instanceof Entity) {
            return provideForEntity(((Entity) obj));
        } else if (obj instanceof Block) {
            return provideForBlock(((Block) obj));
        } else if (obj instanceof World) {
            return provideForWorld(((World) obj));
        } else {
            throw new IllegalArgumentException("Unknown object type: " + obj.getClass());
        }
    }

    /**
     * Gets a {@link MetadataMap} for the given object, if one already exists and has
     * been cached in this registry.
     *
     * A map will only be returned if the object is an instance of
     * {@link Player}, {@link UUID}, {@link Entity}, {@link Block}, {@link World} or {@link ItemStack}.
     *
     * @param obj the object
     * @return a metadata map
     */
    @NonNull
    public static Optional<MetadataMap> get(@NonNull Object obj) {
        Objects.requireNonNull(obj, "obj");
        if (obj instanceof Player) {
            return getForPlayer(((Player) obj));
        } else if (obj instanceof UUID) {
            return getForPlayer(((UUID) obj));
        } else if (obj instanceof Entity) {
            return getForEntity(((Entity) obj));
        } else if (obj instanceof Block) {
            return getForBlock(((Block) obj));
        } else if (obj instanceof World) {
            return getForWorld(((World) obj));
        } else {
            throw new IllegalArgumentException("Unknown object type: " + obj.getClass());
        }
    }

    @NonNull
    public static MetadataMap provideForPlayer(@NonNull UUID uuid) {
        return players().provide(uuid);
    }

    @NonNull
    public static MetadataMap provideForPlayer(@NonNull Player player) {
        return players().provide(player);
    }

    @NonNull
    public static Optional<MetadataMap> getForPlayer(@NonNull UUID uuid) {
        return players().get(uuid);
    }

    @NonNull
    public static Optional<MetadataMap> getForPlayer(@NonNull Player player) {
        return players().get(player);
    }

    @NonNull
    public static <T> Map<Player, T> lookupPlayersWithKey(@NonNull MetadataKey<T> key) {
        return players().getAllWithKey(key);
    }

    @NonNull
    public static MetadataMap provideForEntity(@NonNull UUID uuid) {
        return entities().provide(uuid);
    }

    @NonNull
    public static MetadataMap provideForEntity(@NonNull Entity entity) {
        return entities().provide(entity);
    }

    @NonNull
    public static Optional<MetadataMap> getForEntity(@NonNull UUID uuid) {
        return entities().get(uuid);
    }

    @NonNull
    public static Optional<MetadataMap> getForEntity(@NonNull Entity entity) {
        return entities().get(entity);
    }

    @NonNull
    public static <T> Map<Entity, T> lookupEntitiesWithKey(@NonNull MetadataKey<T> key) {
        return entities().getAllWithKey(key);
    }

    @NonNull
    public static MetadataMap provideForBlock(@NonNull BlockPosition block) {
        return blocks().provide(block);
    }

    @NonNull
    public static MetadataMap provideForBlock(@NonNull Block block) {
        return blocks().provide(block);
    }

    @NonNull
    public static Optional<MetadataMap> getForBlock(@NonNull BlockPosition block) {
        return blocks().get(block);
    }

    @NonNull
    public static Optional<MetadataMap> getForBlock(@NonNull Block block) {
        return blocks().get(block);
    }

    @NonNull
    public static <T> Map<BlockPosition, T> lookupBlocksWithKey(@NonNull MetadataKey<T> key) {
        return blocks().getAllWithKey(key);
    }

    @NonNull
    public static MetadataMap provideForWorld(@NonNull UUID uid) {
        return worlds().provide(uid);
    }

    @NonNull
    public static MetadataMap provideForWorld(@NonNull World world) {
        return worlds().provide(world);
    }

    @NonNull
    public static Optional<MetadataMap> getForWorld(@NonNull UUID uid) {
        return worlds().get(uid);
    }

    @NonNull
    public static Optional<MetadataMap> getForWorld(@NonNull World world) {
        return worlds().get(world);
    }

    @NonNull
    public static <T> Map<World, T> lookupWorldsWithKey(@NonNull MetadataKey<T> key) {
        return worlds().getAllWithKey(key);
    }

    private Metadata() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
