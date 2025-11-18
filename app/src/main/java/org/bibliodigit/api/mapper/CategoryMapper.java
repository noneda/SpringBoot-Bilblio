package org.bibliodigit.api.mapper;

import org.bibliodigit.api.dto.req.CategoryRequest;
import org.bibliodigit.api.dto.res.CategoryResponse;
import org.bibliodigit.domain.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public Category toDomain(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}
