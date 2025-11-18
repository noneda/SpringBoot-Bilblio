package org.bibliodigit.api.mapper;

import org.bibliodigit.api.dto.BookRequest;
import org.bibliodigit.api.dto.BookResponse;
import org.bibliodigit.domain.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toDomain(BookRequest request) {
        return new Book(
            null,
            request.title(),
            request.authorId(),
            request.categoryId()
        );
    }

    public BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthorId(),
                book.getCategoryId()
        );
    }
}
