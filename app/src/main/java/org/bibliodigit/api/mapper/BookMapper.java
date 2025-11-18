package org.bibliodigit.api.mapper;

import org.bibliodigit.api.dto.BookRequest;
import org.bibliodigit.api.dto.BookResponse;
import org.bibliodigit.domain.Author;
import org.bibliodigit.domain.Book;
import org.bibliodigit.domain.Category;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookResponse toResponse(Book book) {
        return BookResponse.builder()
            .id(book.getId())
            .title(book.getTitle())
            .year(book.getYear())

            .authorId(book.getAuthor() != null ? book.getAuthor().getId() : null)
            .authorName(book.getAuthor() != null ? book.getAuthor().getName() : null)
            .authorNationality(book.getAuthor() != null ? book.getAuthor().getNationality() : null)

            .categoryId(book.getCategory() != null ? book.getCategory().getId() : null)
            .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
            .categoryDescription(book.getCategory() != null ? book.getCategory().getDescription() : null)
            .build();
    }
    
    public Book toDomain(BookRequest request) {
        return Book.builder()
            .title(request.getTitle())
            .year(request.getYear())
            .author(Author.builder().id(request.getAuthorId()).build())
            .category(Category.builder().id(request.getCategoryId()).build())
            .build();
    }
}
