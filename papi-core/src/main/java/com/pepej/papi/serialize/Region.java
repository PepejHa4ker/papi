package com.pepej.papi.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pepej.papi.Papi;
import com.pepej.papi.gson.GsonSerializable;
import com.pepej.papi.gson.JsonBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * An immutable and serializable region object
 */
public final class Region implements GsonSerializable {
    public static Region deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("min"));
        Preconditions.checkArgument(object.has("max"));

        Position a = Position.deserialize(object.get("min"));
        Position b = Position.deserialize(object.get("max"));

        return of(a, b);
    }

    public static Region of(Position a, Position b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");

        if (!a.getWorld().equals(b.getWorld())) {
            throw new IllegalArgumentException("positions are in different worlds");
        }

        return new Region(a, b);
    }

    private final Position min;
    private final Position max;

    @Nullable
    private final World world;

    private final double width;
    private final double height;
    private final double depth;

    private Region(Position a, Position b) {
        this.min = Position.of(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()), a.getWorld());
        this.max = Position.of(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()), a.getWorld());

        this.world = Papi.worldNullable(this.getMin().getWorld());

        this.width = this.max.getX() - this.min.getX();
        this.height = this.max.getY() - this.min.getY();
        this.depth = this.max.getZ() - this.min.getZ();
    }

    public double getVolume() {
        return this.width * this.depth * this.height;
    }

    public boolean inRegion(Position pos) {
        Objects.requireNonNull(pos, "pos");
        return pos.getWorld().equals(this.min.getWorld()) && inRegion(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean inRegion(Location loc) {
        Objects.requireNonNull(loc, "loc");
        return loc.getWorld().getName().equals(this.min.getWorld()) && inRegion(loc.getX(), loc.getY(), loc.getZ());
    }

    public boolean inRegion(double x, double y, double z) {
        return x >= this.min.getX() && x <= this.max.getX()
                && y >= this.min.getY() && y <= this.max.getY()
                && z >= this.min.getZ() && z <= this.max.getZ();
    }

    public Position getMin() {
        return this.min;
    }

    public Position getMax() {
        return this.max;
    }

    @Nullable
    public World getWorld() {
        return world;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public double getDepth() {
        return this.depth;
    }

    @NonNull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                          .add("min", this.min)
                          .add("max", this.max)
                          .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Region)) {
            return false;
        }
        final Region other = (Region) o;
        return this.getMin().equals(other.getMin()) && this.getMax().equals(other.getMax());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getMin().hashCode();
        result = result * PRIME + this.getMax().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Region(min=" + this.getMin() + ", max=" + this.getMax() + ")";
    }
}
