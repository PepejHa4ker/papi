package com.pepej.papi.shadow.bukkit;

import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * An enumeration of CraftBukkit package versions.
 */
public enum PackageVersion {
    NONE {
        @Override
        protected @NonNull String getPackageComponent() {
            return ".";
        }
    },
    v1_7_R1,
    v1_7_R2,
    v1_7_R3,
    v1_7_R4,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3;

    /**
     * The nms prefix (without the version component)
     */
    public static final String NMS = "net.minecraft.server";

    /**
     * The obc prefix (without the version component)
     */
    public static final String OBC = "org.bukkit.craftbukkit";

    private final @NonNull String nmsPrefix;
    private final @NonNull String obcPrefix;

    PackageVersion() {
        this.nmsPrefix = NMS + getPackageComponent();
        this.obcPrefix = OBC + getPackageComponent();
    }

    protected @NonNull String getPackageComponent() {
        return "." + name() + ".";
    }

    /**
     * Prepends the versioned NMS prefix to the given class name
     *
     * @param className the name of the class
     * @return the full class name
     */
    public @NonNull String nms(@NonNull String className) {
        Objects.requireNonNull(className, "className");
        return this.nmsPrefix + className;
    }

    /**
     * Prepends the versioned NMS prefix to the given class name
     *
     * @param className the name of the class
     * @return the class represented by the full class name
     */
    public @NonNull Class<?> nmsClass(@NonNull String className) throws ClassNotFoundException {
        return Class.forName(nms(className));
    }

    /**
     * Prepends the versioned OBC prefix to the given class name
     *
     * @param className the name of the class
     * @return the full class name
     */
    public @NonNull String obc(@NonNull String className) {
        Objects.requireNonNull(className, "className");
        return this.obcPrefix + className;
    }

    /**
     * Prepends the versioned OBC prefix to the given class name
     *
     * @param className the name of the class
     * @return the class represented by the full class name
     */
    public @NonNull Class<?> obcClass(@NonNull String className) throws ClassNotFoundException {
        return Class.forName(obc(className));
    }

    private void checkComparable(PackageVersion other) {
        Objects.requireNonNull(other, "other");
        if (this == NONE) {
            throw new IllegalArgumentException("this cannot be NONE");
        }
        if (other == NONE) {
            throw new IllegalArgumentException("other cannot be NONE");
        }
    }

    /**
     * Gets if this version comes before the {@code other} version.
     *
     * @param other the other version
     * @return if it comes before
     */
    public boolean isBefore(@NonNull PackageVersion other) {
        checkComparable(other);
        return this.ordinal() < other.ordinal();
    }

    /**
     * Gets if this version comes after the {@code other} version.
     *
     * @param other the other version
     * @return if it comes after
     */
    public boolean isAfter(@NonNull PackageVersion other) {
        checkComparable(other);
        return this.ordinal() > other.ordinal();
    }

    /**
     * Gets if this version is the same as or comes before the {@code other} version.
     *
     * @param other the other version
     * @return if it comes before or is the same
     */
    public boolean isBeforeOrEq(@NonNull PackageVersion other) {
        checkComparable(other);
        return this.ordinal() <= other.ordinal();
    }

    /**
     * Gets if this version is the same as or comes after the {@code other} version.
     *
     * @param other the other version
     * @return if it comes after or is the same
     */
    public boolean isAfterOrEq(@NonNull PackageVersion other) {
        checkComparable(other);
        return this.ordinal() >= other.ordinal();
    }

    private static final String RUNTIME_VERSION_STRING;
    private static final PackageVersion RUNTIME_VERSION;

    static {
        String serverVersion = "";
        // check we're dealing with a "CraftServer" and that the server isn't non-versioned.
        Class<?> server = Bukkit.getServer().getClass();
        if (server.getSimpleName().equals("CraftServer") && !server.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            String obcPackage = server.getPackage().getName();
            // check we're dealing with a craftbukkit implementation.
            if (obcPackage.startsWith("org.bukkit.craftbukkit.")) {
                // return the package version.
                serverVersion = obcPackage.substring("org.bukkit.craftbukkit.".length());
            }
        }
        RUNTIME_VERSION_STRING = serverVersion;

        PackageVersion runtimeVersion = null;
        if (RUNTIME_VERSION_STRING.isEmpty()) {
            runtimeVersion = PackageVersion.NONE;
        } else {
            try {
                runtimeVersion = PackageVersion.valueOf(serverVersion);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        RUNTIME_VERSION = runtimeVersion;
    }

    /**
     * Gets the package version for the current runtime server instance.
     *
     * @return the package version of the current runtime
     */
    public static @NonNull PackageVersion runtimeVersion() {
        if (RUNTIME_VERSION == null) {
            throw new IllegalStateException("Unknown package version: " + RUNTIME_VERSION_STRING);
        }
        return RUNTIME_VERSION;
    }

}
