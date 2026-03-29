package com.bobcarrental.repository;

import com.bobcarrental.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository za Account entitet
 * Upravlja knjigom prihoda (ACCOUNTS.DBF iz legacy sistema)
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * Pronalazi račune po broju dokumenta (Integer, not Long)
     */
    Optional<Account> findByNum(Integer num);
    
    /**
     * Proverava da li postoji račun sa datim brojem
     */
    boolean existsByNum(Integer num);
    
    /**
     * Pronalazi račune po datumu
     */
    List<Account> findByDate(LocalDate date);
    
    /**
     * Pronalazi račune u određenom periodu
     */
    List<Account> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Pronalazi račune po opisu (parcijalno poklapanje)
     */
    List<Account> findByDescContainingIgnoreCase(String desc);
    
    /**
     * Pronalazi označene račune
     */
    List<Account> findByTaggedTrue();
    
    /**
     * Napredna pretraga računa po opisu
     */
    @Query("SELECT a FROM Account a WHERE LOWER(a.desc) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Account> searchAccounts(@Param("search") String search, Pageable pageable);
    
    /**
     * Ukupno primljeno (uplate) za period
     */
    @Query("SELECT COALESCE(SUM(a.recd), 0) FROM Account a WHERE a.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalReceivedForPeriod(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * Ukupno fakturisano (dugovanja) za period
     */
    @Query("SELECT COALESCE(SUM(a.bill), 0) FROM Account a WHERE a.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalBilledForPeriod(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    /**
     * Pronalazi račune po klijentu u periodu
     */
    List<Account> findByClientIdAndDateBetween(String clientId, LocalDate startDate, LocalDate endDate);
}

// Made with Bob
