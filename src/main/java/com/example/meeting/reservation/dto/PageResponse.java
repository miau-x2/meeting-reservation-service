package com.example.meeting.reservation.dto;

import java.util.List;

public record PageResponse<T>(
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<T> content) {

    public static <T> PageResponse<T> from(
            int page,
            int size,
            long totalElements,
            List<T> content) {

        int totalPages = size <= 0 ? 0 : (int) ((totalElements + size - 1) / size);
        int currentPage = Math.max(page + 1, 1);

        return new PageResponse<>(currentPage, size, totalElements, totalPages, content);
    }
}
