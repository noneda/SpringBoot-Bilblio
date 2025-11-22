package org.bibliodigit.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.domain.TypeUser;
import org.bibliodigit.domain.User;
import org.bibliodigit.domain.port.UserService;
import org.bibliodigit.repository.TypeUserRepository;
import org.bibliodigit.repository.UserRepository;
import org.bibliodigit.util.PasswordUtil;
import org.bibliodigit.util.TokenUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final TypeUserRepository typeUserRepository;


    @Override
    @Transactional
    public User login(String email, String password) {
        log.debug("Login attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = TokenUtil.generateToken();
        user.setAuthToken(token);
        user.setTokenCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.debug("Login successful for user: {}", email);
        return user;
    }

    @Override
    @Transactional
    public User register(String name, String email, String password, Long typeUserId) {
        log.debug("Registration attempt for email: {}", email);

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        TypeUser typeUser = typeUserRepository.findById(typeUserId)
            .orElseThrow(() -> new RuntimeException("Type user not found with id: " + typeUserId));

        User user = User.builder()
            .name(name)
            .email(email)
            .password(PasswordUtil.hashPassword(password))
            .typeUser(typeUser)
            .isActive(true)
            .build();

        User saved = userRepository.save(user);

        String token = TokenUtil.generateToken();
        saved.setAuthToken(token);
        saved.setTokenCreatedAt(LocalDateTime.now());

        userRepository.save(saved);

        log.debug("Registration successful for user: {}", email);
        return saved;
    }

    @Override
    @Transactional
    public void logout(String token) {
        log.debug("Logout attempt with token");

        userRepository.findByAuthToken(token)
            .ifPresent(user -> {
                user.setAuthToken(null);
                user.setTokenCreatedAt(null);
                userRepository.save(user);
                log.debug("Logout successful for user: {}", user.getEmail());
            });
    }

    @Override
    public Optional<User> validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return Optional.empty();
        }

        Optional<User> userOpt = userRepository.findByAuthToken(token);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        if (!user.getIsActive()) {
            return Optional.empty();
        }

        LocalDateTime expirationTime = user.getTokenCreatedAt().plusHours(24);
        if (LocalDateTime.now().isAfter(expirationTime)) {
            log.debug("Token expired for user: {}", user.getEmail());
            return Optional.empty();
        }

        return Optional.of(user);
    }


    @Override
    public List<User> findAll() {
        log.debug("Finding all users");
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User update(Long id, User userData) {
        log.debug("Updating user with id: {}", id);

        return userRepository.findById(id)
            .map(existingUser -> {
                existingUser.setName(userData.getName());
                
                if (!existingUser.getEmail().equals(userData.getEmail())) {
                    if (userRepository.existsByEmail(userData.getEmail())) {
                        throw new RuntimeException("Email already exists: " + userData.getEmail());
                    }
                    existingUser.setEmail(userData.getEmail());
                }

                if (userData.getTypeUser() != null && 
                    !userData.getTypeUser().getId().equals(existingUser.getTypeUser().getId())) {
                    
                    TypeUser newTypeUser = typeUserRepository.findById(userData.getTypeUser().getId())
                        .orElseThrow(() -> new RuntimeException("Type user not found"));
                    existingUser.setTypeUser(newTypeUser);
                }

                if (userData.getPassword() != null && !userData.getPassword().isEmpty()) {
                    existingUser.setPassword(PasswordUtil.hashPassword(userData.getPassword()));
                }

                if (userData.getIsActive() != null) {
                    existingUser.setIsActive(userData.getIsActive());
                }

                return userRepository.save(existingUser);
            })
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public List<User> findByTypeUserId(Long typeUserId) {
        log.debug("Finding users by type user id: {}", typeUserId);
        return userRepository.findByTypeUserId(typeUserId);
    }

    @Override
    public List<User> findByIsActive(Boolean isActive) {
        log.debug("Finding users by active status: {}", isActive);
        return userRepository.findByIsActive(isActive);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User toggleActiveStatus(Long id) {
        log.debug("Toggling active status for user: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setIsActive(!user.getIsActive());
        
        if (!user.getIsActive()) {
            user.setAuthToken(null);
            user.setTokenCreatedAt(null);
        }
        
        return userRepository.save(user);
    }
}
