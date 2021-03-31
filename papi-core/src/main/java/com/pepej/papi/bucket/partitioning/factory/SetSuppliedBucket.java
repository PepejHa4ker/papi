package com.pepej.papi.bucket.partitioning.factory;

import com.pepej.papi.bucket.AbstractBucket;
import com.pepej.papi.bucket.partitioning.PartitioningStrategy;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.function.Supplier;

@ApiStatus.Internal
class SetSuppliedBucket<E> extends AbstractBucket<E> {
    private final Supplier<Set<E>> setSupplier;

    SetSuppliedBucket(int size, PartitioningStrategy<E> strategy, Supplier<Set<E>> setSupplier) {
        super(size, strategy);
        this.setSupplier = setSupplier;
    }

    @Override
    protected Set<E> createSet() {
        return this.setSupplier.get();
    }
}
