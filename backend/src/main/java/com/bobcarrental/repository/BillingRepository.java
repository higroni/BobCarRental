package com.bobcarrental.repository;

import com.bobcarrental.model.Billing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository za Billing entitet
 * Upravlja računima (BILLING.DBF iz legacy sistema)
 */
@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    
    /**
     * Proverava da li postoji račun sa datim brojem
     * Implementacija BillChk validacije
     */
    boolean existsByBillNum(Integer billNum);
    
    /**
     * Pronalazi račune po datumu
     */
    List<Billing> findByBillDate(LocalDate billDate);
    
    /**
     * Pronalazi račune po klijentu
     */
    List<Billing> findByClientId(String clientId);
    
    /**
     * Pronalazi račun po broju putnog lista
     */
    Optional<Billing> findByTrpNum(Integer trpNum);
    
    /**
     * Pronalazi označene račune
     */
    List<Billing> findByTaggedTrue();
    
    /**
     * Pronalazi račune u određenom periodu
     */
    @Query("SELECT b FROM Billing b WHERE b.billDate BETWEEN :startDate AND :endDate ORDER BY b.billDate DESC")
    List<Billing> findByDateRange(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Pronalazi račune po klijentu u periodu
     */
    @Query("SELECT b FROM Billing b WHERE b.clientId = :clientId AND b.billDate BETWEEN :startDate AND :endDate ORDER BY b.billDate DESC")
    List<Billing> findByClientAndDateRange(@Param("clientId") String clientId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    /**
     * Napredna pretraga računa
     */
    @Query("SELECT b FROM Billing b WHERE " +
           "LOWER(b.clientId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(b.billNum AS string) LIKE CONCAT('%', :search, '%') OR " +
           "CAST(b.trpNum AS string) LIKE CONCAT('%', :search, '%')")
    Page<Billing> searchBillings(@Param("search") String search, Pageable pageable);
    
    /**
     * Broji račune za klijenta
     */
    long countByClientId(String clientId);
    
    /**
     * Ukupan iznos računa za period
     */
    @Query("SELECT SUM(b.billAmt) FROM Billing b WHERE b.billDate BETWEEN :startDate AND :endDate")
    Double getTotalAmountForPeriod(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
    
    /**
     * Ukupan iznos računa za klijenta
     */
    @Query("SELECT SUM(b.billAmt) FROM Billing b WHERE b.clientId = :clientId")
    Double getTotalAmountForClient(@Param("clientId") String clientId);
    
    /**
     * Pronalazi najnoviji broj računa
     */
    @Query("SELECT MAX(b.billNum) FROM Billing b")
    Optional<Integer> findMaxBillNumber();
    
    /**
     * Pronalazi račun po broju
     */
    Optional<Billing> findByBillNum(Integer billNum);
    
    /**
     * Pronalazi račune po datumu (sa paginacijom)
     */
    List<Billing> findByBillDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Pronalazi račune po klijentu u periodu
     */
    List<Billing> findByClientIdAndBillDateBetween(String clientId, LocalDate startDate, LocalDate endDate);
}

// Made with Bob
