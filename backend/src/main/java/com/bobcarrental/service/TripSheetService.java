package com.bobcarrental.service;

import com.bobcarrental.dto.request.TripSheetRequest;
import com.bobcarrental.dto.response.TripSheetResponse;
import com.bobcarrental.dto.response.TripSheetSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for TripSheet operations
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
public interface TripSheetService {

    /**
     * Create a new trip sheet
     * 
     * @param request Trip sheet creation request
     * @return Created trip sheet
     */
    TripSheetResponse createTripSheet(TripSheetRequest request);

    /**
     * Update an existing trip sheet
     * 
     * @param id Trip sheet ID
     * @param request Trip sheet update request
     * @return Updated trip sheet
     */
    TripSheetResponse updateTripSheet(Long id, TripSheetRequest request);

    /**
     * Get trip sheet by ID
     * 
     * @param id Trip sheet ID
     * @return Trip sheet details
     */
    TripSheetResponse getTripSheetById(Long id);

    /**
     * Get trip sheet by trip number
     * 
     * @param tripNo Trip number
     * @return Trip sheet details
     */
    TripSheetResponse getTripSheetByTripNo(String tripNo);

    /**
     * Get all trip sheets with pagination
     * 
     * @param pageable Pagination parameters
     * @return Page of trip sheets
     */
    Page<TripSheetSummaryResponse> getAllTripSheets(Pageable pageable);

    /**
     * Get trip sheets by booking ID
     * 
     * @param bookingId Booking ID
     * @return List of trip sheets
     */
    List<TripSheetSummaryResponse> getTripSheetsByBooking(Long bookingId);

    /**
     * Get trip sheets by status
     * 
     * @param status Trip sheet status (OPEN, STARTED, FINISHED)
     * @return List of trip sheets
     */
    List<TripSheetSummaryResponse> getTripSheetsByStatus(String status);

    /**
     * Get trip sheets by date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of trip sheets
     */
    List<TripSheetSummaryResponse> getTripSheetsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Start a trip (change status from OPEN to STARTED)
     * 
     * @param id Trip sheet ID
     * @return Updated trip sheet
     */
    TripSheetResponse startTrip(Long id);

    /**
     * Finish a trip (change status from STARTED to FINISHED)
     * Calculates final fare based on actual km and time
     * 
     * @param id Trip sheet ID
     * @param endKm End kilometer reading
     * @param endDate End date
     * @param endTime End time
     * @return Updated trip sheet with calculated fare
     */
    TripSheetResponse finishTrip(Long id, Double endKm, LocalDate endDate, LocalTime endTime);

    /**
     * Calculate fare for a trip sheet
     * Uses FareCalculationService to compute charges
     * 
     * @param id Trip sheet ID
     * @return Trip sheet with calculated fare
     */
    TripSheetResponse calculateFare(Long id);

    /**
     * Delete a trip sheet
     * 
     * @param id Trip sheet ID
     */
    void deleteTripSheet(Long id);

    /**
     * Check if trip number exists
     * 
     * @param tripNo Trip number
     * @return true if exists, false otherwise
     */
    boolean existsByTripNo(String tripNo);

    /**
     * Get active trip sheets (OPEN or STARTED)
     * 
     * @return List of active trip sheets
     */
    List<TripSheetSummaryResponse> getActiveTripSheets();

    /**
     * Get completed trip sheets (FINISHED)
     * 
     * @return List of completed trip sheets
     */
    List<TripSheetSummaryResponse> getCompletedTripSheets();

    /**
     * Get unbilled trip sheets (FINISHED but not yet billed)
     * 
     * @return List of unbilled trip sheets
     */
    List<TripSheetSummaryResponse> getUnbilledTripSheets();
}

// Made with Bob
