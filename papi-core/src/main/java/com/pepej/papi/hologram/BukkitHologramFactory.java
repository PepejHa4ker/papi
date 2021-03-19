package com.pepej.papi.hologram;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.pepej.papi.events.Events;
import com.pepej.papi.gson.JsonBuilder;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.serialize.Position;
import com.pepej.papi.terminable.composite.CompositeTerminable;
import com.pepej.papi.text.Text;
import lombok.SneakyThrows;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BukkitHologramFactory implements HologramFactory {

    @NonNull
    @Override
    public Hologram newHologram(@NonNull Position position, @NonNull List<String> lines) {
        return new BukkitHologram(position, lines);
    }

    @NonNull
    @Override
    public Hologram newHologram(@NonNull Position position, @NonNull String... lines) {
        return new BukkitHologram(position, Arrays.asList(lines));
    }

    private static final class BukkitHologram implements Hologram {
        private static final Method SET_CAN_TICK;

        static {
            Method setCanTick = null;
            try {
                setCanTick = ArmorStand.class.getDeclaredMethod("setCanTick", boolean.class);
            } catch (Exception ignored) {}
            SET_CAN_TICK = setCanTick;
        }

        private Position position;
        private final List<String> lines = new ArrayList<>();
        private final List<ArmorStand> spawnedEntities = new ArrayList<>();
        private boolean spawned = false;

        private CompositeTerminable listeners = null;

        BukkitHologram(Position position, List<String> lines) {
            this.position = Objects.requireNonNull(position, "position");
            updateLines(lines);
        }

        private Position getNewLinePosition() {
            if (this.spawnedEntities.isEmpty()) {
                return this.position;
            } else {
                // get the last entry
                ArmorStand last = this.spawnedEntities.get(this.spawnedEntities.size() - 1);
                return Position.of(last.getLocation()).subtract(0.0d, 0.25d, 0.0d);
            }
        }

        @Override
        @SneakyThrows
        public void spawn() {
            if (this.listeners == null) {
                setupListeners();
            }
            // resize to fit any new lines
            int linesSize = this.lines.size();
            int spawnedSize = this.spawnedEntities.size();

            // remove excess lines
            if (linesSize < spawnedSize) {
                int diff = spawnedSize - linesSize;
                for (int i = 0; i < diff; i++) {

                    int index = this.spawnedEntities.size() - 1;

                    // remove the armorstand first
                    ArmorStand as = this.spawnedEntities.get(index);
                    as.remove();

                    // then remove from the list
                    this.spawnedEntities.remove(index);

                }
            }

            // now enough armor stands are spawned, we can now update the text
            for (int i = 0; i < this.lines.size(); i++) {
                String line = this.lines.get(i);

                if (i >= this.spawnedEntities.size()) {
                    // add a new line
                    Location loc = getNewLinePosition().toLocation();

                    // ensure the hologram's chunk is loaded.
                    Chunk chunk = loc.getChunk();
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }

                    // remove any armor stands already at this location. (leftover from a server restart)
                    loc.getWorld().getNearbyEntities(loc, 1.0, 1.0, 1.0).forEach(e -> {
                        if (e.getType() == EntityType.ARMOR_STAND && locationsEqual(e.getLocation(), loc)) {
                            e.remove();
                        }
                    });

                    ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class);
                    as.setSmall(true);
                    as.setMarker(true);
                    as.setArms(false);
                    as.setBasePlate(false);
                    as.setGravity(false);
                    as.setVisible(false);
                    as.setCustomName(line);
                    as.setCustomNameVisible(true);
                    as.setAI(false);
                    as.setCollidable(false);
                    as.setInvulnerable(true);
                    if (SET_CAN_TICK != null) {
                        try {
                            SET_CAN_TICK.invoke(as, false);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                    this.spawnedEntities.add(as);
                } else {
                    // update existing line if necessary
                    ArmorStand as = this.spawnedEntities.get(i);

                    if (as.getCustomName() != null && as.getCustomName().equals(line)) {
                        continue;
                    }

                    as.setCustomName(line);
                }
            }


            this.spawned = true;
        }

        private void setupListeners() {
            this.listeners = CompositeTerminable.create();

            Events.subscribe(PluginDisableEvent.class)
                  .filter(e -> e.getPlugin().getName().equals("papi"))
                  .handler(e -> onPluginDisable(e.getPlugin()))
                  .bindWith(this.listeners);
        }

        @Override
        public void delete() {
            this.spawnedEntities.forEach(Entity::remove);
            this.spawnedEntities.clear();
            this.spawned = false;

            if (this.listeners != null) {
                this.listeners.closeAndReportException();
            }
            this.listeners = null;
        }

        @Override
        public boolean isSpawned() {
            if (!this.spawned) {
                return false;
            }

            for (ArmorStand stand : this.spawnedEntities) {
                if (!stand.isValid()) {
                    return false;
                }
            }

            return true;
        }

        @NonNull
        @Override
        public Collection<ArmorStand> getArmorStands() {
            return this.spawnedEntities;
        }

        @Nullable
        @Override
        public ArmorStand getArmorStand(int line) {
            if (line >= this.spawnedEntities.size()) {
                return null;
            }
            return this.spawnedEntities.get(line);
        }

        @Override
        public void updatePosition(@NonNull Position position) {
            Objects.requireNonNull(position, "position");
            if (this.position.equals(position)) {
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
            Schedulers.sync().runLater(this::close, ticksDelay);

        }

        @Override
        public void addExpiring(final long delay, final TimeUnit unit) {
            Schedulers.sync().runLater(this::close, delay, unit);
        }

        @Override
        public void updateLines(@NonNull List<String> lines) {
            Objects.requireNonNull(lines, "lines");
            Preconditions.checkArgument(!lines.isEmpty(), "lines cannot be empty");
            for (String line : lines) {
                Preconditions.checkArgument(line != null, "null line");
            }

            List<String> ret = lines.stream().map(Text::colorize).collect(Collectors.toList());
            if (this.lines.equals(ret)) {
                return;
            }

            this.lines.clear();
            this.lines.addAll(ret);

        }

        @Override
        public void close() {
            delete();
        }

        @Override
        public boolean isClosed() {
            return !this.spawned;
        }

        @NonNull
        @Override
        public JsonObject serialize() {
            return JsonBuilder.object()
                              .add("position", this.position)
                              .add("lines", JsonBuilder.array().addStrings(this.lines).build())
                              .build();
        }

        private static boolean locationsEqual(Location l1, Location l2) {
            return Double.doubleToLongBits(l1.getX()) == Double.doubleToLongBits(l2.getX()) &&
                    Double.doubleToLongBits(l1.getY()) == Double.doubleToLongBits(l2.getY()) &&
                    Double.doubleToLongBits(l1.getZ()) == Double.doubleToLongBits(l2.getZ());
        }
    }
}
