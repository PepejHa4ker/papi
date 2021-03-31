package com.pepej.papi.bucket.partitioning.factory;

import com.pepej.papi.bucket.AbstractBucket;
import com.pepej.papi.bucket.partitioning.PartitioningStrategy;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;
@ApiStatus.Internal
class HashSetBucket<E> extends AbstractBucket<E> {
    HashSetBucket(int size, PartitioningStrategy<E> partitioningStrategy) {
        super(size, partitioningStrategy);
    }

    @Override
    protected Set<E> createSet() {
        return new HashSet<>();
    }
}
