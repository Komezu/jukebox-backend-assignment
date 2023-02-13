package com.jennyqi.jukebox.response;

import java.util.List;

public record PaginatedResponse<T>(List<T> data, int totalCount, int currentPage, boolean pageNumberEstimated, int pageSize) {}

// pageNumberEstimated indicates whether currentPage is a best estimate (true) or exact value (false)
// currentPage will be estimated if count of results skipped (offset) cannot be divided evenly by pageSize (limit)
