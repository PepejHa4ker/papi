package com.pepej.papi.utils;

import com.pepej.papi.serialize.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class GeometryUtils {

    public static List<Location> getAll(@NonNull final Position min, @NonNull final Position max, @NonNull final World world) {
        return getAll(min.getX(), max.getX(), min.getY(), max.getY(), min.getZ(), max.getZ(), world);

    }


    public static List<Location> getAll(final double xMin, final double xMax, final double yMin, final double yMax, final double zMin, final double zMax, @NonNull final World world) {

        List<Location> toReturn = new ArrayList<>();
        for (double x = xMin; x <= xMax; x++) {
            for (double z = zMin; z <= zMax; z++) {
                for (double y = yMin; y <= yMax; y++) {
                    toReturn.add(new Location(world, x, y, z));
                }
            }
        }
        return toReturn;
    }

    public static List<Location> getFloor(@NonNull final Position min, @NonNull final Position max, @NonNull final World world) {
        return getFloor(min.getX(), max.getX(), min.getY(), min.getZ(), max.getZ(), world);
    }

    public static List<Location> getFloor(final double xMin, final double xMax, final double y, final double zMin, final double zMax, final World world) {
        List<Location> toReturn = new ArrayList<>();
        for (double x = xMin; x <= xMax; x++) {
            for (double z = zMin; z <= zMax; z++) {
                toReturn.add(new Location(world, x, y, z));
            }
        }
        return toReturn;
    }

    public static List<Location> getAllEdges(@NonNull final Position min, @NonNull final Position max, @NonNull final World world) {
        return getAllEdges(min.getX(), max.getX(), min.getY(), max.getY(), min.getZ(), max.getZ(), world);
    }


    public static List<Location> getAllEdges(final double xMin, final double xMax, final double yMin, final double yMax, final double zMin, final double zMax, @NonNull final World world) {
        List<Location> toReturn = new ArrayList<>();
        for (double x = xMin; x <= xMax; x++) {
            toReturn.add(new Location(world, x, yMin, zMin));
            toReturn.add(new Location(world, x, yMin, zMax));
            toReturn.add(new Location(world, x, yMax, zMin));
            toReturn.add(new Location(world, x, yMax, zMax));

        }

        for (double y = yMin; y <= yMax; y++) {
            toReturn.add(new Location(world, xMin, y, zMin));
            toReturn.add(new Location(world, xMin, y, zMax));
            toReturn.add(new Location(world, xMax, y, zMin));
            toReturn.add(new Location(world, xMax, y, zMax));

        }

        for (double z = zMin; z <= zMax; z++) {
            toReturn.add(new Location(world, xMin, yMin, z));
            toReturn.add(new Location(world, xMin, yMax, z));
            toReturn.add(new Location(world, xMax, yMin, z));
            toReturn.add(new Location(world, xMax, yMax, z));
        }
        return toReturn;
    }

    public static Location getCenter(@NonNull final Position min, @NonNull final Position max, @NonNull final World world) {
        return getCenter(min.getX(), max.getX(), min.getY(), max.getY(), min.getZ(), max.getZ(), world);
    }


    public static Location getCenter(final double xMin, final double xMax, final double yMin, final double yMax, final double zMin, final double zMax, @NonNull final World world) {
        return new Location(
                world,
                (xMax - xMin) / 2 + xMin,
                (yMax - yMin) / 2 + yMin,
                (zMax - zMin) / 2 + zMin
        );
    }

    public static List<Location> getAllFaces(@NonNull final Position min, @NonNull final Position max, @NonNull final World world) {
        return getAllFaces(min.getX(), max.getX(), min.getY(), max.getY(), min.getZ(), max.getZ(), world);

    }


    public static List<Location> getAllFaces(final double xMin, final double xMax, final double yMin, final double yMax, final double zMin, final double zMax, @NonNull final World world) {
        List<Location> toReturn = new ArrayList<>();
        for (double x = xMin; x <= xMax; x++) {
            for (double y = yMin; y <= yMax; y++) {
                toReturn.add(new Location(world, x, y, zMin));
                toReturn.add(new Location(world, x, y, zMax));
            }
        }
        for (double x = xMin; x <= xMax; x++) {
            for (double z = zMin; z <= zMax; z++) {
                toReturn.add(new Location(world, x, yMin, z));
                toReturn.add(new Location(world, x, yMax, z));
            }
        }

        for (double z = zMin; z <= zMax; z++) {
            for (double y = yMin; y <= yMax; y++) {
                toReturn.add(new Location(world, xMin, y, z));
                toReturn.add(new Location(world, xMax, y, z));
            }
        }

        return toReturn;
    }
}
