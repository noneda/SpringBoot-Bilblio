package org.bibliodigit.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.api.dto.req.LoginRequest;
import org.bibliodigit.api.dto.req.RegisterRequest;
import org.bibliodigit.api.dto.res.AuthResponse;
import org.bibliodigit.api.mapper.AuthMapper;  
import org.bibliodigit.domain.User;
import org.bibliodigit.domain.port.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthMapper authMapper;  


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());

        try {
            User user = userService.login(request.getEmail(), request.getPassword());
            
            AuthResponse response = authMapper.toAuthResponse(user);

            log.debug("Login successful for user: {}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("Registration attempt for email: {}", request.getEmail());

        try {
            User user = userService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getTypeUserId()
            );

            AuthResponse response = authMapper.toAuthResponse(user);

            log.debug("Registration successful for user: {}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        log.debug("Logout attempt");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userService.logout(token);
        }

        return ResponseEntity.noContent().build();
    }
}
