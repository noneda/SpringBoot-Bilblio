package org.bibliodigit.repository;

import org.bibliodigit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @EntityGraph(attributePaths = {"typeUser"})
    Optional<User> findByEmail(String email);
    
    @EntityGraph(attributePaths = {"typeUser"})
    Optional<User> findByAuthToken(String authToken); 
    
    @EntityGraph(attributePaths = {"typeUser"})
    @Override
    List<User> findAll();
    
    @EntityGraph(attributePaths = {"typeUser"})
    Optional<User> findById(Long id);
    
    boolean existsByEmail(String email);
    
    List<User> findByTypeUserId(Long typeUserId);
    
    List<User> findByIsActive(Boolean isActive);
}
