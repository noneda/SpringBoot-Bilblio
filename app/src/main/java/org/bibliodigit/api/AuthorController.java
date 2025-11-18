package org.bibliodigit.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

import org.bibliodigit.api.dto.AuthorRequest;
import org.bibliodigit.api.dto.AuthorResponse;
import org.bibliodigit.api.mapper.AuthorMapper;
import org.bibliodigit.domain.Author;
import org.bibliodigit.domain.port.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private final AuthorMapper mapper;

    // ========== BASIC CRUD OPERATIONS ==========

    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {
        log.debug("Creating author: {}", request.getName());

        Author created = authorService.create(
            request.getName(),
            request.getNationality()
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {
        log.debug("Getting all authors");

        List<AuthorResponse> responses = authorService.findAll()
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        log.debug("Getting author by id: {}", id);

        return authorService.findById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {

        log.debug("Updating author with id: {}", id);

        try {
            Author updatedData = mapper.toDomain(request);
            Author updated = authorService.update(id, updatedData);

            return ResponseEntity.ok(mapper.toResponse(updated));

        } catch (RuntimeException e) {
            log.error("Error updating author: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        log.debug("Deleting author with id: {}", id);

        try {
            authorService.deleteById(id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            log.error("Error deleting author: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ADDITIONAL SEARCHES ==========

    @GetMapping("/search")
    public ResponseEntity<List<AuthorResponse>> findByNationality(
            @RequestParam String nationality) {

        log.debug("GET /api/authors/search?nationality={}", nationality);

        List<AuthorResponse> results =
                authorService.findByNationality(nationality)
                        .stream()
                        .map(mapper::toResponse)
                        .toList();

        return ResponseEntity.ok(results);
    }

    @GetMapping("/with-books")
    public ResponseEntity<List<AuthorResponse>> findAuthorsWithBooks() {
        log.debug("GET /api/authors/with-books");

        List<AuthorResponse> results =
                authorService.findByAuthorsIsNotEmpty()
                        .stream()
                        .map(mapper::toResponse)
                        .toList();

        return ResponseEntity.ok(results);
    }
}
