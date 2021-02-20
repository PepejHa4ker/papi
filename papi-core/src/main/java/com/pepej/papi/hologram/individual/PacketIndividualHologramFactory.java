package com.pepej.papi.hologram.individual;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.pepej.papi.events.Events;
import com.pepej.papi.hologram.HologramLine;
import com.pepej.papi.protocol.Protocol;
import com.pepej.papi.reflect.MinecraftVersion;
import com.pepej.papi.reflect.MinecraftVersions;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.serialize.Position;
import com.pepej.papi.terminable.composite.CompositeTerminable;
import com.pepej.papi.text.Text;
import com.pepej.papi.utils.entityspawner.EntitySpawner;
import lombok.Data;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PacketIndividualHologramFactory implements IndividualHologramFactory {

    @NonNull
    @Override
    public IndividualHologram newHologram(@NonNull Position position, @NonNull List<HologramLine> lines) {
        return new PacketHologram(position, lines);
    }

    @Data
    private static final class HologramEntity {
        private ArmorStand armorStand;
        private HologramLine line;
        private int entityId;

        private final Map<Integer, WrappedWatchableObject> cachedMetadata = new HashMap<>();

        private HologramEntity(HologramLine line) {
            this.line = line;
        }
        public Map<Integer, WrappedWatchableObject> getCachedMetadata() {
            return this.cachedMetadata;
        }
    }

    private static final class PacketHologram implements IndividualHologram {

        private Position position;
        private final List<HologramLine> lines;
        private final List<HologramEntity> spawnedEntities;
        private final Set<Player> viewers;
        private boolean spawned;

        private CompositeTerminable listener;

        PacketHologram(@NonNull Position position, @NonNull List<@NonNull HologramLine> lines) {
            Objects.requireNonNull(position, "position");
            this.position = position;
            updateLines(lines);
            this.lines = new ArrayList<>();
            spawnedEntities = new ArrayList<>();
            viewers = Collections.synchronizedSet(new HashSet<>());

        }


        private Position getNewLinePosition() {
            if (this.spawnedEntities.isEmpty()) {
                return this.position;
            } else {
                // get the last entry
                ArmorStand last = this.spawnedEntities.get(this.spawnedEntities.size() - 1).getArmorStand();
                return Position.of(last.getLocation()).subtract(0.0d, 0.25d, 0.0d);
            }
        }

        @Override
        public void spawn() {
            // ensure listening
            if (this.listener == null) {
                setupPacketListeners();
            }

            // resize to fit any new lines
            int linesSize = this.lines.size();
            int spawnedSize = this.spawnedEntities.size();

            // remove excess lines
            if (linesSize < spawnedSize) {
                int diff = spawnedSize - linesSize;
                for (int i = 0; i < diff; i++) {

                    // get and remove the last entry
                    int index = this.spawnedEntities.size() - 1;

                    // remove the armorstand first
                    ArmorStand as = this.spawnedEntities.get(index).getArmorStand();
                    as.remove();

                    // then remove from the list
                    this.spawnedEntities.remove(index);
                }
            }

            // now enough armorstands are spawned, we can now update the text
            for (int i = 0; i < this.lines.size(); i++) {
                HologramLine line = this.lines.get(i);

                String generatedName = "individual_hologram_line-" + ThreadLocalRandom.current().nextInt(100000000);

                if (i >= this.spawnedEntities.size()) {
                    // add a new line
                    Location loc = getNewLinePosition().toLocation();

                    // init the holo entity before actually spawning (so the listeners can catch it)
                    HologramEntity holoEntity = new HologramEntity(line);
                    this.spawnedEntities.add(holoEntity);

                    // ensure the hologram's chunk is loaded.
                    Chunk chunk = loc.getChunk();
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }

                    // spawn the armorstand
                    EntitySpawner.INSTANCE.spawn(loc, ArmorStand.class, as -> {
                        int eid = as.getEntityId();
                        holoEntity.setEntityId(eid);
                        holoEntity.setArmorStand(as);

                        as.setSmall(true);
                        as.setMarker(true);
                        as.setArms(false);
                        as.setBasePlate(false);
                        as.setGravity(false);
                        as.setVisible(false);
                        as.setCustomName(generatedName);
                        as.setCustomNameVisible(true);

                        if (MinecraftVersion.getRuntimeVersion().isAfterOrEq(MinecraftVersions.v1_9)) {
                            as.setAI(false);
                            as.setCollidable(false);
                            as.setInvulnerable(true);
                        }
                    });

                } else {
                    // update existing line if necessary
                    HologramEntity as = this.spawnedEntities.get(i);

                    if (!as.getLine().equals(line)) {
                        as.setLine(line);
                        as.getArmorStand().setCustomName(generatedName);
                    }
                }
            }

            this.spawned = true;
        }

        @Override
        public void delete() {
            this.spawnedEntities.forEach(e -> e.getArmorStand().remove());
            this.spawnedEntities.clear();
            this.spawned = false;

            if (this.listener != null) {
                this.listener.closeAndReportException();
            }
            this.listener = null;
        }

        @Override
        public boolean isSpawned() {
            if (!this.spawned) {
                return false;
            }

            for (HologramEntity stand : this.spawnedEntities) {
                if (!stand.getArmorStand().isValid()) {
                    return false;
                }
            }

            return true;
        }

        @NonNull
        @Override
        public Collection<ArmorStand> getArmorStands() {
            return spawnedEntities.stream().map(HologramEntity::getArmorStand).collect(Collectors.toSet());
        }

        @Nullable
        @Override
        public ArmorStand getArmorStand(int line) {
            if (line >= spawnedEntities.size()) {
                return null;
            }
            return spawnedEntities.get(line).armorStand;
        }


        @Override
        public void updatePosition(@NonNull Position position) {
            Objects.requireNonNull(position, "position");
            if (this.position.equals(position)) {
                return;
            }
            if (isClosed() || !isSpawned()) {
                return;
            }

            this.position = position;
            delete();
            spawn();
        }

        @Override
        public void onPluginDisable(@NonNull final Plugin plugin) {
            this.close();
        }

        @Override
        public void addExpiring(final long ticksDelay) {
            Schedulers.sync().runLater(this::close, ticksDelay).bindWith(listener);

        }

        @Override
        public void addExpiring(final long delay, final TimeUnit unit) {
            Schedulers.sync().runLater(this::close, delay, unit).bindWith(listener);
        }



        @Override
        public void updateLines(@NonNull List<@NonNull HologramLine> lines) {
            Objects.requireNonNull(lines, "lines");
            Preconditions.checkArgument(!lines.isEmpty(), "lines cannot be empty");
            if (this.lines.equals(lines)) {
                return;
            }

            this.lines.clear();
            this.lines.addAll(lines);
        }

        @NonNull
        @Override
        public Set<Player> getViewers() {
            return ImmutableSet.copyOf(this.viewers);
        }

        @Override
        public void addViewer(@NonNull Player player) {
            if (!this.viewers.add(player)) {
                return;
            }

            boolean modern = MinecraftVersion.getRuntimeVersion().isAfterOrEq(MinecraftVersions.v1_9);

            // handle resending
            for (HologramEntity entity : this.spawnedEntities) {
                PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
                spawnPacket.getModifier().writeDefaults();

                // write entity id
                spawnPacket.getIntegers().write(0, entity.getEntityId());

                // write unique id
                if (modern) {
                    spawnPacket.getUUIDs().write(0, entity.getArmorStand().getUniqueId());
                }

                // write coordinates
                Location loc = entity.getArmorStand().getLocation();

                if (modern) {
                    spawnPacket.getDoubles().write(0, loc.getX());
                    spawnPacket.getDoubles().write(1, loc.getY());
                    spawnPacket.getDoubles().write(2, loc.getZ());
                } else {
                    spawnPacket.getIntegers().write(1, (int) Math.floor(loc.getX() * 32));
                    spawnPacket.getIntegers().write(2, (int) Math.floor(loc.getY() * 32));
                    spawnPacket.getIntegers().write(3, (int) Math.floor(loc.getZ() * 32));
                }

                spawnPacket.getIntegers().write(modern ? 4 : 7, (int) ((loc.getPitch()) * 256.0F / 360.0F));
                spawnPacket.getIntegers().write(modern ? 5 : 8, (int) ((loc.getYaw()) * 256.0F / 360.0F));

                // write type
                spawnPacket.getIntegers().write(modern ? 6 : 9, 78);

                // write object data
                spawnPacket.getIntegers().write(modern ? 7 : 10, 0);

                // send it
                Protocol.sendPacket(player, spawnPacket);


                // send missed metadata
                PacketContainer metadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

                // write entity id
                metadataPacket.getIntegers().write(0, entity.getEntityId());

                // write metadata
                List<WrappedWatchableObject> watchableObjects = new ArrayList<>();
                // set custom name
                watchableObjects.add(new WrappedWatchableObject(2, Text.colorize(entity.getLine().resolve(player))));
                // set custom name visible
                if (modern) {
                    watchableObjects.add(new WrappedWatchableObject(3, true));
                } else {
                    watchableObjects.add(new WrappedWatchableObject(3, (byte) 1));
                }

                // re-add all other cached metadata
                for (Map.Entry<Integer, WrappedWatchableObject> ent : entity.getCachedMetadata().entrySet()) {
                    if (ent.getKey() != 2 && ent.getKey() != 3) {
                        watchableObjects.add(ent.getValue());
                    }
                }

                metadataPacket.getWatchableCollectionModifier().write(0, watchableObjects);

                // send it
                Protocol.sendPacket(player, metadataPacket);
            }
        }

        @Override
        public void removeViewer(@NonNull Player player) {
            if (!this.viewers.remove(player)) {
                return;
            }

            // handle removing the existing entity?
            PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

            // set ids
            int[] ids = this.spawnedEntities.stream().mapToInt(HologramEntity::getEntityId).toArray();
            destroyPacket.getIntegerArrays().write(0, ids);

            Protocol.sendPacket(player, destroyPacket);
        }

        @Override
        public void close() {
            delete();
        }

        @Override
        public boolean isClosed() {
            return !this.spawned;
        }

        private HologramEntity getHologramEntity(int entityId) {
            for (HologramEntity entity : PacketHologram.this.spawnedEntities) {
                if (entity.getEntityId() == entityId) {
                    return entity;
                }
            }
            return null;
        }

        private void setupPacketListeners() {
            this.listener = CompositeTerminable.create();

            // remove players when they quit
            Events.subscribe(PlayerQuitEvent.class)
                  .handler(e -> this.viewers.remove(e.getPlayer()))
                  .bindWith(this.listener);

            Events.subscribe(PluginDisableEvent.class)
                  .filter(e -> e.getPlugin().getName().equals("papi"))
                  .handler(event -> onPluginDisable(event.getPlugin()))
                  .bindWith(this.listener);
            Protocol.subscribe(ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_METADATA)
                    .handler(e -> {
                        PacketContainer packet = e.getPacket();
                        Player player = e.getPlayer();

                        // get entity id
                        int entityId = packet.getIntegers().read(0);

                        // find a matching hologram line
                        HologramEntity hologram = getHologramEntity(entityId);
                        if (hologram == null) {
                            return;
                        }

                        // get metadata
                        List<WrappedWatchableObject> metadata = new ArrayList<>(packet.getWatchableCollectionModifier().read(0));

                        if (!this.viewers.contains(player)) {
                            // attempt to cache metadata anyway
                            for (WrappedWatchableObject value : metadata) {
                                if (value.getIndex() != 2) {
                                    hologram.getCachedMetadata().put(value.getIndex(), value);
                                }
                            }

                            e.setCancelled(true);
                            return;
                        }

                        // process metadata
                        for (WrappedWatchableObject value : metadata) {
                            if (value.getIndex() == 2) {
                                value.setValue(Text.colorize(hologram.getLine().resolve(player)));
                            }
                            else {
                                // cache the metadata
                                hologram.getCachedMetadata().put(value.getIndex(), value);
                            }
                        }

                        packet = packet.deepClone();
                        packet.getWatchableCollectionModifier().write(0, metadata);
                        e.setPacket(packet);
                    })
                    .bindWith(this.listener);

            Protocol.subscribe(ListenerPriority.MONITOR, PacketType.Play.Server.SPAWN_ENTITY)
                    .handler(e -> {
                        PacketContainer packet = e.getPacket();
                        Player player = e.getPlayer();

                        // get entity id
                        int entityId = packet.getIntegers().read(0);

                        // find a matching hologram
                        HologramEntity hologram = getHologramEntity(entityId);
                        if (hologram == null) {
                            return;
                        }

                        if (!this.viewers.contains(player)) {
                            e.setCancelled(true);
                        }
                    })
                    .bindWith(this.listener);

        }
    }
}
