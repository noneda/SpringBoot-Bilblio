package org.bibliodigit.repository;

import org.bibliodigit.domain.Author;
import org.bibliodigit.domain.Book;
import org.bibliodigit.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    
    List<Book> findByTitleContaining(String titleKeyword);
    
    List<Book> findByYear(Integer year);
    
    List<Book> findByYearBetween(Integer startYear, Integer endYear);
        
    List<Book> findByAuthorId(Long authorId);
    
    List<Book> findByCategoryId(Long categoryId);
    
    
    List<Book> findByAuthor(Author author);
    
    List<Book> findByCategory(Category category);
    
    // ========== OPTIMIZATIONS ==========
    
    @EntityGraph(attributePaths = {"author", "category"})
    @Override
    List<Book> findAll();
    
    @EntityGraph(attributePaths = {"author", "category"})
    Optional<Book> findById(Long id);
    
    @Query("SELECT b FROM Book b " +
           "JOIN FETCH b.author " +
           "JOIN FETCH b.category")
    List<Book> findAllWithRelations();
    

    // ========== ADVANCED SEARCHES ==========

    @Query("SELECT b FROM Book b " +
           "JOIN FETCH b.author a " +
           "JOIN FETCH b.category " +
           "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContaining(@Param("authorName") String authorName);
    
    @Query("SELECT b FROM Book b " +
           "JOIN FETCH b.author " +
           "JOIN FETCH b.category c " +
           "WHERE c.name = :categoryName")
    List<Book> findByCategoryName(@Param("categoryName") String categoryName);
    
    List<Book> findByAuthorIsNull();
    
    List<Book> findByCategoryIsNull();
    
    Long countByAuthorId(Long authorId);
    
    Long countByCategoryId(Long categoryId);
    
    boolean existsByTitle(String title);
}
