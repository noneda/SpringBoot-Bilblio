package org.bibliodigit.application.service;

import org.bibliodigit.domain.Book;
import org.bibliodigit.domain.port.BookService;
import org.bibliodigit.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service // Spring registers it as a service component
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public Book saveOrUpdate(Book book) {
        return bookRepository.save(book); 
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Libro no encontrado con ID: " + id));
    }
    
    @Override
    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findBooksByTitle(String titleKeyword) {
        return bookRepository.findByTitleContaining(titleKeyword);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findBooksByAuthorId(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }
}
