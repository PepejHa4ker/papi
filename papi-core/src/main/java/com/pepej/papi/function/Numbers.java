package com.pepej.papi.function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import static java.lang.System.out;

/**
 * Utility methods for parsing {@link Number}s, {@link Integer}s, {@link Long}s,
 * {@link Float}s and {@link Double}s from {@link String}s.
 */
public final class Numbers {

    // number

    @Nullable
    public static Number parseNullable(@NonNull String s) {
        Objects.requireNonNull(s);
        try {
            return NumberFormat.getInstance().parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    @NonNull
    public static Optional<Number> parse(@NonNull String s) {
        return Optional.ofNullable(parseNullable(s));
    }

    // integer

    @Nullable
    public static Integer parseIntegerNullable(@NonNull String s) {
        Objects.requireNonNull(s);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NonNull
    public static Optional<Integer> parseIntegerOpt(@NonNull String s) {
        return Optional.ofNullable(parseIntegerNullable(s));
    }

    @NonNull
    public static OptionalInt parseInteger(@NonNull String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    // long

    @Nullable
    public static Long parseLongNullable(@NonNull String s) {
        Objects.requireNonNull(s);
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NonNull
    public static Optional<Long> parseLongOpt(@NonNull String s) {
        return Optional.ofNullable(parseLongNullable(s));
    }

    @NonNull
    public static OptionalLong parseLong(@NonNull String s) {
        try {
            return OptionalLong.of(Long.parseLong(s));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }

    // float

    @Nullable
    public static Float parseFloatNullable(@NonNull String s) {
        Objects.requireNonNull(s);
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NonNull
    public static Optional<Float> parseFloatOpt(@NonNull String s) {
        return Optional.ofNullable(parseFloatNullable(s));
    }

    @NonNull
    public static OptionalDouble parseFloat(@NonNull String s) {
        try {
            return OptionalDouble.of(Float.parseFloat(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    // double

    @Nullable
    public static Double parseDoubleNullable(@NonNull String s) {
        Objects.requireNonNull(s);
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NonNull
    public static Optional<Double> parseDoubleOpt(@NonNull String s) {
        return Optional.ofNullable(parseDoubleNullable(s));
    }

    @NonNull
    public static OptionalDouble parseDouble(@NonNull String s) {
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    // byte

    @Nullable
    public static Byte parseByteNullable(@NonNull String s) {
        Objects.requireNonNull(s);
        try {
            return Byte.parseByte(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NonNull
    public static Optional<Byte> parseByteOpt(@NonNull String s) {
        return Optional.ofNullable(parseByteNullable(s));
    }

    private Numbers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
