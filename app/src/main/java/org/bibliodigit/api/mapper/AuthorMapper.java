package org.bibliodigit.api.mapper;

import org.bibliodigit.api.dto.AuthorRequest;
import org.bibliodigit.api.dto.AuthorResponse;
import org.bibliodigit.domain.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public AuthorResponse toResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .nationality(author.getNationality())
                .build();
    }

    public Author toDomain(AuthorRequest request) {
        return Author.builder()
                .name(request.getName())
                .nationality(request.getNationality())
                .build();
    }
}
