package com.pepej.papi.bucket.partitioning.factory;

import com.pepej.papi.bucket.AbstractBucket;
import com.pepej.papi.bucket.partitioning.PartitioningStrategy;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ApiStatus.Internal
class SynchronizedHashSetBucket<E> extends AbstractBucket<E> {
    SynchronizedHashSetBucket(int size, PartitioningStrategy<E> partitioningStrategy) {
        super(size, partitioningStrategy);
    }

    @Override
    protected Set<E> createSet() {
        return Collections.synchronizedSet(new HashSet<>());
    }
}