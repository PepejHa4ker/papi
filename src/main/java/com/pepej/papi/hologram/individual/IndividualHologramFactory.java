package com.pepej.papi.hologram.individual;

import com.pepej.papi.serialize.Position;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A object which can create {@link IndividualHologram}s.
 */
public interface IndividualHologramFactory {

    /**
     * Creates a new hologram.
     *
     * @param position the position of the hologram
     * @param lines the lines to display
     * @return the new hologram
     */
    @Nonnull
    IndividualHologram newHologram(@Nonnull Position position, @Nonnull List<HologramLine> lines);

}
