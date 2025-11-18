package org.bibliodigit.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

import org.bibliodigit.api.dto.req.BookRequest;
import org.bibliodigit.api.dto.res.BookResponse;
import org.bibliodigit.api.mapper.BookMapper;
import org.bibliodigit.domain.Book;
import org.bibliodigit.domain.port.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookMapper mapper;

    // ========== BASIC CRUD OPERATIONS ==========

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        log.debug("Creating book: {}", request.getTitle());
        
        Book created = bookService.create(
            request.getTitle(),
            request.getYear(),
            request.getAuthorId(),
            request.getCategoryId()
        );
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        log.debug("Getting all books");
        
        List<BookResponse> responses = bookService.findAll()
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        log.debug("Getting book by id: {}", id);
        
        return bookService.findById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id, 
            @RequestBody BookRequest request) {
        
        log.debug("Updating book with id: {}", id);
        
        try {
            Book bookToUpdate = mapper.toDomain(request);
            Book updated = bookService.update(id, bookToUpdate);
            
            return ResponseEntity.ok(mapper.toResponse(updated));
        } catch (RuntimeException e) {
            log.error("Error updating book: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.debug("Deleting book with id: {}", id);
        
        try {
            bookService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting book: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ADVANCED SEARCHES ==========

    @GetMapping("/search/title")
    public ResponseEntity<List<BookResponse>> searchByTitle(
            @RequestParam String keyword) {
        
        log.debug("Searching books by title: {}", keyword);
        
        List<BookResponse> responses = bookService.findByTitleContaining(keyword)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<BookResponse>> getBooksByAuthorId(
            @PathVariable Long authorId) {
        
        log.debug("Getting books by authorId: {}", authorId);
        
        List<BookResponse> responses = bookService.findByAuthorId(authorId)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<BookResponse>> searchByAuthorName(
            @RequestParam String name) {
        
        log.debug("Searching books by author name: {}", name);
        
        List<BookResponse> responses = bookService.findByAuthorName(name)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BookResponse>> getBooksByCategoryId(
            @PathVariable Long categoryId) {
        
        log.debug("Getting books by categoryId: {}", categoryId);
        
        List<BookResponse> responses = bookService.findByCategoryId(categoryId)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/search/category")
    public ResponseEntity<List<BookResponse>> searchByCategoryName(
            @RequestParam String name) {
        
        log.debug("Searching books by category name: {}", name);
        
        List<BookResponse> responses = bookService.findByCategoryName(name)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }


    @GetMapping("/year/{year}")
    public ResponseEntity<List<BookResponse>> getBooksByYear(
            @PathVariable Integer year) {
        
        log.debug("Getting books by year: {}", year);
        
        List<BookResponse> responses = bookService.findByYear(year)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/year-range")
    public ResponseEntity<List<BookResponse>> getBooksByYearRange(
            @RequestParam Integer start,
            @RequestParam Integer end) {
        
        log.debug("Getting books between {} and {}", start, end);
        
        List<BookResponse> responses = bookService.findByYearBetween(start, end)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    // ========== STATISTICS ==========

    @GetMapping("/count/author/{authorId}")
    public ResponseEntity<Long> countBooksByAuthor(@PathVariable Long authorId) {
        log.debug("Counting books by authorId: {}", authorId);
        Long count = bookService.countBooksByAuthor(authorId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/category/{categoryId}")
    public ResponseEntity<Long> countBooksByCategory(@PathVariable Long categoryId) {
        log.debug("Counting books by categoryId: {}", categoryId);
        Long count = bookService.countBooksByCategory(categoryId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByTitle(@RequestParam String title) {
        log.debug("Checking if book exists with title: {}", title);
        boolean exists = bookService.existsByTitle(title);
        return ResponseEntity.ok(exists);
    }
}
