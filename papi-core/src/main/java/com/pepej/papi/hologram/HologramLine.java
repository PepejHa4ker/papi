package com.pepej.papi.hologram;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.function.Function;

/**
 * Represents a line in a hologram.
 */
public interface HologramLine {

    /**
     * Returns a new {@link HologramLine.Builder}.
     *
     * @return a new builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a hologram line that doesn't change between players.
     *
     * @param text the text to display
     * @return the line
     */
    @NonNull
    static HologramLine fixed(@NonNull String text) {
        return $ -> text;
    }

    @NonNull
    static HologramLine fromFunction(@NonNull Function<Player, String> function) {
        return function::apply;
    }

    /**
     * Gets the string representation of the line, for the given player.
     *
     * @param viewer the player
     * @return the line
     */
    @NonNull
    String resolve(final @NonNull Player viewer);

    final class Builder {
        private final ImmutableList.Builder<HologramLine> lines = ImmutableList.builder();

        private Builder() {

        }

        public Builder line(final @NonNull HologramLine line) {
            this.lines.add(line);
            return this;
        }

        public Builder emptyLine() {
            this.lines.add(HologramLine.fixed("&1"));
            return this;
        }

        public Builder lines(final @NonNull Iterable<? extends HologramLine> lines) {
            this.lines.addAll(lines);
            return this;
        }

        public Builder line(final @NonNull String line) {
            return line(HologramLine.fixed(line));
        }

        public Builder fromFunction(@NonNull Function<Player, String> function) {
            return line(HologramLine.fromFunction(function));
        }

        public List<HologramLine> build() {
            return this.lines.build();
        }
    }

}
