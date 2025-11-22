package org.bibliodigit.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.api.dto.req.TypeUserRequest;
import org.bibliodigit.api.dto.res.TypeUserResponse;
import org.bibliodigit.api.mapper.TypeUserMapper;
import org.bibliodigit.domain.TypeUser;
import org.bibliodigit.domain.port.TypeUserService;
import org.bibliodigit.security.RequireRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/type-users")
@RequiredArgsConstructor
public class TypeUserController {

    private final TypeUserService typeUserService;
    private final TypeUserMapper mapper;

    @GetMapping
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})  // Todos pueden ver
    public ResponseEntity<List<TypeUserResponse>> getAllTypeUsers() {
        log.debug("Getting all type users");

        List<TypeUserResponse> responses = typeUserService.findAll()
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})
    public ResponseEntity<TypeUserResponse> getTypeUserById(@PathVariable Long id) {
        log.debug("Getting type user by id: {}", id);

        return typeUserService.findById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})
    public ResponseEntity<TypeUserResponse> getTypeUserByType(@PathVariable String type) {
        log.debug("Getting type user by type: {}", type);

        return typeUserService.findByType(type)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequireRole("ADMIN")
    public ResponseEntity<TypeUserResponse> createTypeUser(@Valid @RequestBody TypeUserRequest request) {
        log.debug("Creating type user: {}", request.getType());

        try {
            TypeUser created = typeUserService.create(
                request.getType(),
                request.getDescription()
            );

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(created));
        } catch (RuntimeException e) {
            log.error("Error creating type user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<TypeUserResponse> updateTypeUser(
            @PathVariable Long id,
            @Valid @RequestBody TypeUserRequest request) {

        log.debug("Updating type user with id: {}", id);

        try {
            TypeUser typeUserToUpdate = mapper.toDomain(request);
            TypeUser updated = typeUserService.update(id, typeUserToUpdate);

            return ResponseEntity.ok(mapper.toResponse(updated));
        } catch (RuntimeException e) {
            log.error("Error updating type user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<Void> deleteTypeUser(@PathVariable Long id) {
        log.debug("Deleting type user with id: {}", id);

        try {
            typeUserService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting type user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/exists")
    @RequireRole("ADMIN")
    public ResponseEntity<Boolean> existsByType(@RequestParam String type) {
        log.debug("Checking if type user exists: {}", type);

        boolean exists = typeUserService.existsByType(type);
        return ResponseEntity.ok(exists);
    }
}
