package com.pepej.papi.bucket;

import com.pepej.papi.bucket.partitioning.BucketPartition;
import com.pepej.papi.bucket.partitioning.PartitioningStrategy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A bucket is an extension of {@link Set}, which allows contained elements
 * to be separated into parts by a {@link PartitioningStrategy}.
 *
 * <p>The performance of {@link Bucket} should be largely similar to the performance
 * of the underlying {@link Set}. Elements are stored twice - once in a set
 * containing all elements in the bucket, and again in a set representing each partition.</p>
 *
 * @param <E> the element type
 */
public interface Bucket<E> extends Set<E> {

    /**
     * Gets the number of partitions used to form this bucket.
     *
     * @return the number of partitions in this bucket
     */
    int getPartitionCount();

    /**
     * Gets the partition with the given index value
     *
     * @param i the partition index
     * @return the partition
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= getPartitionCount()</tt>)
     */
    @NonNull
    BucketPartition<E> getPartition(int i);

    /**
     * Gets the partitions which form this bucket.
     *
     * @return the partitions within the bucket
     */
    @NonNull
    List<BucketPartition<E>> getPartitions();

    /**
     * Returns a cycle instance unique to this bucket.
     *
     * <p>This method is provided as a utility for operating deterministically on
     * all elements within the bucket over a period of time.</p>
     *
     * <p>The same cycle instance is returned for each bucket.</p>
     *
     * @return a cycle of partitions
     */
    @NonNull
    Cycle<BucketPartition<E>> asCycle();

}

