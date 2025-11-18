package org.bibliodigit.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.domain.Author;
import org.bibliodigit.domain.Book;
import org.bibliodigit.domain.Category;
import org.bibliodigit.domain.port.BookService;
import org.bibliodigit.repository.AuthorRepository;
import org.bibliodigit.repository.BookRepository;
import org.bibliodigit.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j  
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    
    // ========== BASIC CRUD OPERATIONS ==========
    
    @Override
    public List<Book> findAll() {
        log.debug("Finding all books with relations");
        return bookRepository.findAll();  
    }
    
    @Override
    public Optional<Book> findById(Long id) {
        log.debug("Finding book by id: {}", id);
        return bookRepository.findById(id); 
    }
    
    @Override
    @Transactional  
    public Book save(Book book) {
        log.debug("Saving book: {}", book.getTitle());
        return bookRepository.save(book);
    }
    
    @Override
    @Transactional
    public Book create(String title, Integer year, Long authorId, Long categoryId) {
        log.debug("Creating book: {} by authorId: {} categoryId: {}", title, authorId, categoryId);
        
        if (bookRepository.existsByTitle(title)) {
            throw new RuntimeException("Book with title '" + title + "' already exists");
        }
        
        Author author = authorRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        
        Book book = Book.builder()
            .title(title)
            .year(year)
            .author(author)
            .category(category)
            .build();
        
        return bookRepository.save(book);
    }
    
    @Override
    @Transactional
    public Book update(Long id, Book book) {
        log.debug("Updating book with id: {}", id);
        
        return bookRepository.findById(id)
            .map(existingBook -> {
                existingBook.setTitle(book.getTitle());
                existingBook.setYear(book.getYear());
                
                if (book.getAuthor() != null && 
                    !book.getAuthor().getId().equals(existingBook.getAuthor().getId())) {
                    
                    Author newAuthor = authorRepository.findById(book.getAuthor().getId())
                        .orElseThrow(() -> new RuntimeException("Author not found"));
                    existingBook.setAuthor(newAuthor);
                }
                
                if (book.getCategory() != null && 
                    !book.getCategory().getId().equals(existingBook.getCategory().getId())) {
                    
                    Category newCategory = categoryRepository.findById(book.getCategory().getId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                    existingBook.setCategory(newCategory);
                }
                
                return bookRepository.save(existingBook);
            })
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }
    
    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting book with id: {}", id);
        
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        
        bookRepository.deleteById(id);
    }
    
    // ========== RELATIONSHIP SEARCHES ==========
    
    @Override
    public List<Book> findByAuthorId(Long authorId) {
        log.debug("Finding books by authorId: {}", authorId);
        return bookRepository.findByAuthorId(authorId);
    }
    
    @Override
    public List<Book> findByCategoryId(Long categoryId) {
        log.debug("Finding books by categoryId: {}", categoryId);
        return bookRepository.findByCategoryId(categoryId);
    }
    
    @Override
    public List<Book> findByAuthorName(String authorName) {
        log.debug("Finding books by author name: {}", authorName);
        return bookRepository.findByAuthorNameContaining(authorName);
    }
    
    // ========== ADDITIONAL SEARCHES ==========
    
    @Override
    public List<Book> findByTitleContaining(String titleKeyword) {
        log.debug("Finding books by title containing: {}", titleKeyword);
        return bookRepository.findByTitleContaining(titleKeyword);
    }
    
    @Override
    public List<Book> findByYear(Integer year) {
        log.debug("Finding books by year: {}", year);
        return bookRepository.findByYear(year);
    }
    
    @Override
    public List<Book> findByYearBetween(Integer startYear, Integer endYear) {
        log.debug("Finding books between {} and {}", startYear, endYear);
        return bookRepository.findByYearBetween(startYear, endYear);
    }
    
    @Override
    public List<Book> findByCategoryName(String categoryName) {
        log.debug("Finding books by category name: {}", categoryName);
        return bookRepository.findByCategoryName(categoryName);
    }
    
    @Override
    public List<Book> findBooksWithoutAuthor() {
        log.debug("Finding books without author");
        return bookRepository.findByAuthorIsNull();
    }
    
    @Override
    public List<Book> findBooksWithoutCategory() {
        log.debug("Finding books without category");
        return bookRepository.findByCategoryIsNull();
    }
    
    // ========== COUNTING OPERATIONS ==========
    
    @Override
    public Long countBooksByAuthor(Long authorId) {
        log.debug("Counting books by authorId: {}", authorId);
        return bookRepository.countByAuthorId(authorId);
    }
    
    @Override
    public Long countBooksByCategory(Long categoryId) {
        log.debug("Counting books by categoryId: {}", categoryId);
        return bookRepository.countByCategoryId(categoryId);
    }
    
    @Override
    public boolean existsByTitle(String title) {
        log.debug("Checking if book exists with title: {}", title);
        return bookRepository.existsByTitle(title);
    }
}
