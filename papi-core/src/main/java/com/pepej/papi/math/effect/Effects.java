package com.pepej.papi.math.effect;

import com.google.common.collect.ImmutableList;
import com.pepej.papi.math.vector.Vector3d;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.scheduler.Task;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pepej.papi.math.TrigonometricMath.*;


public final class Effects {

    private static ImmutableList<Double> getCirclePoint(double circles) {
        final ImmutableList.Builder<Double> points = ImmutableList.builder();
        for (double i = 0.0; i < TWO_PI; i += circles) {
            points.add(i);
        }
        return points.build();
    }

    public static void spawnColoredRedstoneParticle(Vector3d point, World world, Color color) {
        double r = Math.max(1.0E-10, (color.getRed() / 255.0));
        double g = color.getBlue() / 255.0;
        double b = color.getGreen() / 255.0;
        world.spawnParticle(Particle.REDSTONE, new Location(world, point.getX(), point.getY(), point.getZ()), 0, r, g, b, 1.0);

    }

    public static ImmutableList<Vector3d> getWingsEffect(Vector3d base, double yaw, double pitch) {
        final ImmutableList.Builder<Vector3d> points = ImmutableList.builder();
        getCirclePoint(TWO_PI / 92).forEach(circlePoint -> {
            double offset = (Math.pow(E, cos(circlePoint)) - 2 * cos(circlePoint * 4)) - Math.pow(sin(circlePoint / 12), 5) / 2;
            double dx = sin(circlePoint) * offset;
            double dy = cos(circlePoint) * offset;
            Vector3d newPoint = new Vector3d(dx, dy, -0.3).rotateDeg(pitch, -yaw, 0);
            points.add(base.add(newPoint));
        });
        return points.build();
    }

    public static Task spawnQuadHelixSyncTask(Player player, int maxStepX, int maxStepY, int orbs, Color color) {

        AtomicInteger stepX = new AtomicInteger();
        AtomicInteger stepY = new AtomicInteger();
        AtomicBoolean reverse = new AtomicBoolean(true);
        return Schedulers
                .sync()
                .runRepeating(() -> {
                    stepX.getAndIncrement();
                    if (stepX.get() > maxStepX) {
                        stepX.set(0);
                    }
                    if (reverse.get()) {
                        stepY.getAndIncrement();
                        if (stepY.get() > maxStepY) {
                            reverse.set(false);
                        }
                    }
                    else {
                        stepY.getAndDecrement();
                        if (stepY.get() < -maxStepY) {
                            reverse.set(true);
                        }
                    }

                    for (int i = 0; i < orbs; i++) {
                        double offset = (stepX.get() / (double) maxStepX) * TWO_PI + ((TWO_PI / orbs) * i);
                        double step = (maxStepY - Math.abs(stepY.get())) / (double) maxStepY;
                        double dx = -cos(offset) * step;
                        double dy = (stepY.get() / (double) maxStepY) * 1.5;
                        double dz = -sin(offset) * step;
                    }
                }, 0, 1);

    }


    }
