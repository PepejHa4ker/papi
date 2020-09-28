package com.pepej.papi.fuction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Utility methods for parsing {@link Number}s, {@link Integer}s, {@link Long}s,
 * {@link Float}s and {@link Double}s from {@link String}s.
 */
public final class Numbers {

    // number

    @Nullable
    public static Number parseNullable(@Nonnull String s) {
        Objects.requireNonNull(s);
        try {
            return NumberFormat.getInstance().parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    @Nonnull
    public static Optional<Number> parse(@Nonnull String s) {
        return Optional.ofNullable(parseNullable(s));
    }

    // integer

    @Nullable
    public static Integer parseIntegerNullable(@Nonnull String s) {
        Objects.requireNonNull(s);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nonnull
    public static Optional<Integer> parseIntegerOpt(@Nonnull String s) {
        return Optional.ofNullable(parseIntegerNullable(s));
    }

    @Nonnull
    public static OptionalInt parseInteger(@Nonnull String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    // long

    @Nullable
    public static Long parseLongNullable(@Nonnull String s) {
        Objects.requireNonNull(s);
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nonnull
    public static Optional<Long> parseLongOpt(@Nonnull String s) {
        return Optional.ofNullable(parseLongNullable(s));
    }

    @Nonnull
    public static OptionalLong parseLong(@Nonnull String s) {
        try {
            return OptionalLong.of(Long.parseLong(s));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }

    // float

    @Nullable
    public static Float parseFloatNullable(@Nonnull String s) {
        Objects.requireNonNull(s);
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nonnull
    public static Optional<Float> parseFloatOpt(@Nonnull String s) {
        return Optional.ofNullable(parseFloatNullable(s));
    }

    @Nonnull
    public static OptionalDouble parseFloat(@Nonnull String s) {
        try {
            return OptionalDouble.of(Float.parseFloat(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    // double

    @Nullable
    public static Double parseDoubleNullable(@Nonnull String s) {
        Objects.requireNonNull(s);
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nonnull
    public static Optional<Double> parseDoubleOpt(@Nonnull String s) {
        return Optional.ofNullable(parseDoubleNullable(s));
    }

    @Nonnull
    public static OptionalDouble parseDouble(@Nonnull String s) {
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    // byte

    @Nullable
    public static Byte parseByteNullable(@Nonnull String s) {
        Objects.requireNonNull(s);
        try {
            return Byte.parseByte(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nonnull
    public static Optional<Byte> parseByteOpt(@Nonnull String s) {
        return Optional.ofNullable(parseByteNullable(s));
    }

    private Numbers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
