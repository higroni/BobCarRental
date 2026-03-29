package com.bobcarrental.repository;

import com.bobcarrental.model.Booking;
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
 * Repository za Booking entitet
 * Upravlja rezervacijama vozila (BOOKING.DBF iz legacy sistema)
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Pronalazi rezervacije po datumu izvršenja
     */
    List<Booking> findByBookDate(LocalDate bookDate);
    
    /**
     * Pronalazi rezervacije po datumu kreiranja
     */
    List<Booking> findByTodayDate(LocalDate todayDate);
    
    /**
     * Pronalazi rezervacije po klijentu
     */
    List<Booking> findByClientId(String clientId);
    
    /**
     * Pronalazi rezervacije po tipu vozila
     */
    List<Booking> findByTypeId(String typeId);
    
    /**
     * Pronalazi rezervacije u određenom periodu
     */
    @Query("SELECT b FROM Booking b WHERE b.bookDate BETWEEN :startDate AND :endDate ORDER BY b.bookDate, b.time")
    List<Booking> findByDateRange(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Pronalazi rezervacije po referenci
     */
    List<Booking> findByRefContainingIgnoreCase(String ref);
    
    /**
     * Pronalazi označene rezervacije
     */
    List<Booking> findByTaggedTrue();
    
    /**
     * Pronalazi rezervacije za određeni datum i tip vozila
     */
    @Query("SELECT b FROM Booking b WHERE b.bookDate = :date AND b.typeId = :typeId ORDER BY b.time")
    List<Booking> findByDateAndVehicleType(@Param("date") LocalDate date, 
                                            @Param("typeId") String typeId);
    
    /**
     * Napredna pretraga rezervacija
     */
    @Query("SELECT b FROM Booking b WHERE " +
           "LOWER(b.clientId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.ref) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.typeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.info1) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Booking> searchBookings(@Param("search") String search, Pageable pageable);
    
    /**
     * Broji rezervacije za određeni datum
     */
    long countByBookDate(LocalDate bookDate);
    
    /**
     * Broji rezervacije za klijenta
     */
    long countByClientId(String clientId);
    
    /**
     * Pronalazi današnje rezervacije
     */
    @Query("SELECT b FROM Booking b WHERE b.bookDate = CURRENT_DATE ORDER BY b.time")
    List<Booking> findTodaysBookings();
    
    /**
     * Pronalazi buduće rezervacije
     */
    @Query("SELECT b FROM Booking b WHERE b.bookDate > CURRENT_DATE ORDER BY b.bookDate, b.time")
    List<Booking> findUpcomingBookings();
    
    /**
     * Pronalazi prošle rezervacije
     */
    @Query("SELECT b FROM Booking b WHERE b.bookDate < CURRENT_DATE ORDER BY b.bookDate DESC")
    
    /**
     * Pronalazi rezervaciju po booking datumu i klijentu (kombinovani ključ)
     */
    Optional<Booking> findByBookDateAndClientId(LocalDate bookDate, String clientId);
    
    /**
     * Pronalazi rezervacije u periodu (bookDate)
     */
    List<Booking> findByBookDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Pronalazi rezervacije po klijentu u periodu
     */
    List<Booking> findByClientIdAndBookDateBetween(String clientId, LocalDate startDate, LocalDate endDate);
}

// Made with Bob
