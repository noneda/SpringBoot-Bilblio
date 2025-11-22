package org.bibliodigit.api.mapper;

import org.bibliodigit.api.dto.res.AuthResponse;
import org.bibliodigit.domain.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(User user) {
        if (user == null) {
            return null;
        }

        return AuthResponse.builder()
            .token(user.getAuthToken())
            .type("Bearer")
            .userId(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .role(user.getTypeUser() != null ? user.getTypeUser().getType() : null)
            .build();
    }
}
