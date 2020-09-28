package com.pepej.papi.menu.paginated;

import javax.annotation.Nonnull;

/**
 * Represents data about a currently open page in a {@link PaginatedGui}.
 */
public final class PageInfo {

    @Nonnull
    public static PageInfo create(int current, int size) {
        return new PageInfo(current, size);
    }

    private final int current;
    private final int size;

    private PageInfo(int current, int size) {
        this.current = current;
        this.size = size;
    }

    public int getCurrent() {
        return this.current;
    }

    public int getSize() {
        return this.size;
    }

}
