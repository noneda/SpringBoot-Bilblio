package org.bibliodigit.domain.port;

import org.bibliodigit.domain.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    
    // ========== BASIC CRUD OPERATIONS ==========

    List<Category> findAll();
    
    Optional<Category> findById(Long id);
    
    Category save(Category category);
    
    Category create(String name, String description);
    
    Category update(Long id, Category category);
    
    void deleteById(Long id);


    // ========== ADDITIONAL SEARCHES ==========

    Optional<Category> findByName(String name);

    List<Category> findByCategoriesIsNotEmpty();
    
    // ========== COUNTING OPERATIONS ==========
    
    boolean existsByName(String name);


}
