package com.pepej.papi.metadata;

import com.google.common.collect.ImmutableMap;
import com.pepej.papi.metadata.type.*;
import com.pepej.papi.serialize.BlockPosition;
import com.pepej.papi.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * The Metadata registries provided by papi.
 * <p>
 * These instances can be accessed through {@link Metadata}.
 */
final class StandardMetadataRegistries {

    public static final UUIDMetadataRegistry UUID = new UUIDRegistry();
    public static final PlayerMetadataRegistry PLAYER = new PlayerRegistry();
    public static final EntityMetadataRegistry ENTITY = new EntityRegistry();
    public static final BlockMetadataRegistry BLOCK = new BlockRegistry();
    public static final WorldMetadataRegistry WORLD = new WorldRegistry();

    private static final MetadataRegistry<?>[] VALUES = new MetadataRegistry[]{UUID, PLAYER, ENTITY, BLOCK, WORLD};

    public static MetadataRegistry<?>[] values() {
        return VALUES;
    }

    private static final class UUIDRegistry extends AbstractMetadataRegistry<UUID> implements UUIDMetadataRegistry {

        @NonNull
        @Override
        public MetadataMap provide(@NonNull UUID uuid) {
            Objects.requireNonNull(uuid, "uuid");
            return provide(uuid);
        }

        @NonNull
        @Override
        public Optional<MetadataMap> get(@NonNull UUID uuid) {
            Objects.requireNonNull(uuid, "uuid");
            return get(uuid);
        }

        @NonNull
        @Override
        public <K> Map<UUID, K> getAllWithKey(@NonNull MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<UUID, K> ret = ImmutableMap.builder();
            this.cache.asMap().forEach((uuid, map) -> map.get(key).ifPresent(t -> ret.put(uuid, t)));
            return ret.build();
        }
    }

    private static final class PlayerRegistry extends AbstractMetadataRegistry<UUID> implements PlayerMetadataRegistry {

        @NonNull
        @Override
        public MetadataMap provide(@NonNull Player player) {
            Objects.requireNonNull(player, "player");
            return provide(player.getUniqueId());
        }

        @NonNull
        @Override
        public Optional<MetadataMap> get(@NonNull Player player) {
            Objects.requireNonNull(player, "player");
            return get(player.getUniqueId());
        }

        @NonNull
        @Override
        public <K> Map<Player, K> getAllWithKey(@NonNull MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<Player, K> ret = ImmutableMap.builder();
            this.cache.asMap().forEach((uuid, map) -> map.get(key).ifPresent(t -> {
                Player player = Players.getNullable(uuid);
                if (player != null) {
                    ret.put(player, t);
                }
            }));
            return ret.build();
        }
    }

    private static final class EntityRegistry extends AbstractMetadataRegistry<UUID> implements EntityMetadataRegistry {

        @NonNull
        @Override
        public MetadataMap provide(@NonNull Entity entity) {
            Objects.requireNonNull(entity, "entity");
            return provide(entity.getUniqueId());
        }

        @NonNull
        @Override
        public Optional<MetadataMap> get(@NonNull Entity entity) {
            Objects.requireNonNull(entity, "entity");
            return get(entity.getUniqueId());
        }

        @NonNull
        @Override
        public <K> Map<Entity, K> getAllWithKey(@NonNull MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<Entity, K> ret = ImmutableMap.builder();
            this.cache.asMap().forEach((uuid, map) -> map.get(key).ifPresent(t -> {
                Entity entity = Bukkit.getEntity(uuid);
                if (entity != null) {
                    ret.put(entity, t);
                }
            }));
            return ret.build();
        }
    }

    private static final class BlockRegistry extends AbstractMetadataRegistry<BlockPosition> implements BlockMetadataRegistry {

        @NonNull
        @Override
        public MetadataMap provide(@NonNull Block block) {
            Objects.requireNonNull(block, "block");
            return provide(BlockPosition.of(block));
        }

        @NonNull
        @Override
        public Optional<MetadataMap> get(@NonNull Block block) {
            Objects.requireNonNull(block, "block");
            return get(BlockPosition.of(block));
        }

        @NonNull
        @Override
        public <K> Map<BlockPosition, K> getAllWithKey(@NonNull MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<BlockPosition, K> ret = ImmutableMap.builder();
            this.cache.asMap().forEach((pos, map) -> map.get(key).ifPresent(t -> ret.put(pos, t)));
            return ret.build();
        }
    }

    private static final class WorldRegistry extends AbstractMetadataRegistry<UUID> implements WorldMetadataRegistry {

        @NonNull
        @Override
        public MetadataMap provide(@NonNull World world) {
            Objects.requireNonNull(world, "world");
            return provide(world.getUID());
        }

        @NonNull
        @Override
        public Optional<MetadataMap> get(@NonNull World world) {
            Objects.requireNonNull(world, "world");
            return get(world.getUID());
        }

        @NonNull
        @Override
        public <K> Map<World, K> getAllWithKey(@NonNull MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<World, K> ret = ImmutableMap.builder();
            this.cache.asMap().forEach((uuid, map) -> map.get(key).ifPresent(t -> {
                World world = Bukkit.getWorld(uuid);
                if (world != null) {
                    ret.put(world, t);
                }
            }));
            return ret.build();
        }
    }

    private StandardMetadataRegistries() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
