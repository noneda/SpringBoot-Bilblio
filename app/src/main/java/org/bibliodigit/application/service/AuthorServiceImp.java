package org.bibliodigit.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.domain.Author;
import org.bibliodigit.domain.port.AuthorService;
import org.bibliodigit.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImp implements AuthorService {

    private final AuthorRepository authorRepository;

    // ========== BASIC CRUD OPERATIONS ==========

    @Override
    public List<Author> findAll() {
        log.debug("Finding all authors");
        return authorRepository.findAll();
    }

    @Override
    public Optional<Author> findById(Long id) {
        log.debug("Finding author by ID: {}", id);
        return authorRepository.findById(id);
    }

    @Override
    @Transactional
    public Author save(Author author) {
        log.debug("Saving author: {}", author.getName());
        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public Author create(String name, String nationality) {
        log.debug("Creating author: {}", name);

        if (authorRepository.existsByName(name)) {
            throw new RuntimeException("Author with name '" + name + "' already exists");
        }

        Author author = Author.builder()
                .name(name)
                .nationality(nationality)
                .build();

        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public Author update(Long id, Author author) {
        log.debug("Updating author with ID: {}", id);

        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    existingAuthor.setName(author.getName());
                    existingAuthor.setNationality(author.getNationality());
                    return authorRepository.save(existingAuthor);
                })
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting author with ID: {}", id);

        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found with id: " + id);
        }

        authorRepository.deleteById(id);
    }

    // ========== ADDITIONAL SEARCHES ==========

    @Override
    public Optional<Author> findByName(String name) {
        log.debug("Finding author by name: {}", name);
        return authorRepository.findByName(name);
    }

    @Override
    public List<Author> findByNationality(String nationality) {
        log.debug("Finding authors by nationality: {}", nationality);
        return authorRepository.findByNationality(nationality);
    }

    @Override
    public List<Author> findByAuthorsIsNotEmpty() {
        log.debug("Finding authors with at least one book");
        return authorRepository.findByBooksIsNotEmpty();
    }

    // ========== COUNTING OPERATIONS ==========

    @Override
    public boolean existsByName(String name) {
        log.debug("Checking if author exists with name: {}", name);
        return authorRepository.existsByName(name);
    }
}
