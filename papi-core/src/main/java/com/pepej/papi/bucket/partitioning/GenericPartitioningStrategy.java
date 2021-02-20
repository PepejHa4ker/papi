package com.pepej.papi.bucket.partitioning;

import com.pepej.papi.bucket.Bucket;

/**
 * A {@link PartitioningStrategy} which allocates partitions without reference
 * to the object being added.
 */
@FunctionalInterface
public interface GenericPartitioningStrategy extends PartitioningStrategy<Object> {

    /**
     * Calculates the index of the partition to use for any given object.
     *
     * @param bucket the bucket
     * @return the index
     */
    int allocate(Bucket<?> bucket);

    /**
     * Casts this {@link GenericPartitioningStrategy} to a {@link PartitioningStrategy} of type T.
     *
     * @param <T> the type
     * @return a casted strategy
     */
    default <T> PartitioningStrategy<T> cast() {
        //noinspection unchecked
        return (PartitioningStrategy<T>) this;
    }

    @Override
    @Deprecated
    default int allocate(Object object, Bucket<Object> bucket) {
        return allocate(bucket);
    }

}
