package org.bibliodigit.domain.port;

import org.bibliodigit.domain.TypeUser;
import java.util.List;
import java.util.Optional;

public interface TypeUserService {
    
    List<TypeUser> findAll();
    Optional<TypeUser> findById(Long id);
    Optional<TypeUser> findByType(String type);
    TypeUser save(TypeUser typeUser);
    TypeUser create(String type, String description);
    TypeUser update(Long id, TypeUser typeUser);
    void deleteById(Long id);
    
    boolean existsByType(String type);
}
