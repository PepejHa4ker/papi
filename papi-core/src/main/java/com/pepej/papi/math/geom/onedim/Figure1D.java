package com.pepej.papi.math.geom.onedim;

import com.pepej.papi.math.geom.FigureND;

public interface Figure1D extends FigureND {

    @Override
    default int getDimension() {
        return 1;
    }
}
