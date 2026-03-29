package com.bobcarrental.repository;

import com.bobcarrental.model.HeaderTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository za HeaderTemplate entitet
 * Upravlja šablonima zaglavlja dokumenata (HEADER.TXT iz legacy sistema)
 * ADMIN ONLY pristup
 */
@Repository
public interface HeaderTemplateRepository extends JpaRepository<HeaderTemplate, Long> {
    
    /**
     * Pronalazi aktivne šablone
     * HeaderTemplate ima samo line1-8 i active polja
     */
    List<HeaderTemplate> findByActiveTrue();
    
    /**
     * Pronalazi neaktivne šablone
     */
    List<HeaderTemplate> findByActiveFalse();
    
    /**
     * Pretraga šablona po imenu, sadržaju i opisu
     */
    @Query("SELECT h FROM HeaderTemplate h WHERE " +
           "LOWER(h.templateName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(h.templateContent) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(h.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<HeaderTemplate> searchTemplates(@Param("search") String search);
    
    /**
     * Broji aktivne šablone
     */
    @Query("SELECT COUNT(h) FROM HeaderTemplate h WHERE h.active = true")
    long countActiveTemplates();
    
    /**
     * Pronalazi default šablon (prvi ako ima više)
     */
    Optional<HeaderTemplate> findFirstByIsDefaultTrueOrderByIdAsc();
    
    /**
     * Pronalazi prvi aktivni šablon (fallback ako nema default)
     */
    Optional<HeaderTemplate> findFirstByActiveTrueOrderByIdAsc();
}

// Made with Bob
