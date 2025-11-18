package org.bibliodigit.api.dto;

public record BookRequest(
        String title,
        Long authorId,
        Long categoryId
) {}
