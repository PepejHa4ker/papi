package com.pepej.papi.time;


import com.google.common.collect.ImmutableMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.intellij.lang.annotations.RegExp;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses durations from a string format
 */
public final class DurationParser {
    private DurationParser() {}

    private static final Map<ChronoUnit, String> UNITS_PATTERNS = ImmutableMap.<ChronoUnit, String>builder()
            .put(ChronoUnit.YEARS, "y(?:ear)?s?")
            .put(ChronoUnit.MONTHS, "mo(?:nth)?s?")
            .put(ChronoUnit.WEEKS, "w(?:eek)?s?")
            .put(ChronoUnit.DAYS, "d(?:ay)?s?")
            .put(ChronoUnit.HOURS, "h(?:our|r)?s?")
            .put(ChronoUnit.MINUTES, "m(?:inute|in)?s?")
            .put(ChronoUnit.SECONDS, "(?:s(?:econd|ec)?s?)?")
            .build();

    private static final ChronoUnit[] UNITS = UNITS_PATTERNS.keySet().toArray(new ChronoUnit[0]);

    @RegExp
    private static final String PATTERN_STRING = UNITS_PATTERNS.values().stream()
                                                               .map(pattern -> "(?:(\\d+)\\s*" + pattern + "[,\\s]*)?")
                                                               .collect(Collectors.joining());

    public static final Pattern PATTERN = Pattern.compile(PATTERN_STRING, Pattern.CASE_INSENSITIVE);

    /**
     * Parses a {@link Duration} from a string.
     *
     * @param input the input string
     * @return the parsed duration
     * @throws IllegalArgumentException if parsing fails
     */
    public static Duration parse(String input) throws IllegalArgumentException {
        Matcher matcher = PATTERN.matcher(input);

        while (matcher.find()) {
            if (matcher.group() == null || matcher.group().isEmpty()) {
                continue;
            }

            Duration duration = Duration.ZERO;
            for (int i = 0; i < UNITS.length; i++) {
                ChronoUnit unit = UNITS[i];
                int g = i + 1;

                if (matcher.group(g) != null && !matcher.group(g).isEmpty()) {
                    int n = Integer.parseInt(matcher.group(g));
                    if (n > 0) {
                        duration = duration.plus(unit.getDuration().multipliedBy(n));
                    }
                }
            }

            return duration;
        }

        throw new IllegalArgumentException("unable to parse duration: " + input);
    }

    /**
     * Attempts to parse a {@link Duration} and returns the
     * result as an {@link Optional}-wrapped object.
     *
     * @param input the input string
     * @return an Optional Duration
     */
    @NonNull
    public static Optional<Duration> parseSafely(String input) {
        try {
            return Optional.of(parse(input));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

}
