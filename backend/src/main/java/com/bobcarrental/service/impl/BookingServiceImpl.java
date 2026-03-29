package com.bobcarrental.service.impl;

import com.bobcarrental.mapper.BookingMapper;
import com.bobcarrental.dto.request.BookingRequest;
import com.bobcarrental.dto.response.BookingResponse;
import com.bobcarrental.dto.response.BookingSummaryResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.model.Booking;
import com.bobcarrental.model.Client;
import com.bobcarrental.model.VehicleType;
import com.bobcarrental.repository.BookingRepository;
import com.bobcarrental.repository.ClientRepository;
import com.bobcarrental.repository.VehicleTypeRepository;
import com.bobcarrental.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of BookingService
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating new booking: {}", request.getBookDate());

        // Validate booking number uniqueness
        if (bookingRepository.findByBookDateAndClientId(request.getBookDate(), request.getClientId()).isPresent()) {
            throw new ValidationException("Booking number already exists: " + request.getBookDate());
        }

        // Validate client exists
        Client client = clientRepository.findByClientId(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        if (!client.getActive()) {
            throw new ValidationException("Cannot create booking for inactive client: " + client.getName());
        }

        // Validate vehicle type exists
        VehicleType vehicleType = vehicleTypeRepository.findByTypeId(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", request.getTypeId()));

        if (!vehicleType.getActive()) {
            throw new ValidationException("Cannot create booking for inactive vehicle type: " + vehicleType.getTypeName());
        }

        // Validate date range
        if (request.getTodayDate().isBefore(request.getBookDate())) {
            throw new ValidationException("To date must be after from date");
        }

        // Check vehicle availability
        if (!isVehicleAvailable(request.getTypeId(), request.getBookDate(), request.getTodayDate())) {
            throw new ValidationException("Vehicle type is not available for the selected dates");
        }

        // Create booking
        Booking booking = bookingMapper.toEntity(request);
        booking.setClient(client);
        booking.setVehicleType(vehicleType);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with id: {}", savedBooking.getId());

        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        log.info("Updating booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        // Check if booking can be modified
        if ("COMPLETED".equals(booking.getStatus()) || "CANCELLED".equals(booking.getStatus())) {
            throw new ValidationException("Cannot update completed or cancelled booking");
        }

        // Validate booking number uniqueness (if changed)
        if (!booking.getBookDate().equals(request.getBookDate())) {
            if (bookingRepository.findByBookDateAndClientId(request.getBookDate(), request.getClientId()).isPresent()) {
                throw new ValidationException("Booking number already exists: " + request.getBookDate());
            }
        }

        // Validate client exists (if changed)
        if (!booking.getClient().getId().equals(request.getClientId())) {
            Client client = clientRepository.findByClientId(request.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));
            
            if (!client.getActive()) {
                throw new ValidationException("Cannot update booking with inactive client: " + client.getName());
            }
            booking.setClient(client);
        }

        // Validate vehicle type exists (if changed)
        if (!booking.getVehicleType().getId().equals(request.getTypeId())) {
            VehicleType vehicleType = vehicleTypeRepository.findByTypeId(request.getTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", request.getTypeId()));
            
            if (!vehicleType.getActive()) {
                throw new ValidationException("Cannot update booking with inactive vehicle type: " + vehicleType.getTypeName());
            }
            booking.setVehicleType(vehicleType);
        }

        // Update booking fields
        bookingMapper.updateEntityFromRequest(request, booking);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking updated successfully with id: {}", updatedBooking.getId());

        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingByBookingNo(String bookingNo) {
        log.info("Fetching booking with booking number: {}", bookingNo);

        // Parse bookingNo format: "YYYYMMDD-CLIENTID" or search all bookings
        // For now, throw exception as we need proper composite key lookup
        throw new ValidationException("Booking lookup by booking number not yet implemented. Use getBookingById instead.");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingSummaryResponse> getAllBookings(Pageable pageable) {
        log.info("Fetching all bookings with pagination");

        return bookingRepository.findAll(pageable)
                .map(bookingMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingSummaryResponse> getBookingsByClient(Long clientId) {
        log.info("Fetching bookings for client id: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        // Use clientId string field instead of Client entity relationship
        return bookingRepository.findByClientId(client.getClientId()).stream()
                .map(bookingMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingSummaryResponse> getBookingsByStatus(String status) {
        log.info("Fetching bookings with status: {}", status);
        
        // Booking entity doesn't have status field - return empty list or throw exception
        log.warn("Booking entity does not have status field - returning empty list");
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingSummaryResponse> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching bookings between {} and {}", startDate, endDate);

        return bookingRepository.findByBookDateBetween(startDate, endDate).stream()
                .map(bookingMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingSummaryResponse> getUpcomingBookings() {
        log.info("Fetching upcoming bookings");

        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusMonths(3); // Next 3 months

        return bookingRepository.findByBookDateBetween(today, futureDate).stream()
                .filter(booking -> "CONFIRMED".equals(booking.getStatus()) || "PENDING".equals(booking.getStatus()))
                .map(bookingMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse cancelBooking(Long id) {
        log.info("Cancelling booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if ("COMPLETED".equals(booking.getStatus())) {
            throw new ValidationException("Cannot cancel completed booking");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new ValidationException("Booking is already cancelled");
        }

        // Set deleted flag to true (legacy system uses this for cancellation)
        booking.setDeleted(true);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking cancelled successfully with id: {}", updatedBooking.getId());
        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    public BookingResponse confirmBooking(Long id) {
        log.info("Confirming booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new ValidationException("Only pending bookings can be confirmed");
        }

        booking.setStatus("CONFIRMED");
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking confirmed successfully with id: {}", updatedBooking.getId());
        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    public BookingResponse completeBooking(Long id) {
        log.info("Completing booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new ValidationException("Cannot complete cancelled booking");
        }

        if ("COMPLETED".equals(booking.getStatus())) {
            throw new ValidationException("Booking is already completed");
        }

        // Allow completing from CONFIRMED or IN_PROGRESS status
        // If booking is CONFIRMED, move it to IN_PROGRESS first, then to COMPLETED
        if ("CONFIRMED".equals(booking.getStatus())) {
            booking.setStatus("IN_PROGRESS");
        }

        booking.setStatus("COMPLETED");
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking completed successfully with id: {}", updatedBooking.getId());
        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        log.info("Deleting booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if ("CONFIRMED".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
            throw new ValidationException("Cannot delete confirmed or completed booking. Cancel it first.");
        }

        bookingRepository.delete(booking);
        log.info("Booking deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBookingNo(String bookingNo) {
        // Parse bookingNo format: "YYYYMMDD-CLIENTID" or search all bookings
        // For now, return false as we need proper composite key lookup
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleAvailable(String typeId, LocalDate fromDate, LocalDate toDate) {
        log.info("Checking vehicle availability for type {} from {} to {}", typeId, fromDate, toDate);

        // Get all confirmed bookings for this vehicle type that overlap with the requested dates
        List<Booking> overlappingBookings = bookingRepository.findByBookDateBetween(
                fromDate.minusDays(30), toDate.plusDays(30)
        ).stream()
                .filter(booking -> booking.getVehicleType().getTypeId().equals(typeId))
                .filter(booking -> "CONFIRMED".equals(booking.getStatus()) || "PENDING".equals(booking.getStatus()))
                .filter(booking -> {
                    // Check if dates overlap
                    return !(booking.getTodayDate().isBefore(fromDate) || booking.getBookDate().isAfter(toDate));
                })
                .collect(Collectors.toList());

        boolean available = overlappingBookings.isEmpty();
        log.info("Vehicle availability check result: {}", available);

        return available;
    }
}

// Made with Bob
