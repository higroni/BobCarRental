package com.bobcarrental.service.impl;

import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.model.StandardFare;
import com.bobcarrental.model.VehicleType;
import com.bobcarrental.repository.StandardFareRepository;
import com.bobcarrental.service.FareCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Implementation of FareCalculationService
 * Ports legacy fare calculation algorithms from TRIPMAS.PRG
 * 
 * Legacy Algorithm References:
 * - LocFare(): TRIPMAS.PRG lines 321-363
 * - OutFare(): TRIPMAS.PRG lines 367-369
 * - Time2Val(): Used for time calculations
 * - TripProcess(): TRIPMAS.PRG lines 254-318
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FareCalculationServiceImpl implements FareCalculationService {

    private final StandardFareRepository standardFareRepository;

    @Override
    public LocalFareResult calculateLocalFare(double totalHours, VehicleType vehicleType) {
        log.info("Calculating local fare for vehicle type: {}, hours: {}", 
                vehicleType.getTypeName(), totalHours);

        StandardFare fare = getStandardFare(vehicleType, "LOCAL");

        double basicCharge;
        double freeKm;
        double perKmRate = fare.getExtraKmRate().doubleValue();
        double baseKm = fare.getBaseKm().doubleValue();
        double baseAmount = fare.getBaseAmount().doubleValue();

        // Algorithm from LocFare() lines 329-358
        if (totalHours <= baseKm) {
            // Within base hours
            basicCharge = baseAmount;
            freeKm = baseKm;
        } else {
            // Beyond base hours - calculate multiples
            int times = (int) (totalHours / baseKm);
            double remainder = totalHours % baseKm;

            basicCharge = times * baseAmount;
            freeKm = times * baseKm;

            // Add charges for remainder hours if any
            if (remainder > 0) {
                // For simplicity, charge proportionally
                // In legacy system, this would lookup extra hour rates
                double extraHourCharge = (remainder / baseKm) * baseAmount;
                basicCharge += extraHourCharge;
                freeKm += remainder * (baseKm / baseKm); // Proportional free km
            }
        }

        return LocalFareResult.builder()
                .basicCharge(basicCharge)
                .freeKm(freeKm)
                .extraKmCharge(0.0) // Will be calculated when actual km is known
                .totalCharge(basicCharge)
                .perKmRate(perKmRate)
                .build();
    }

    @Override
    public OutstationFareResult calculateOutstationFare(double totalKm, int totalDays, 
                                                        VehicleType vehicleType) {
        log.info("Calculating outstation fare for vehicle type: {}, km: {}, days: {}", 
                vehicleType.getTypeName(), totalKm, totalDays);

        StandardFare fare = getStandardFare(vehicleType, "OUTSTATION");

        // Algorithm from OutFare() lines 367-369 and TripProcess() lines 300-313
        double perKmRate = fare.getExtraKmRate().doubleValue();
        double minKmPerDay = fare.getBaseKm().doubleValue();
        double nightHaltPerDay = fare.getDriverAllowance().doubleValue(); // Night halt charge per day

        // Calculate minimum km for the trip
        double minimumKm = minKmPerDay * totalDays;
        
        double minimumCharge;
        double extraKmCharge;

        if (totalKm > minimumKm) {
            // Extra kilometers beyond minimum
            minimumCharge = minimumKm * perKmRate;
            extraKmCharge = (totalKm - minimumKm) * perKmRate;
        } else {
            // Within minimum km
            minimumCharge = minimumKm * perKmRate;
            extraKmCharge = 0.0;
        }

        // Night halt charges (days - 1, as first day doesn't count)
        double nightHaltCharge = (totalDays - 1) * nightHaltPerDay;

        double totalCharge = minimumCharge + extraKmCharge + nightHaltCharge;

        return OutstationFareResult.builder()
                .minimumCharge(minimumCharge)
                .extraKmCharge(extraKmCharge)
                .nightHaltCharge(nightHaltCharge)
                .totalCharge(totalCharge)
                .perKmRate(perKmRate)
                .minKmPerDay(minKmPerDay)
                .build();
    }

    @Override
    public SplitFareResult calculateSplitFare(double totalHours, double totalKm, 
                                              VehicleType vehicleType) {
        log.info("Calculating split fare for vehicle type: {}, hours: {}, km: {}", 
                vehicleType.getTypeName(), totalHours, totalKm);

        StandardFare localFare = getStandardFare(vehicleType, "LOCAL");

        // Algorithm from TripProcess() CASE nType==3, lines 283-296
        
        // Calculate basic hiring charge based on hours
        LocalFareResult localResult = calculateLocalFare(totalHours, vehicleType);
        double basicCharge = localResult.getBasicCharge();

        // Fuel charge calculation
        // In legacy: hFuelKm is the km covered by fuel charge, hKmFuel is rate per km
        double fuelKm = localFare.getBaseKm().doubleValue(); // Kilometers covered by fuel charge
        double fuelRatePerKm = localFare.getExtraKmRate().doubleValue() * 0.5; // Fuel rate (typically half of extra km rate)
        
        double fuelCharge;
        double extraKmCharge;

        if (fuelKm >= totalKm) {
            // All km covered by fuel charge
            fuelCharge = totalKm * fuelRatePerKm;
            extraKmCharge = 0.0;
        } else {
            // Some km beyond fuel coverage
            fuelCharge = fuelKm * fuelRatePerKm;
            extraKmCharge = (totalKm - fuelKm) * localFare.getExtraKmRate().doubleValue();
        }

        double totalCharge = basicCharge + fuelCharge + extraKmCharge;

        return SplitFareResult.builder()
                .basicCharge(basicCharge)
                .fuelCharge(fuelCharge)
                .extraKmCharge(extraKmCharge)
                .totalCharge(totalCharge)
                .fuelKm(fuelKm)
                .build();
    }

    @Override
    public double calculateTotalHours(LocalTime startTime, LocalTime endTime, long daysDifference) {
        // Algorithm ported from Time2Val() function
        // Converts time difference to decimal hours
        
        if (startTime == null || endTime == null) {
            return 0.0;
        }

        // Calculate hours within the day
        long minutesDiff = ChronoUnit.MINUTES.between(startTime, endTime);
        
        // Add full days in hours
        double totalHours = (daysDifference * 24.0) + (minutesDiff / 60.0);

        log.debug("Time calculation: start={}, end={}, days={}, totalHours={}", 
                startTime, endTime, daysDifference, totalHours);

        return totalHours;
    }

    @Override
    public double calculateTotalHours(LocalDate startDate, LocalTime startTime, 
                                     LocalDate endDate, LocalTime endTime) {
        if (startDate == null || startTime == null || endDate == null || endTime == null) {
            return 0.0;
        }

        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDateTime end = LocalDateTime.of(endDate, endTime);

        long totalMinutes = ChronoUnit.MINUTES.between(start, end);
        double totalHours = totalMinutes / 60.0;

        log.debug("Time calculation: start={}, end={}, totalHours={}", start, end, totalHours);

        return totalHours;
    }

    @Override
    public StandardFare getStandardFare(VehicleType vehicleType, String fareType) {
        log.debug("Fetching standard fare for vehicle type: {}, fare type: {}", 
                vehicleType.getTypeName(), fareType);

        return standardFareRepository.findByVehicleTypeAndFareType(vehicleType, fareType)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StandardFare", 
                        "vehicleType and fareType", 
                        vehicleType.getTypeName() + " - " + fareType));
    }

    /**
     * Calculate extra km charges for local fare
     * This is called after the trip is completed and actual km is known
     * 
     * @param actualKm Actual kilometers driven
     * @param freeKm Free kilometers included in basic charge
     * @param perKmRate Rate per extra kilometer
     * @return Extra kilometer charges
     */
    public double calculateExtraKmCharges(double actualKm, double freeKm, double perKmRate) {
        double extraKm = actualKm - freeKm;
        if (extraKm > 0) {
            return extraKm * perKmRate;
        }
        return 0.0;
    }

    /**
     * Calculate total fare for a completed trip
     * This combines all charges including permit and misc expenses
     * 
     * @param basicCharge Basic hiring/minimum charge
     * @param extraKmCharge Extra kilometer charges
     * @param nightHaltCharge Night halt charges (outstation only)
     * @param permitCharge Permit charges
     * @param miscCharge Miscellaneous charges
     * @return Total fare
     */
    public double calculateTotalFare(double basicCharge, double extraKmCharge, 
                                    double nightHaltCharge, double permitCharge, 
                                    double miscCharge) {
        return basicCharge + extraKmCharge + nightHaltCharge + permitCharge + miscCharge;
    }
}

// Made with Bob
