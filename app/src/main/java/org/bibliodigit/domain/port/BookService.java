package org.bibliodigit.domain.port;

import org.bibliodigit.domain.Book;
import java.util.List;
import java.util.Optional;


public interface BookService {
    
    // ========== BASIC CRUD OPERATIONS ==========

    List<Book> findAll();
    
    Optional<Book> findById(Long id);
    
    Book save(Book book);
    
    Book create(String title, Integer year, Long authorId, Long categoryId);
    
    Book update(Long id, Book book);
    
    void deleteById(Long id);

    // ========== RELATIONSHIP SEARCHES ==========
    
    List<Book> findByAuthorId(Long authorId);
    
    List<Book> findByCategoryId(Long categoryId);
    
    List<Book> findByAuthorName(String authorName);
    
    // ========== ADDITIONAL SEARCHES ==========
    
    List<Book> findByTitleContaining(String titleKeyword);
    
    List<Book> findByYear(Integer year);
    
    List<Book> findByYearBetween(Integer startYear, Integer endYear);
    
    List<Book> findByCategoryName(String categoryName);
    
    List<Book> findBooksWithoutAuthor();
    
    List<Book> findBooksWithoutCategory();
    
    // ========== COUNTING OPERATIONS ==========
    
    Long countBooksByAuthor(Long authorId);
    
    Long countBooksByCategory(Long categoryId);
    
    boolean existsByTitle(String title);
}
