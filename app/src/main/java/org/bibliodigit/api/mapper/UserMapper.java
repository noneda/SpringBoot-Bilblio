package org.bibliodigit.api.mapper;

import org.bibliodigit.api.dto.req.RegisterRequest;
import org.bibliodigit.api.dto.req.UserRequest;
import org.bibliodigit.api.dto.res.UserResponse;
import org.bibliodigit.domain.TypeUser;
import org.bibliodigit.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .typeUserId(user.getTypeUser() != null ? user.getTypeUser().getId() : null)
            .typeUserName(user.getTypeUser() != null ? user.getTypeUser().getType() : null)
            .typeUserDescription(user.getTypeUser() != null ? user.getTypeUser().getDescription() : null)
            .isActive(user.getIsActive())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    public User toDomain(UserRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(request.getPassword())  
            .typeUser(TypeUser.builder().id(request.getTypeUserId()).build())
            .isActive(request.getIsActive() != null ? request.getIsActive() : true)
            .build();
    }

    public User toDomain(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(request.getPassword())  
            .typeUser(TypeUser.builder().id(request.getTypeUserId()).build())
            .isActive(true)
            .build();
    }
}
