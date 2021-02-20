package com.pepej.papi.math.geom.threedim;

import com.pepej.papi.math.geom.twodim.Figure2D;

public interface Figure3D extends Figure2D {

    @Override
    default int getDimension() {
        return 3;
    }

    double getVolume();

}
