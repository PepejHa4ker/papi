package com.pepej.papi.menu.paginated;


import lombok.Value;

/**
 * Represents data about a currently open page in a {@link PaginatedGui}.
 */
@Value(staticConstructor = "create")
public class PageInfo {
    int current;
    int size;

}
