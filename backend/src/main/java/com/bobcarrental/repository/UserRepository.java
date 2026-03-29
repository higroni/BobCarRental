package com.bobcarrental.repository;

import com.bobcarrental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository za User entitet
 * Omogućava pristup korisničkim podacima za autentifikaciju i autorizaciju
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Pronalazi korisnika po username-u
     * Koristi se za login i JWT autentifikaciju
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Pronalazi korisnika po email-u
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Proverava da li postoji korisnik sa datim username-om
     * Koristi se za validaciju pri registraciji
     */
    boolean existsByUsername(String username);
    
    /**
     * Proverava da li postoji korisnik sa datim email-om
     */
    boolean existsByEmail(String email);
    
    /**
     * Broji aktivne korisnike
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countActiveUsers();
}

// Made with Bob
