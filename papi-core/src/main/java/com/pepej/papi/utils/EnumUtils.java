package com.pepej.papi.utils;

public final class EnumUtils {

    public static <E extends Enum<E>> E valueOfOrNull(Class<E> enumClass, String name) {
        return valueOfOrDefault(enumClass, name, null);
    }

    public static <E extends Enum<E>> E valueOfOrDefault(Class<E> enumClass, String name, E defaultValue) {
        if (StringUtils.isEmpty(name)) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    private EnumUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }


}
