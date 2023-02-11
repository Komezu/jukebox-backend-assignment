package com.jennyqi.jukebox.models;

public record DataResponse<T>(T data, ErrorResponse error) {}
