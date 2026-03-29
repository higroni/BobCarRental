package com.bobcarrental.service;

import com.bobcarrental.dto.request.BookingRequest;
import com.bobcarrental.dto.response.BookingResponse;
import com.bobcarrental.dto.response.BookingSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Booking operations
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
public interface BookingService {

    /**
     * Create a new booking
     * 
     * @param request Booking creation request
     * @return Created booking
     */
    BookingResponse createBooking(BookingRequest request);

    /**
     * Update an existing booking
     * 
     * @param id Booking ID
     * @param request Booking update request
     * @return Updated booking
     */
    BookingResponse updateBooking(Long id, BookingRequest request);

    /**
     * Get booking by ID
     * 
     * @param id Booking ID
     * @return Booking details
     */
    BookingResponse getBookingById(Long id);

    /**
     * Get booking by booking number
     * 
     * @param bookingNo Booking number
     * @return Booking details
     */
    BookingResponse getBookingByBookingNo(String bookingNo);

    /**
     * Get all bookings with pagination
     * 
     * @param pageable Pagination parameters
     * @return Page of bookings
     */
    Page<BookingSummaryResponse> getAllBookings(Pageable pageable);

    /**
     * Get bookings by client ID
     * 
     * @param clientId Client ID
     * @return List of bookings
     */
    List<BookingSummaryResponse> getBookingsByClient(Long clientId);

    /**
     * Get bookings by status
     * 
     * @param status Booking status
     * @return List of bookings
     */
    List<BookingSummaryResponse> getBookingsByStatus(String status);

    /**
     * Get bookings by date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of bookings
     */
    List<BookingSummaryResponse> getBookingsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get upcoming bookings (from today onwards)
     * 
     * @return List of upcoming bookings
     */
    List<BookingSummaryResponse> getUpcomingBookings();

    /**
     * Cancel a booking
     * 
     * @param id Booking ID
     * @return Cancelled booking
     */
    BookingResponse cancelBooking(Long id);

    /**
     * Confirm a booking
     * 
     * @param id Booking ID
     * @return Confirmed booking
     */
    BookingResponse confirmBooking(Long id);

    /**
     * Complete a booking
     * 
     * @param id Booking ID
     * @return Completed booking
     */
    BookingResponse completeBooking(Long id);

    /**
     * Delete a booking
     * 
     * @param id Booking ID
     */
    void deleteBooking(Long id);

    /**
     * Check if booking number exists
     * 
     * @param bookingNo Booking number
     * @return true if exists, false otherwise
     */
    boolean existsByBookingNo(String bookingNo);

    /**
     * Validate booking availability
     *
     * @param typeId Vehicle type ID
     * @param fromDate From date
     * @param toDate To date
     * @return true if available, false otherwise
     */
    boolean isVehicleAvailable(String typeId, LocalDate fromDate, LocalDate toDate);
}

// Made with Bob
