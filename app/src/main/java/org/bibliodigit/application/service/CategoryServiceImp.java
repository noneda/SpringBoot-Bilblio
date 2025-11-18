package org.bibliodigit.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.domain.Category;
import org.bibliodigit.domain.port.CategoryService;
import org.bibliodigit.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        log.debug("Finding all categories");
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        log.debug("Finding category by ID: {}", id);
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional
    public Category save(Category category) {
        log.debug("Saving category: {}", category.getName());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category create(String name, String description) {
        log.debug("Creating category: {}", name);

        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category with name '" + name + "' already exists");
        }

        Category category = Category.builder()
                .name(name)
                .description(description)
                .build();

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(Long id, Category newData) {
        log.debug("Updating category with ID: {}", id);

        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(newData.getName());
                    existing.setDescription(newData.getDescription());
                    return categoryRepository.save(existing);
                })
                .orElseThrow(() ->
                        new RuntimeException("Category not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting category with ID: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }

        categoryRepository.deleteById(id);
    }

    // ========== ADDITIONAL SEARCHES ==========

    @Override
    public Optional<Category> findByName(String name) {
        log.debug("Finding category by name: {}", name);
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> findByCategoriesIsNotEmpty() {
        log.debug("Finding categories with books");
        return categoryRepository.findByCategoriesIsNotEmpty();
    }

    // ========== COUNTING OPERATIONS ==========

    @Override
    public boolean existsByName(String name) {
        log.debug("Checking if category exists with name: {}", name);
        return categoryRepository.existsByName(name);
    }
}
