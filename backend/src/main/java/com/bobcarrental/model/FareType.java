package com.bobcarrental.model;

/**
 * Fare type enumeration
 * Defines different types of fares in the system
 */
public enum FareType {
    LOCAL,      // Hourly rates with free KM
    EXTRA,      // Extra hour rates
    GENERAL,    // Fuel and excess KM rates
    OUTSTATION, // Per KM rates for outstation trips
    FULL_DAY,   // Full day rental
    HALF_DAY,   // Half day rental
    PER_KM      // Per kilometer rate
}

// Made with Bob