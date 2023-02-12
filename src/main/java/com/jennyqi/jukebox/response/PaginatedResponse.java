package com.jennyqi.jukebox.response;

import java.util.List;

public record PaginatedResponse<T>(List<T> data, int totalCount, int currentPage, boolean pageNumberEstimated, int pageSize) {}
