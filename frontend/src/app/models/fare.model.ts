/**
 * Standard Fare model matching backend StandardFare entity and DTOs
 */

export interface StandardFare {
  id?: number;
  vehicleCode: string;
  fareType: string;
  hours?: number;
  rate?: number;
  freeKm?: number;
  splitFare?: number;
  splitFuelKm?: number;
  hireRate?: number;
  hireKm?: number;
  fuelRatePerKm?: number;
  ratePerExcessKm?: number;
  ratePerKm?: number;
  minKmPerDay?: number;
  nightHalt?: number;
  active?: boolean;
  isActive?: boolean;
  effectiveFrom?: string;
  effectiveTo?: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface StandardFareRequest {
  vehicleCode: string;
  fareType: string;
  hours?: number;
  rate?: number;
  freeKm?: number;
  splitFare?: number;
  splitFuelKm?: number;
  hireRate?: number;
  hireKm?: number;
  fuelRatePerKm?: number;
  ratePerExcessKm?: number;
  ratePerKm?: number;
  minKmPerDay?: number;
  nightHalt?: number;
  active?: boolean;
  isActive?: boolean;
  effectiveFrom?: string;
  effectiveTo?: string;
  description?: string;
}

export interface StandardFareResponse {
  id: number;
  vehicleCode: string;
  fareType: string;
  hours?: number;
  rate?: number;
  freeKm?: number;
  splitFare?: number;
  splitFuelKm?: number;
  hireRate?: number;
  hireKm?: number;
  fuelRatePerKm?: number;
  ratePerExcessKm?: number;
  ratePerKm?: number;
  minKmPerDay?: number;
  nightHalt?: number;
  active: boolean;
  isActive: boolean;
  effectiveFrom?: string;
  effectiveTo?: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

// Fare types as constants
export const FARE_TYPES = {
  LOCAL: 'LOCAL',
  EXTRA: 'EXTRA',
  GENERAL: 'GENERAL',
  OUTSTATION: 'OUTSTATION'
} as const;

export type FareType = typeof FARE_TYPES[keyof typeof FARE_TYPES];

// Made with Bob
