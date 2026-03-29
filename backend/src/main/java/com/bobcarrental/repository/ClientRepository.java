package com.bobcarrental.repository;

import com.bobcarrental.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository za Client entitet
 * Omogućava upravljanje klijentima (CLIENT.DBF iz legacy sistema)
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    /**
     * Pronalazi klijenta po jedinstvenom clientId (legacy ključ)
     * Koristi se za validaciju i pretragu
     */
    Optional<Client> findByClientId(String clientId);
    
    /**
     * Proverava da li postoji klijent sa datim clientId
     * Implementacija PresenceChk validacije iz legacy sistema
     */
    boolean existsByClientId(String clientId);
    
    /**
     * Pretraga klijenata po imenu (case-insensitive)
     */
    List<Client> findByClientNameContainingIgnoreCase(String clientName);
    
    /**
     * Pronalazi klijente po gradu
     */
    List<Client> findByCity(String city);
    
    /**
     * Pronalazi aktivne klijente (deleted = false)
     */
    @Query("SELECT c FROM Client c WHERE c.deleted = false")
    List<Client> findByActiveTrue();
    
    /**
     * Pronalazi označene (tagged) klijente
     * Koristi se za filtriranje
     */
    List<Client> findByTaggedTrue();
    
    /**
     * Napredna pretraga klijenata
     * Pretražuje po imenu, clientId i gradu
     */
    @Query("SELECT c FROM Client c WHERE " +
           "LOWER(c.clientName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.clientId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Client> searchClients(@Param("search") String search);
    
    /**
     * Paginirana pretraga klijenata
     */
    @Query("SELECT c FROM Client c WHERE " +
           "LOWER(c.clientName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.clientId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.city) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Client> searchClients(@Param("search") String search, Pageable pageable);
    
    /**
     * Broji aktivne klijente (deleted = false)
     */
    @Query("SELECT COUNT(c) FROM Client c WHERE c.deleted = false")
    long countActiveClients();
    
    /**
     * Pronalazi sve klijente osim MISC (specijalni klijent)
     */
    @Query("SELECT c FROM Client c WHERE c.clientId <> 'MISC' ORDER BY c.clientName")
    List<Client> findAllExceptMisc();
    
    /**
     * Methods required by ClientServiceImpl
     */
    @Query("SELECT c FROM Client c WHERE LOWER(c.clientName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Client> searchClientsByName(@Param("name") String name);
    
    boolean existsByClientName(String clientName);
    
    boolean existsByPhone(String phone);
}

// Made with Bob
