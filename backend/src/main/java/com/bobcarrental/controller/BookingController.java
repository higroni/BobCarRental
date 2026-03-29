package com.bobcarrental.controller;

import com.bobcarrental.dto.request.BookingRequest;
import com.bobcarrental.dto.response.BookingResponse;
import com.bobcarrental.dto.response.BookingSummaryResponse;
import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.PageResponse;
import com.bobcarrental.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Booking management
 * Handles CRUD operations and booking workflow
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Get all bookings with pagination
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<PageResponse<BookingSummaryResponse>>> getAllBookings(
            @PageableDefault(size = 20, sort = "bookDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Fetching all bookings with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<BookingSummaryResponse> bookingPage = bookingService.getAllBookings(pageable);
        PageResponse<BookingSummaryResponse> pageResponse = PageResponse.of(bookingPage);
        
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", pageResponse));
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        log.info("Fetching booking with id: {}", id);
        
        BookingResponse booking = bookingService.getBookingById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking));
    }

    /**
     * Get booking by booking number
     */
    @GetMapping("/by-number/{bookingNo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingByBookingNo(@PathVariable String bookingNo) {
        log.info("Fetching booking with booking number: {}", bookingNo);
        
        BookingResponse booking = bookingService.getBookingByBookingNo(bookingNo);
        
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking));
    }

    /**
     * Get bookings by client ID
     */
    @GetMapping("/by-client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<BookingSummaryResponse>>> getBookingsByClient(
            @PathVariable Long clientId) {
        
        log.info("Fetching bookings for client id: {}", clientId);
        
        List<BookingSummaryResponse> bookings = bookingService.getBookingsByClient(clientId);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d bookings for client", bookings.size()), bookings));
    }

    /**
     * Get bookings by status
     */
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<BookingSummaryResponse>>> getBookingsByStatus(
            @PathVariable String status) {
        
        log.info("Fetching bookings with status: {}", status);
        
        List<BookingSummaryResponse> bookings = bookingService.getBookingsByStatus(status);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d bookings with status %s", bookings.size(), status), bookings));
    }

    /**
     * Get bookings by date range
     */
    @GetMapping("/by-date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<BookingSummaryResponse>>> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Fetching bookings between {} and {}", startDate, endDate);
        
        List<BookingSummaryResponse> bookings = bookingService.getBookingsByDateRange(startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d bookings in date range", bookings.size()), bookings));
    }

    /**
     * Get upcoming bookings
     */
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<BookingSummaryResponse>>> getUpcomingBookings() {
        log.info("Fetching upcoming bookings");
        
        List<BookingSummaryResponse> bookings = bookingService.getUpcomingBookings();
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d upcoming bookings", bookings.size()), bookings));
    }

    /**
     * Create new booking
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request) {
        
        log.info("Creating new booking: {}", request.getBookDate());
        
        BookingResponse booking = bookingService.createBooking(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", booking));
    }

    /**
     * Update existing booking
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<BookingResponse>> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingRequest request) {
        
        log.info("Updating booking with id: {}", id);
        
        BookingResponse booking = bookingService.updateBooking(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Booking updated successfully", booking));
    }

    /**
     * Cancel booking
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable Long id) {
        log.info("Cancelling booking with id: {}", id);
        
        BookingResponse booking = bookingService.cancelBooking(id);
        
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }

    /**
     * Confirm booking
     */
    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmBooking(@PathVariable Long id) {
        log.info("Confirming booking with id: {}", id);
        
        BookingResponse booking = bookingService.confirmBooking(id);
        
        return ResponseEntity.ok(ApiResponse.success("Booking confirmed successfully", booking));
    }

    /**
     * Complete booking
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponse>> completeBooking(@PathVariable Long id) {
        log.info("Completing booking with id: {}", id);
        
        BookingResponse booking = bookingService.completeBooking(id);
        
        return ResponseEntity.ok(ApiResponse.success("Booking completed successfully", booking));
    }

    /**
     * Delete booking
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Long id) {
        log.info("Deleting booking with id: {}", id);
        
        bookingService.deleteBooking(id);
        
        return ResponseEntity.ok(ApiResponse.success("Booking deleted successfully", null));
    }

    /**
     * Check if booking number exists
     */
    @GetMapping("/validate/booking-no")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<Boolean>> validateBookingNo(@RequestParam String bookingNo) {
        log.info("Validating booking number: {}", bookingNo);
        
        boolean exists = bookingService.existsByBookingNo(bookingNo);
        
        return ResponseEntity.ok(ApiResponse.success(
                exists ? "Booking number already exists" : "Booking number is available", !exists));
    }

    /**
     * Check vehicle availability
     */
    @GetMapping("/check-availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<Boolean>> checkVehicleAvailability(
            @RequestParam String vehicleTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Checking vehicle availability for type {} from {} to {}", vehicleTypeId, fromDate, toDate);
        
        boolean available = bookingService.isVehicleAvailable(vehicleTypeId, fromDate, toDate);
        
        return ResponseEntity.ok(ApiResponse.success(
                available ? "Vehicle is available" : "Vehicle is not available for selected dates", available));
    }
}

// Made with Bob
