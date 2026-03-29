package com.bobcarrental.repository;

import com.bobcarrental.model.TripSheet;
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
 * Repository za TripSheet entitet
 * Upravlja putnim listovima (TRPSHEET.DBF iz legacy sistema)
 */
@Repository
public interface TripSheetRepository extends JpaRepository<TripSheet, Long> {
    
    /**
     * Proverava da li postoji putni list sa datim brojem
     * Implementacija PresenceChk validacije
     */
    boolean existsByTrpNum(Integer trpNum);
    
    /**
     * Pronalazi putne listove po datumu
     */
    List<TripSheet> findByTrpDate(LocalDate trpDate);
    
    /**
     * Pronalazi putne listove po klijentu
     */
    List<TripSheet> findByClientId(String clientId);
    
    /**
     * Pronalazi putne listove po vozilu (registarska oznaka)
     */
    List<TripSheet> findByRegNum(String regNum);
    
    /**
     * Pronalazi putne listove po vozaču
     */
    List<TripSheet> findByDriver(String driver);
    
    /**
     * Pronalazi putne listove po tipu vozila
     */
    List<TripSheet> findByTypeId(String typeId);
    
    /**
     * Pronalazi fakturisane putne listove
     */
    List<TripSheet> findByIsBilledTrue();
    
    /**
     * Pronalazi nefakturisane putne listove
     */
    List<TripSheet> findByIsBilledFalse();
    
    /**
     * Pronalazi putne listove po broju računa
     */
    List<TripSheet> findByBillNum(Integer billNum);
    
    /**
     * Pronalazi putne listove po statusu (F=Flat, S=Split, O=Outstation)
     */
    List<TripSheet> findByStatus(String status);
    
    /**
     * Pronalazi putne listove u određenom periodu
     */
    @Query("SELECT t FROM TripSheet t WHERE t.trpDate BETWEEN :startDate AND :endDate ORDER BY t.trpDate DESC")
    List<TripSheet> findByDateRange(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
    
    /**
     * Pronalazi putne listove po periodu putovanja
     */
    @Query("SELECT t FROM TripSheet t WHERE t.startDt >= :startDate AND t.endDt <= :endDate")
    List<TripSheet> findByTravelPeriod(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    /**
     * Napredna pretraga putnih listova
     */
    @Query("SELECT t FROM TripSheet t WHERE " +
           "LOWER(t.clientId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.regNum) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.driver) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "str(t.trpNum) LIKE CONCAT('%', :search, '%')")
    Page<TripSheet> searchTripSheets(@Param("search") String search, Pageable pageable);
    
    /**
     * Broji putne listove za klijenta
     */
    long countByClientId(String clientId);
    
    /**
     * Broji nefakturisane putne listove
     */
    @Query("SELECT COUNT(t) FROM TripSheet t WHERE t.isBilled = false")
    long countUnbilledTripSheets();
    
    /**
     * Pronalazi putne listove spremne za fakturisanje
     */
    @Query("SELECT t FROM TripSheet t WHERE t.isBilled = false AND t.endDt IS NOT NULL ORDER BY t.trpDate")
    List<TripSheet> findReadyForBilling();
    
    /**
     * Ukupan prihod za period
     */
    @Query("SELECT SUM(t.hiring + t.extra + t.halt + t.minimum + t.permit + t.misc) " +
           "FROM TripSheet t WHERE t.trpDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueForPeriod(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
    
    /**
     * Ukupan prihod za klijenta
     */
    @Query("SELECT SUM(t.hiring + t.extra + t.halt + t.minimum + t.permit + t.misc) " +
           "FROM TripSheet t WHERE t.clientId = :clientId")
    Double getTotalRevenueForClient(@Param("clientId") String clientId);
    
    /**
     * Pronalazi najnoviji broj putnog lista
     */
    @Query("SELECT MAX(t.trpNum) FROM TripSheet t")
    Optional<Integer> findMaxTripNumber();
    
    /**
     * Pronalazi putni list po trip broju (alias za findByTripNumber)
     */
    Optional<TripSheet> findByTrpNum(Integer trpNum);
    
    /**
     * Pronalazi putne listove u periodu (startDate)
     */
    List<TripSheet> findByStartDtBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Pronalazi putne listove po klijentu u periodu (trpDate)
     */
    List<TripSheet> findByClientIdAndTrpDateBetween(String clientId, LocalDate startDate, LocalDate endDate);
}

// Made with Bob
