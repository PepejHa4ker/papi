package com.pepej.papi.network.metadata;

import com.pepej.papi.utils.Tps;

import java.util.Collections;

public final class TpsMetadataProvider implements ServerMetadataProvider {
    public static final ServerMetadataProvider INSTANCE = new TpsMetadataProvider();

    private TpsMetadataProvider() {

    }

    @Override
    public Iterable<ServerMetadata> provide() {
        if (!Tps.isReadSupported()) {
            return Collections.emptyList();
        }

        Tps tps = Tps.read();
        return Collections.singleton(ServerMetadata.of("tps", tps, Tps.class));
    }
}
