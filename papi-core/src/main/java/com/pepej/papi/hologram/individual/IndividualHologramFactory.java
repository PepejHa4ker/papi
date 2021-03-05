package com.pepej.papi.hologram.individual;

import com.pepej.papi.hologram.HologramLine;
import com.pepej.papi.serialize.Position;
import com.pepej.papi.services.Implementor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * A object which can create {@link IndividualHologram}s.
 */
@Implementor(PacketIndividualHologramFactory.class)
public interface IndividualHologramFactory {

    /**
     * Creates a new hologram.
     *
     * @param position the position of the hologram
     * @param lines the lines to display
     * @return the new hologram
     */
    @NonNull
    IndividualHologram newHologram(@NonNull Position position, @NonNull List<HologramLine> lines);

}
