package com.bobcarrental.service.impl;

import com.bobcarrental.mapper.TripSheetMapper;
import com.bobcarrental.dto.request.TripSheetRequest;
import com.bobcarrental.dto.response.TripSheetResponse;
import com.bobcarrental.dto.response.TripSheetSummaryResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.model.Booking;
import com.bobcarrental.model.TripSheet;
import com.bobcarrental.model.VehicleType;
import com.bobcarrental.repository.BookingRepository;
import com.bobcarrental.repository.TripSheetRepository;
import com.bobcarrental.repository.VehicleTypeRepository;
import com.bobcarrental.service.FareCalculationService;
import com.bobcarrental.service.FareCalculationService.*;
import com.bobcarrental.service.TripSheetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TripSheetService
 * Integrates with FareCalculationService for fare calculations
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TripSheetServiceImpl implements TripSheetService {

    private final TripSheetRepository tripSheetRepository;
    private final BookingRepository bookingRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final TripSheetMapper tripSheetMapper;
    private final FareCalculationService fareCalculationService;

    @Override
    public TripSheetResponse createTripSheet(TripSheetRequest request) {
        log.info("Creating new trip sheet: {}", request.getTrpNum());

        // Validate trip number uniqueness
        if (tripSheetRepository.findByTrpNum(request.getTrpNum()).isPresent()) {
            throw new ValidationException("Trip number already exists: " + request.getTrpNum());
        }

        // Note: bookingId is optional reference field (string), not a foreign key
        // It's used for reference purposes only and not validated against Booking table
        
        TripSheet tripSheet = tripSheetMapper.toEntity(request);
        
        // Calculate days automatically from start and end dates
        if (tripSheet.getStartDt() != null && tripSheet.getEndDt() != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                tripSheet.getStartDt(),
                tripSheet.getEndDt()
            ) + 1; // +1 to include both start and end day
            tripSheet.setDays((int) daysBetween);
        }
        
        // Auto-calculate fare if all required fields are present
        // This mimics legacy TripProcess() function from TRIPMAS.PRG line 232
        if (shouldCalculateFare(tripSheet)) {
            try {
                calculateAndSetFareSimple(tripSheet);
                log.info("Fare calculated automatically for trip sheet: {}", request.getTrpNum());
            } catch (Exception e) {
                log.warn("Could not auto-calculate fare: {}. Trip sheet will be saved without fare calculation.", e.getMessage());
                // Continue saving without fare - user can calculate manually later
            }
        }
        
        TripSheet savedTripSheet = tripSheetRepository.save(tripSheet);
        log.info("Trip sheet created successfully with id: {}", savedTripSheet.getId());

        return tripSheetMapper.toResponse(savedTripSheet);
    }

    @Override
    public TripSheetResponse updateTripSheet(Long id, TripSheetRequest request) {
        log.info("Updating trip sheet with id: {}", id);

        TripSheet tripSheet = tripSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "id", id));

        // Check if trip sheet can be modified (if already billed)
        if (Boolean.TRUE.equals(tripSheet.getIsBilled())) {
            throw new ValidationException("Cannot update trip sheet that has been billed");
        }

        // Validate trip number uniqueness (if changed)
        if (!tripSheet.getTrpNum().equals(request.getTrpNum())) {
            if (tripSheetRepository.findByTrpNum(request.getTrpNum()).isPresent()) {
                throw new ValidationException("Trip number already exists: " + request.getTrpNum());
            }
        }

        // Note: bookingId is optional reference field (string), not validated

        // Update trip sheet fields
        tripSheetMapper.updateEntityFromRequest(request, tripSheet);
        
        // Calculate days automatically from start and end dates
        if (tripSheet.getStartDt() != null && tripSheet.getEndDt() != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                tripSheet.getStartDt(),
                tripSheet.getEndDt()
            ) + 1; // +1 to include both start and end day
            tripSheet.setDays((int) daysBetween);
        }

        TripSheet updatedTripSheet = tripSheetRepository.save(tripSheet);
        log.info("Trip sheet updated successfully with id: {}", updatedTripSheet.getId());

        return tripSheetMapper.toResponse(updatedTripSheet);
    }

    @Override
    @Transactional(readOnly = true)
    public TripSheetResponse getTripSheetById(Long id) {
        log.info("Fetching trip sheet with id: {}", id);

        TripSheet tripSheet = tripSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "id", id));

        return tripSheetMapper.toResponse(tripSheet);
    }

    @Override
    @Transactional(readOnly = true)
    public TripSheetResponse getTripSheetByTripNo(String tripNo) {
        log.info("Fetching trip sheet with trip number: {}", tripNo);

        TripSheet tripSheet = tripSheetRepository.findByTrpNum(Integer.parseInt(tripNo))
                .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "tripNo", tripNo));

        return tripSheetMapper.toResponse(tripSheet);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripSheetSummaryResponse> getAllTripSheets(Pageable pageable) {
        log.info("Fetching all trip sheets with pagination");

        return tripSheetRepository.findAll(pageable)
                .map(tripSheetMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripSheetSummaryResponse> getTripSheetsByBooking(Long bookingId) {
        log.info("Fetching trip sheets for booking id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        // Find trip sheets by client ID and date range around booking date
        // Since there's no direct FK relationship, we search by common fields
        return tripSheetRepository.findByClientId(booking.getClientId()).stream()
                .filter(ts -> ts.getTrpDate().equals(booking.getBookDate()) ||
                             (ts.getStartDt() != null && !ts.getStartDt().isAfter(booking.getBookDate()) &&
                              ts.getEndDt() != null && !ts.getEndDt().isBefore(booking.getBookDate())))
                .map(tripSheetMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripSheetSummaryResponse> getTripSheetsByStatus(String status) {
        log.info("Fetching trip sheets with status: {}", status);

        return tripSheetRepository.findByStatus(status).stream()
                .map(tripSheetMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripSheetSummaryResponse> getTripSheetsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching trip sheets between {} and {}", startDate, endDate);

        return tripSheetRepository.findByStartDtBetween(startDate, endDate).stream()
                .map(tripSheetMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TripSheetResponse startTrip(Long id) {
        log.info("Starting trip with id: {}", id);

        TripSheet tripSheet = tripSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "id", id));

        if (!"OPEN".equals(tripSheet.getStatus())) {
            throw new ValidationException("Only OPEN trips can be started");
        }

        tripSheet.setStatus("STARTED");
        TripSheet updatedTripSheet = tripSheetRepository.save(tripSheet);

        log.info("Trip started successfully with id: {}", updatedTripSheet.getId());
        return tripSheetMapper.toResponse(updatedTripSheet);
    }

    @Override
    public TripSheetResponse finishTrip(Long id, Double endKm, LocalDate endDate, LocalTime endTime) {
        log.info("Finishing trip with id: {}", id);

        TripSheet tripSheet = tripSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "id", id));

        if ("FINISHED".equals(tripSheet.getStatus())) {
            throw new ValidationException("Trip is already finished");
        }

        // Update end details - convert types to match entity
        tripSheet.setEndKm(endKm.intValue());
        tripSheet.setEndDt(endDate);
        tripSheet.setEndTm(endTime.toString());
        tripSheet.setStatus("FINISHED");

        // Calculate fare
        calculateAndSetFare(tripSheet);

        TripSheet updatedTripSheet = tripSheetRepository.save(tripSheet);

        log.info("Trip finished successfully with id: {}", updatedTripSheet.getId());
        return tripSheetMapper.toResponse(updatedTripSheet);
    }

    @Override
    public TripSheetResponse calculateFare(Long id) {
        log.info("Calculating fare for trip sheet with id: {}", id);

        TripSheet tripSheet = tripSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "id", id));

        if (tripSheet.getEndKm() == null || tripSheet.getEndKm() == 0) {
            throw new ValidationException("Cannot calculate fare: End kilometer reading not recorded");
        }

        calculateAndSetFare(tripSheet);

        TripSheet updatedTripSheet = tripSheetRepository.save(tripSheet);
        return tripSheetMapper.toResponse(updatedTripSheet);
    }

    /**
     * Calculate and set fare for a trip sheet
     * Uses FareCalculationService with legacy algorithms
     * 
     * @param tripSheet Trip sheet to calculate fare for
     */
    private void calculateAndSetFare(TripSheet tripSheet) {
        log.info("Calculating fare for trip: {}", tripSheet.getTrpNum());

        // Get vehicle type from booking
        VehicleType vehicleType = tripSheet.getBooking().getVehicleType();

        // Calculate total kilometers and hours
        double totalKm = tripSheet.getEndKm() - tripSheet.getStartKm();
        double totalHours = fareCalculationService.calculateTotalHours(
                tripSheet.getStartDate(), LocalTime.parse(tripSheet.getStartTime()),
                tripSheet.getEndDate(), LocalTime.parse(tripSheet.getEndTime())
        );
        int totalDays = (int) (tripSheet.getEndDate().toEpochDay() - tripSheet.getStartDate().toEpochDay()) + 1;

        log.debug("Trip details: km={}, hours={}, days={}", totalKm, totalHours, totalDays);

        // Determine fare type based on trip characteristics
        // This logic mimics the legacy TripProcess() function
        String fareType = determineFareType(totalKm, totalDays, totalHours);

        switch (fareType) {
            case "LOCAL": // Flat rate
                LocalFareResult localResult = fareCalculationService.calculateLocalFare(totalHours, vehicleType);
                double extraKmCharge = fareCalculationService.calculateExtraKmCharges(
                        totalKm, localResult.getFreeKm(), localResult.getPerKmRate()
                );
                
                tripSheet.setLocalKm(totalKm);
                tripSheet.setOutKm(0.0);
                tripSheet.setLocalAmount(localResult.getBasicCharge() + extraKmCharge);
                tripSheet.setOutAmount(0.0);
                break;

            case "OUTSTATION":
                OutstationFareResult outstationResult = fareCalculationService.calculateOutstationFare(
                        totalKm, totalDays, vehicleType
                );
                
                tripSheet.setLocalKm(0.0);
                tripSheet.setOutKm(totalKm);
                tripSheet.setLocalAmount(0.0);
                tripSheet.setOutAmount(outstationResult.getMinimumCharge() + outstationResult.getExtraKmCharge());
                // Night halt charges would be added separately
                break;

            case "SPLIT": // Hire + Fuel
                SplitFareResult splitResult = fareCalculationService.calculateSplitFare(
                        totalHours, totalKm, vehicleType
                );
                
                tripSheet.setLocalKm(totalKm);
                tripSheet.setOutKm(0.0);
                tripSheet.setLocalAmount(splitResult.getBasicCharge() + splitResult.getFuelCharge());
                tripSheet.setOutAmount(splitResult.getExtraKmCharge());
                break;
        }
        
        log.info("Fare calculated - Local: {}, Out: {}",
                tripSheet.getLocalAmount(), tripSheet.getOutAmount());
    }

    /**
     * Check if trip sheet has all required fields for fare calculation
     */
    private boolean shouldCalculateFare(TripSheet tripSheet) {
        return tripSheet.getStartKm() != null && tripSheet.getStartKm() > 0
            && tripSheet.getEndKm() != null && tripSheet.getEndKm() > 0
            && tripSheet.getStartDt() != null && tripSheet.getEndDt() != null
            && tripSheet.getStartTm() != null && tripSheet.getEndTm() != null
            && tripSheet.getTypeId() != null && !tripSheet.getTypeId().isEmpty()
            && tripSheet.getStatus() != null && !tripSheet.getStatus().isEmpty();
    }

    /**
     * Simplified fare calculation that doesn't depend on Booking
     * Uses StandardFare table directly based on typeId
     */
    private void calculateAndSetFareSimple(TripSheet tripSheet) {
        // Calculate total kilometers
        int totalKm = tripSheet.getEndKm() - tripSheet.getStartKm();
        
        // Calculate total hours
        double totalHours = fareCalculationService.calculateTotalHours(
            tripSheet.getStartDt(), 
            java.time.LocalTime.parse(tripSheet.getStartTm()),
            tripSheet.getEndDt(), 
            java.time.LocalTime.parse(tripSheet.getEndTm())
        );
        
        // Get vehicle type
        VehicleType vehicleType = vehicleTypeRepository.findByTypeId(tripSheet.getTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "typeId", tripSheet.getTypeId()));
        
        // Calculate based on status (fare type)
        switch (tripSheet.getStatus()) {
            case "F": // Flat Rate
                FareCalculationService.LocalFareResult localResult =
                    fareCalculationService.calculateLocalFare(totalHours, vehicleType);
                double extraKm = Math.max(0, totalKm - localResult.getFreeKm());
                
                tripSheet.setHiring(BigDecimal.valueOf(localResult.getBasicCharge()));
                tripSheet.setExtra(BigDecimal.valueOf(extraKm * localResult.getPerKmRate()));
                tripSheet.setMinimum(BigDecimal.ZERO);
                tripSheet.setHalt(BigDecimal.ZERO);
                break;
                
            case "S": // Split Rate
                FareCalculationService.SplitFareResult splitResult =
                    fareCalculationService.calculateSplitFare(totalKm, totalHours, vehicleType);
                
                tripSheet.setHiring(BigDecimal.valueOf(splitResult.getBasicCharge()));
                tripSheet.setMinimum(BigDecimal.valueOf(splitResult.getFuelCharge()));
                tripSheet.setExtra(BigDecimal.valueOf(splitResult.getExtraKmCharge()));
                tripSheet.setHalt(BigDecimal.ZERO);
                break;
                
            case "O": // Outstation
                FareCalculationService.OutstationFareResult outResult =
                    fareCalculationService.calculateOutstationFare(totalKm, tripSheet.getDays(), vehicleType);
                
                tripSheet.setHiring(BigDecimal.ZERO);
                tripSheet.setExtra(BigDecimal.valueOf(outResult.getExtraKmCharge()));
                tripSheet.setMinimum(BigDecimal.valueOf(outResult.getMinimumCharge()));
                tripSheet.setHalt(BigDecimal.valueOf(outResult.getNightHaltCharge()));
                break;
                
            default:
                log.warn("Unknown status type: {}", tripSheet.getStatus());
        }
        
        // Ensure permit and misc are not null
        if (tripSheet.getPermit() == null) {
            tripSheet.setPermit(BigDecimal.ZERO);
        }
        if (tripSheet.getMisc() == null) {
            tripSheet.setMisc(BigDecimal.ZERO);
        }
        
        log.info("Fare calculated: hiring={}, extra={}, minimum={}, halt={}, total={}", 
            tripSheet.getHiring(), tripSheet.getExtra(), tripSheet.getMinimum(), 
            tripSheet.getHalt(), tripSheet.getTotalAmount());
    }

    /**
     * Determine fare type based on trip characteristics
     * Mimics legacy logic from TripProcess()
     * 
     * @param totalKm Total kilometers
     * @param totalDays Total days
     * @param totalHours Total hours
     * @return Fare type (LOCAL, OUTSTATION, SPLIT)
     */
    private String determineFareType(double totalKm, int totalDays, double totalHours) {
        // Simple heuristic - can be enhanced based on business rules
        if (totalDays > 1 && totalKm > 200) {
            return "OUTSTATION";
        } else if (totalHours <= 12) {
            return "LOCAL";
        } else {
            return "SPLIT";
        }
    }

    @Override
    public void deleteTripSheet(Long id) {
        log.info("Deleting trip sheet with id: {}", id);

        TripSheet tripSheet = tripSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "id", id));

        if ("FINISHED".equals(tripSheet.getStatus())) {
            throw new ValidationException("Cannot delete finished trip sheet");
        }

        tripSheetRepository.delete(tripSheet);
        log.info("Trip sheet deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTripNo(String tripNo) {
        return tripSheetRepository.findByTrpNum(Integer.parseInt(tripNo)).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripSheetSummaryResponse> getActiveTripSheets() {
        log.info("Fetching active trip sheets");

        return tripSheetRepository.findAll().stream()
                .filter(ts -> "OPEN".equals(ts.getStatus()) || "STARTED".equals(ts.getStatus()))
                .map(tripSheetMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripSheetSummaryResponse> getCompletedTripSheets() {
        log.info("Fetching completed trip sheets");

        return tripSheetRepository.findByStatus("FINISHED").stream()
                .map(tripSheetMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripSheetSummaryResponse> getUnbilledTripSheets() {
        log.info("Fetching unbilled trip sheets");

        // This would need a field in TripSheet entity to track billing status
        // For now, return all finished trips
        return getCompletedTripSheets();
    }
}

// Made with Bob
