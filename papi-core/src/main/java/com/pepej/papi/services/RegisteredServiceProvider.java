package com.pepej.papi.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * A registered service provider.
 *
 * @param <T> Service
 */

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@AllArgsConstructor
public class RegisteredServiceProvider<T> implements Comparable<RegisteredServiceProvider<?>> {

    Class<T> service;
    T provider;
    ServicePriority priority;

    public int compareTo(RegisteredServiceProvider<?> other) {
        if (priority.ordinal() == other.getPriority().ordinal()) {
            return 0;
        } else {
            return priority.ordinal() < other.getPriority().ordinal() ? 1 : -1;
        }
    }
}
