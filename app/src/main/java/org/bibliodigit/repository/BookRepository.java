package org.bibliodigit.repository;

import org.bibliodigit.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Persistence Adapter (Driven Adapter)
 * JpaRepository automatically provides CRUD (save, findById, findAll, delete) functionality.
*/
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Spring generates the query: SELECT * FROM Book WHERE title LIKE '%keyword%'
    List<Book> findByTitleContaining(String titleKeyword);
    
    // Spring genera la consulta: SELECT * FROM Book WHERE author_id = :id
    List<Book> findByAuthorId(Long authorId);
    
    // Spring generates the query: SELECT * FROM Book WHERE category_id = :id
    List<Book> findByCategoryId(Long categoryId);
}
