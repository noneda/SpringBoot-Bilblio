package org.bibliodigit.api;

import org.bibliodigit.api.dto.BookRequest;
import org.bibliodigit.api.dto.BookResponse;
import org.bibliodigit.api.mapper.BookMapper;
import org.bibliodigit.domain.Book;
import org.bibliodigit.domain.port.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookMapper mapper;

    public BookController(BookService bookService, BookMapper mapper) {
        this.bookService = bookService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest request) {
        Book saved = bookService.saveOrUpdate(mapper.toDomain(request));
        return new ResponseEntity<>(mapper.toResponse(saved), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<Book> books = bookService.findAll();

        List<BookResponse> responses = books.stream()
                .map(mapper::toResponse)
                .toList();

        return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        try {
            Book book = bookService.findById(id);
            return ResponseEntity.ok(mapper.toResponse(book));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @RequestBody BookRequest request) {
        try {
            bookService.findById(id);
            Book domain = mapper.toDomain(request);
            domain.setId(id);

            Book updated = bookService.saveOrUpdate(domain);
            return ResponseEntity.ok(mapper.toResponse(updated));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.findById(id);
            bookService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
