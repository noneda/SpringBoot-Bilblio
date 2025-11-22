package org.bibliodigit.api.mapper;

import org.bibliodigit.api.dto.req.TypeUserRequest;
import org.bibliodigit.api.dto.res.TypeUserResponse;
import org.bibliodigit.domain.TypeUser;
import org.springframework.stereotype.Component;

@Component
public class TypeUserMapper {

    public TypeUserResponse toResponse(TypeUser typeUser) {
        if (typeUser == null) {
            return null;
        }

        return TypeUserResponse.builder()
            .id(typeUser.getId())
            .type(typeUser.getType())
            .description(typeUser.getDescription())
            .userCount(typeUser.getUsers() != null ? typeUser.getUsers().size() : 0)
            .build();
    }

    public TypeUser toDomain(TypeUserRequest request) {
        if (request == null) {
            return null;
        }

        return TypeUser.builder()
            .type(request.getType().toUpperCase())  // Normalizar a mayúsculas
            .description(request.getDescription())
            .build();
    }
}
