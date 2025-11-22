package org.bibliodigit.domain.port;

import org.bibliodigit.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    User login(String email, String password);
    User register(String name, String email, String password, Long typeUserId);
    void logout(String token);
    Optional<User> validateToken(String token);
    
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User update(Long id, User user);
    void deleteById(Long id);
    
    List<User> findByTypeUserId(Long typeUserId);
    List<User> findByIsActive(Boolean isActive);
    
    boolean existsByEmail(String email);
    
    User toggleActiveStatus(Long id);
}
