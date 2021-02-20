package com.pepej.papi.bucket.partitioning;

import com.pepej.papi.bucket.Bucket;

import java.util.Collection;
import java.util.Set;

/**
 * Represents a partition of elements within a {@link Bucket}.
 *
 * @param <E> the element type
 */
public interface BucketPartition<E> extends Set<E> {

    /**
     * Gets the index of this partition within the bucket
     *
     * @return the index
     */
    int getPartitionIndex();

    /**
     * {@inheritDoc}
     * @deprecated as partitions do not support this method.
     */
    @Override
    @Deprecated
    default boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @deprecated as partitions do not support this method.
     */
    @Override
    @Deprecated
    default boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

}

