package org.bibliodigit.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.api.dto.req.UserRequest;
import org.bibliodigit.api.dto.res.UserResponse;
import org.bibliodigit.api.mapper.UserMapper;
import org.bibliodigit.domain.User;
import org.bibliodigit.domain.port.UserService;
import org.bibliodigit.security.RequireRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;


    @GetMapping
    @RequireRole("ADMIN")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.debug("Getting all users");

        List<UserResponse> responses = userService.findAll()
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }


    @GetMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.debug("Getting user by id: {}", id);

        return userService.findById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/me")
    @RequireRole({"ADMIN", "USER"})
    public ResponseEntity<UserResponse> getMyProfile(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        log.debug("Getting profile for user: {}", currentUserId);

        return userService.findById(currentUserId)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/me")
    @RequireRole({"ADMIN", "USER"})
    public ResponseEntity<UserResponse> updateMyProfile(
            HttpServletRequest request,
            @Valid @RequestBody UserRequest userRequest) {
        
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        log.debug("Updating profile for user: {}", currentUserId);

        try {
            User userToUpdate = mapper.toDomain(userRequest);
            User updated = userService.update(currentUserId, userToUpdate);

            return ResponseEntity.ok(mapper.toResponse(updated));
        } catch (RuntimeException e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping
    @RequireRole("ADMIN")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        log.debug("Creating user: {}", request.getEmail());

        User created = userService.register(
            request.getName(),
            request.getEmail(),
            request.getPassword(),
            request.getTypeUserId()
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.toResponse(created));
    }


    @PutMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        log.debug("Updating user with id: {}", id);

        try {
            User userToUpdate = mapper.toDomain(request);
            User updated = userService.update(id, userToUpdate);

            return ResponseEntity.ok(mapper.toResponse(updated));
        } catch (RuntimeException e) {
            log.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("Deleting user with id: {}", id);

        try {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @PatchMapping("/{id}/toggle-status")
    @RequireRole("ADMIN")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long id) {
        log.debug("Toggling status for user: {}", id);

        try {
            User updated = userService.toggleActiveStatus(id);
            return ResponseEntity.ok(mapper.toResponse(updated));
        } catch (RuntimeException e) {
            log.error("Error toggling user status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/type/{typeUserId}")
    @RequireRole("ADMIN")
    public ResponseEntity<List<UserResponse>> getUsersByType(@PathVariable Long typeUserId) {
        log.debug("Getting users by type: {}", typeUserId);

        List<UserResponse> responses = userService.findByTypeUserId(typeUserId)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }
}
