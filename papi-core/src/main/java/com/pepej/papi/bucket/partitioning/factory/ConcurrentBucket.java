package com.pepej.papi.bucket.partitioning.factory;

import com.pepej.papi.bucket.AbstractBucket;
import com.pepej.papi.bucket.partitioning.PartitioningStrategy;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ConcurrentBucket<E> extends AbstractBucket<E> {
    ConcurrentBucket(int size, PartitioningStrategy<E> strategy) {
        super(size, strategy);
    }

    @Override
    protected Set<E> createSet() {
        return ConcurrentHashMap.newKeySet();
    }
}
