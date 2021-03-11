package com.pepej.papi.config;

import com.pepej.papi.config.validate.Range;
import com.pepej.papi.config.validate.Time;
import com.pepej.papi.time.DurationParser;
import org.spongepowered.configurate.objectmapping.meta.Constraint;
import org.spongepowered.configurate.serialize.SerializationException;

public class Constraints {

    static Constraint.Factory<Time, String> time() {
        return (data, type) -> value -> {
//            if (value != null) {
//                if (DurationParser.PATTERN.matcher(value).matches()) {
//                    return;
//                }
//            }
            throw new SerializationException("Invalid time value");

        };
    }

    static Constraint.Factory<Range, Integer> range() {
        return (data, type) -> value -> {
//            if (value != null) {
//                long min = Long.parseLong(data.min());
//                long max = Long.parseLong(data.max());
//                if (value.longValue() >= min || value.longValue() <= max) {
//                    return;
//                }
//
//            }
            throw new SerializationException("Value out of range");

        };
    }
}
