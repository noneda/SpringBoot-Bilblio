package org.bibliodigit.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.domain.TypeUser;
import org.bibliodigit.domain.port.TypeUserService;
import org.bibliodigit.repository.TypeUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TypeUserServiceImpl implements TypeUserService {

    private final TypeUserRepository typeUserRepository;

    @Override
    public List<TypeUser> findAll() {
        log.debug("Finding all type users");
        return typeUserRepository.findAll();
    }

    @Override
    public Optional<TypeUser> findById(Long id) {
        log.debug("Finding type user by id: {}", id);
        return typeUserRepository.findById(id);
    }

    @Override
    public Optional<TypeUser> findByType(String type) {
        log.debug("Finding type user by type: {}", type);
        return typeUserRepository.findByType(type.toUpperCase());
    }

    @Override
    @Transactional
    public TypeUser save(TypeUser typeUser) {
        log.debug("Saving type user: {}", typeUser.getType());
        return typeUserRepository.save(typeUser);
    }

    @Override
    @Transactional
    public TypeUser create(String type, String description) {
        log.debug("Creating type user: {}", type);

        String normalizedType = type.toUpperCase();

        if (typeUserRepository.existsByType(normalizedType)) {
            throw new RuntimeException("Type user already exists: " + normalizedType);
        }

        TypeUser typeUser = TypeUser.builder()
            .type(normalizedType)
            .description(description)
            .build();

        return typeUserRepository.save(typeUser);
    }

    @Override
    @Transactional
    public TypeUser update(Long id, TypeUser typeUserData) {
        log.debug("Updating type user with id: {}", id);

        return typeUserRepository.findById(id)
            .map(existingTypeUser -> {
                if (typeUserData.getType() != null && 
                    !existingTypeUser.getType().equals(typeUserData.getType().toUpperCase())) {
                    
                    String newType = typeUserData.getType().toUpperCase();
                    
                    if (typeUserRepository.existsByType(newType)) {
                        throw new RuntimeException("Type user already exists: " + newType);
                    }
                    
                    existingTypeUser.setType(newType);
                }

                if (typeUserData.getDescription() != null) {
                    existingTypeUser.setDescription(typeUserData.getDescription());
                }

                return typeUserRepository.save(existingTypeUser);
            })
            .orElseThrow(() -> new RuntimeException("Type user not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting type user with id: {}", id);

        TypeUser typeUser = typeUserRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Type user not found with id: " + id));

        if (typeUser.getUsers() != null && !typeUser.getUsers().isEmpty()) {
            throw new RuntimeException(
                String.format("Cannot delete type user '%s'. It has %d associated users.", 
                    typeUser.getType(), typeUser.getUsers().size())
            );
        }

        typeUserRepository.deleteById(id);
    }

    @Override
    public boolean existsByType(String type) {
        log.debug("Checking if type user exists: {}", type);
        return typeUserRepository.existsByType(type.toUpperCase());
    }
}
