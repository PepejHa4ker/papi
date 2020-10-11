package com.pepej.papi.time;


import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Formats durations to a readable form
 */
public enum DurationFormatter {
    LONG,
    CONCISE {
        @Override
        protected String formatUnitPlural(ChronoUnit unit) {
            return String.valueOf(Character.toLowerCase(unit.name().charAt(0)));
        }

        @Override
        protected String formatUnitSingular(ChronoUnit unit) {
            return formatUnitPlural(unit);
        }
    },
    CONCISE_LOW_ACCURACY(3) {
        @Override
        protected String formatUnitPlural(ChronoUnit unit) {
            return String.valueOf(Character.toLowerCase(unit.name().charAt(0)));
        }

        @Override
        protected String formatUnitSingular(ChronoUnit unit) {
            return formatUnitPlural(unit);
        }
    };

    private final Unit[] units = new Unit[]{
            new Unit(ChronoUnit.YEARS),
            new Unit(ChronoUnit.MONTHS),
            new Unit(ChronoUnit.WEEKS),
            new Unit(ChronoUnit.DAYS),
            new Unit(ChronoUnit.HOURS),
            new Unit(ChronoUnit.MINUTES),
            new Unit(ChronoUnit.SECONDS)
    };

    private final int accuracy;

    DurationFormatter() {
        this(Integer.MAX_VALUE);
    }

    DurationFormatter(int accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * Formats {@code duration} as a string.
     *
     * @param duration the duration
     * @return the formatted string
     */
    public String format(Duration duration) {
        long seconds = duration.getSeconds();
        StringBuilder output = new StringBuilder();
        int outputSize = 0;

        for (Unit unit : this.units) {
            long n = seconds / unit.duration;
            if (n > 0) {
                seconds -= unit.duration * n;
                output.append(' ').append(n).append(unit.toString(n));
                outputSize++;
            }
            if (seconds <= 0 || outputSize >= this.accuracy) {
                break;
            }
        }

        if (output.length() == 0) {
            return "0" + this.units[this.units.length - 1].stringPlural;
        }
        return output.substring(1);
    }

    protected String formatUnitPlural(ChronoUnit unit) {
        return " " + unit.name().toLowerCase();
    }

    protected String formatUnitSingular(ChronoUnit unit) {
        String s = unit.name().toLowerCase();
        return " " + s.substring(0, s.length() - 1);
    }

    private final class Unit {
        private final long duration;
        private final String stringPlural;
        private final String stringSingular;

        Unit(ChronoUnit unit) {
            this.duration = unit.getDuration().getSeconds();
            this.stringPlural = formatUnitPlural(unit);
            this.stringSingular = formatUnitSingular(unit);
        }

        public String toString(long n) {
            return n == 1 ? this.stringSingular : this.stringPlural;
        }
    }

}
