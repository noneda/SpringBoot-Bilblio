package org.bibliodigit.domain.port;

import org.bibliodigit.domain.Author;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
    
    // ========== BASIC CRUD OPERATIONS ==========

    List<Author> findAll();
    
    Optional<Author> findById(Long id);
    
    Author save(Author author);
    
    Author create(String name, String nationality);
    
    Author update(Long id, Author author);
    
    void deleteById(Long id);


    // ========== ADDITIONAL SEARCHES ==========

    Optional<Author> findByName(String name);
    
    List<Author> findByNationality(String nationality);
    
    List<Author> findByAuthorsIsNotEmpty();

    // ========== COUNTING OPERATIONS ==========

    boolean existsByName(String name);

}
