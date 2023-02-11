package com.jennyqi.jukebox.models;

import java.util.List;

public record PaginatedResponse<T>(List<T> data,
                                   int totalCount,
                                   int currentPage,
                                   boolean pageNumberEstimated,
                                   int pageSize) {}
