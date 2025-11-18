package org.bibliodigit.api.dto;

public record BookResponse(
        Long id,
        String title,
        Long authorId,
        Long categoryId
) {}
