package org.bibliodigit.domain.port;

import org.bibliodigit.domain.Book;

import java.util.List;


//Define the business operations that the application exposes.
public interface BookService { 
    
    // Basic CRUD
    Book saveOrUpdate(Book book); 
    List<Book> findAll();
    Book findById(Long id);
    void deleteById(Long id);
    
    // Specific search operations
    List<Book> findBooksByTitle(String titleKeyword);
    List<Book> findBooksByAuthorId(Long authorId);
    List<Book> findBooksByCategoryId(Long categoryId);
}
