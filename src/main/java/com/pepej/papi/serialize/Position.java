package com.pepej.papi.serialize;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pepej.papi.Papi;
import com.pepej.papi.gson.GsonSerializable;
import com.pepej.papi.gson.JsonBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * An immutable and serializable location object
 */
public final class Position implements GsonSerializable {
    public static Position deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("x"));
        Preconditions.checkArgument(object.has("y"));
        Preconditions.checkArgument(object.has("z"));
        Preconditions.checkArgument(object.has("world"));

        double x = object.get("x").getAsDouble();
        double y = object.get("y").getAsDouble();
        double z = object.get("z").getAsDouble();
        String world = object.get("world").getAsString();

        return of(x, y, z, world);
    }

    public static Position of(double x, double y, double z, String world) {
        Objects.requireNonNull(world, "world");
        return new Position(x, y, z, world);
    }

    public static Position of(double x, double y, double z, World world) {
        Objects.requireNonNull(world, "world");
        return of(x, y, z, world.getName());
    }

    public static Position of(Vector3d vector, String world) {
        Objects.requireNonNull(vector, "vector");
        Objects.requireNonNull(world, "world");
        return of(vector.getX(), vector.getY(), vector.getZ(), world);
    }

    public static Position of(Vector3d vector, World world) {
        Objects.requireNonNull(vector, "vector");
        Objects.requireNonNull(world, "world");
        return of(vector.getX(), vector.getY(), vector.getZ(), world);
    }

    public static Position of(Location location) {
        Objects.requireNonNull(location, "location");
        return of(location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
    }

    public static Position of(Block block) {
        Objects.requireNonNull(block, "block");
        return of(block.getLocation());
    }

    private final double x;
    private final double y;
    private final double z;
    private final String world;

    @Nullable
    private Location bukkitLocation = null;

    private Position(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public String getWorld() {
        return this.world;
    }

    public Vector3d toVector() {
        return new Vector3d(this.x, this.y, this.z);
    }

    public synchronized Location toLocation() {
        if (this.bukkitLocation == null) {
            this.bukkitLocation = new Location(Papi.worldNullable(this.world), this.x, this.y, this.z);
        }

        return this.bukkitLocation.clone();
    }

    public BlockPosition floor() {
        return BlockPosition.of(bukkitFloor(this.x), bukkitFloor(this.y), bukkitFloor(this.z), this.world);
    }

    public Position getRelative(BlockFace face) {
        Objects.requireNonNull(face, "face");
        return Position.of(this.x + face.getModX(), this.y + face.getModY(), this.z + face.getModZ(), this.world);
    }

    public Position getRelative(BlockFace face, double distance) {
        Objects.requireNonNull(face, "face");
        return Position.of(this.x + (face.getModX() * distance), this.y + (face.getModY() * distance), this.z + (face.getModZ() * distance), this.world);
    }

    public Position add(Vector3i vector3i) {
        return add(vector3i.getX(), vector3i.getY(), vector3i.getZ());
    }

    public Position add(Vector3d vector3d) {
        return add(vector3d.getX(), vector3d.getY(), vector3d.getZ());
    }

    public Position add(double x, double y, double z) {
        return Position.of(this.x + x, this.y + y, this.z + z, this.world);
    }

    public Position subtract(Vector3i vector3i) {
        return subtract(vector3i.getX(), vector3i.getY(), vector3i.getZ());
    }

    public Position subtract(Vector3d vector3d) {
        return subtract(vector3d.getX(), vector3d.getY(), vector3d.getZ());
    }

    public Position subtract(double x, double y, double z) {
        return add(-x, -y, -z);
    }

    public Region regionWith(Position other) {
        Objects.requireNonNull(other, "other");
        return Region.of(this, other);
    }

    public Point withDirection(Direction direction) {
        return Point.of(this, direction);
    }

    @Nonnull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                          .add("x", this.x)
                          .add("y", this.y)
                          .add("z", this.z)
                          .add("world", this.world)
                          .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Position)) return false;
        final Position other = (Position) o;
        return Double.compare(this.getX(), other.getX()) == 0 &&
                Double.compare(this.getY(), other.getY()) == 0 &&
                Double.compare(this.getZ(), other.getZ()) == 0 &&
                this.getWorld().equals(other.getWorld());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;

        final long x = Double.doubleToLongBits(this.getX());
        final long y = Double.doubleToLongBits(this.getY());
        final long z = Double.doubleToLongBits(this.getZ());

        result = result * PRIME + (int) (x >>> 32 ^ x);
        result = result * PRIME + (int) (y >>> 32 ^ y);
        result = result * PRIME + (int) (z >>> 32 ^ z);
        result = result * PRIME + this.getWorld().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Position(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", world=" + this.getWorld() + ")";
    }

    private static int bukkitFloor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

}
