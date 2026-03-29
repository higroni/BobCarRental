package com.bobcarrental.service;

import com.bobcarrental.model.StandardFare;
import com.bobcarrental.model.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Service for fare calculations
 * Ports legacy algorithms from TRIPMAS.PRG (LocFare, OutFare, Time2Val)
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
public interface FareCalculationService {

    /**
     * Calculate local fare (Flat rate)
     * Ported from LocFare() in TRIPMAS.PRG lines 321-363
     * 
     * @param totalHours Total hours of trip
     * @param vehicleType Vehicle type
     * @return Local fare calculation result
     */
    LocalFareResult calculateLocalFare(double totalHours, VehicleType vehicleType);

    /**
     * Calculate outstation fare
     * Ported from OutFare() in TRIPMAS.PRG lines 367-369
     * 
     * @param totalKm Total kilometers
     * @param totalDays Total days
     * @param vehicleType Vehicle type
     * @return Outstation fare calculation result
     */
    OutstationFareResult calculateOutstationFare(double totalKm, int totalDays, VehicleType vehicleType);

    /**
     * Calculate split fare (Hire + Fuel)
     * Ported from TripProcess() CASE nType==3 in TRIPMAS.PRG lines 283-296
     * 
     * @param totalHours Total hours
     * @param totalKm Total kilometers
     * @param vehicleType Vehicle type
     * @return Split fare calculation result
     */
    SplitFareResult calculateSplitFare(double totalHours, double totalKm, VehicleType vehicleType);

    /**
     * Calculate time difference in hours
     * Ported from Time2Val() function
     * 
     * @param startTime Start time
     * @param endTime End time
     * @param daysDifference Days difference
     * @return Total hours
     */
    double calculateTotalHours(LocalTime startTime, LocalTime endTime, long daysDifference);

    /**
     * Calculate time difference in hours using dates
     * 
     * @param startDate Start date
     * @param startTime Start time
     * @param endDate End date
     * @param endTime End time
     * @return Total hours
     */
    double calculateTotalHours(LocalDate startDate, LocalTime startTime, 
                               LocalDate endDate, LocalTime endTime);

    /**
     * Get standard fare for vehicle type
     * 
     * @param vehicleType Vehicle type
     * @param fareType Fare type (LOCAL or OUTSTATION)
     * @return Standard fare
     */
    StandardFare getStandardFare(VehicleType vehicleType, String fareType);

    /**
     * Result of local fare calculation
     */
    @Data
    @Builder
    class LocalFareResult {
        private double basicCharge;      // Basic hiring charge
        private double freeKm;           // Free kilometers included
        private double extraKmCharge;    // Extra kilometer charges
        private double totalCharge;      // Total charge
        private double perKmRate;        // Rate per extra kilometer
    }

    /**
     * Result of outstation fare calculation
     */
    @Data
    @Builder
    class OutstationFareResult {
        private double minimumCharge;    // Minimum charge (minKm * rate)
        private double extraKmCharge;    // Extra kilometer charges
        private double nightHaltCharge;  // Night halt charges
        private double totalCharge;      // Total charge
        private double perKmRate;        // Rate per kilometer
        private double minKmPerDay;      // Minimum km per day
    }

    /**
     * Result of split fare calculation
     */
    @Data
    @Builder
    class SplitFareResult {
        private double basicCharge;      // Basic hiring charge
        private double fuelCharge;       // Fuel cost (normal km)
        private double extraKmCharge;    // Extra kilometer charges
        private double totalCharge;      // Total charge
        private double fuelKm;           // Kilometers covered by fuel charge
    }
    /**
     * Calculate extra kilometer charges
     *
     * @param totalKm Total kilometers
     * @param freeKm Free kilometers included
     * @param perKmRate Rate per extra kilometer
     * @return Extra kilometer charges
     */
    double calculateExtraKmCharges(double totalKm, double freeKm, double perKmRate);
}

// Made with Bob
