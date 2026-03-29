package com.bobcarrental.repository;

import com.bobcarrental.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository za Role entitet
 * Upravlja korisničkim rolama (ADMIN, USER)
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Pronalazi rolu po imenu (ADMIN, USER)
     */
    Optional<Role> findByName(Role.RoleName name);
    
    /**
     * Proverava da li postoji rola sa datim imenom
     */
    boolean existsByName(Role.RoleName name);
}

// Made with Bob
