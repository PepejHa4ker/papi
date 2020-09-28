package com.pepej.papi.hologram.individual;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
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
    static HologramLine.Builder builder() {
        return new Builder();
    }

    /**
     * Returns a hologram line that doesn't change between players.
     *
     * @param text the text to display
     * @return the line
     */
    @Nonnull
    static HologramLine fixed(@Nonnull String text) {
        return viewer -> text;
    }

    @Nonnull
    static HologramLine fromFunction(@Nonnull Function<Player, String> function) {
        return function::apply;
    }

    /**
     * Gets the string representation of the line, for the given player.
     *
     * @param viewer the player
     * @return the line
     */
    @Nonnull
    String resolve(Player viewer);

    final class Builder {
        private final ImmutableList.Builder<HologramLine> lines = ImmutableList.builder();

        private Builder() {

        }

        public Builder line(HologramLine line) {
            this.lines.add(line);
            return this;
        }

        public Builder lines(Iterable<? extends HologramLine> lines) {
            this.lines.addAll(lines);
            return this;
        }

        public Builder line(String line) {
            return line(HologramLine.fixed(line));
        }

        public Builder fromFunction(@Nonnull Function<Player, String> function) {
            return line(HologramLine.fromFunction(function));
        }

        public List<HologramLine> build() {
            return this.lines.build();
        }
    }

}
