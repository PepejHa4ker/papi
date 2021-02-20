package com.pepej.papi.math.geom.twodim;

import com.pepej.papi.math.geom.FigureND;

public interface Figure2D extends FigureND {

    @Override
    default int getDimension() {
        return 2;
    }

    double getSquare();

    double getPerimeter();
}
