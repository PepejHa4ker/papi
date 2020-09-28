package com.pepej.papi.utils;

import java.util.UUID;

/**
 * Utilities for converting {@link UUID} string representations without dashes.
 */
public final class UndashedUuids {

    /**
     * Returns a {@link UUID#toString()} string without dashes.
     *
     * @param uuid the uuid
     * @return the string form
     */
    public static String toString(UUID uuid) {
        // copied from UUID impl
        return (digits(uuid.getMostSignificantBits() >> 32, 8) +
                digits(uuid.getMostSignificantBits() >> 16, 4) +
                digits(uuid.getMostSignificantBits(), 4) +
                digits(uuid.getLeastSignificantBits() >> 48, 4) +
                digits(uuid.getLeastSignificantBits(), 12));
    }

    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }


    /**
     * Parses a UUID from an undashed string.
     *
     * @param string the string
     * @return the uuid
     */
    public static UUID fromString(String string) throws IllegalArgumentException {
        if (string.length() != 32) {
            throw new IllegalArgumentException("Invalid length " + string.length() + ": " + string);
        }

        try {
            return new UUID(
                    Long.parseUnsignedLong(string.substring(0, 16), 16),
                    Long.parseUnsignedLong(string.substring(16), 16)
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid uuid string: " + string, e);
        }

    }

    private UndashedUuids() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
