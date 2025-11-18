package org.bibliodigit.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

import org.bibliodigit.api.dto.req.CategoryRequest;
import org.bibliodigit.api.dto.res.CategoryResponse;
import org.bibliodigit.api.mapper.CategoryMapper;
import org.bibliodigit.domain.Category;
import org.bibliodigit.domain.port.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper mapper;

    // ========== BASIC CRUD OPERATIONS ==========

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.debug("Creating category: {}", request.getName());

        Category created = categoryService.create(
                request.getName(),
                request.getDescription()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.debug("Getting all categories");

        List<CategoryResponse> responses = categoryService.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.debug("Getting category by id: {}", id);

        return categoryService.findById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        log.debug("Updating category with id: {}", id);

        try {
            Category updated = categoryService.update(
                    id,
                    mapper.toDomain(request)
            );

            return ResponseEntity.ok(mapper.toResponse(updated));

        } catch (RuntimeException e) {
            log.error("Error updating category: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.debug("Deleting category with id: {}", id);

        try {
            categoryService.deleteById(id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            log.error("Error deleting category: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // ========== SEARCH BY NAME ==========

    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> findByName(@RequestParam String name) {

        log.debug("Searching categories by name: {}", name);

        List<CategoryResponse> results = categoryService.findByName(name)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return results.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(results);
    }
}
