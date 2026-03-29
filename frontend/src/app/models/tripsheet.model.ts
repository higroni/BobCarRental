/**
 * TripSheet model matching backend TripSheet entity and DTOs
 * Most complex entity with fare calculations
 */

export interface TripSheetRequest {
  trpNum: number;
  trpDate: string;
  regNum: string;
  driver?: string;
  bookingId?: number;
  clientId: string;
  startKm: number;
  endKm: number;
  typeId: string;
  startDate: string;
  endDate?: string;
  startTime?: string;
  endTime?: string;
  isBilled?: boolean;
  billNumber?: number;
  billDate?: string;
  status?: 'F' | 'S' | 'O'; // F=Flat, S=Split, O=Outstation
  hiring?: number;
  extra?: number;
  halt?: number;
  minimum?: number;
  time?: number;
  days?: number;
  permit?: number;
  misc?: number;
  remarks?: string;
  tagged?: boolean;
}

export interface TripSheetResponse {
  id: number;
  trpNum: number;
  clientName?: string;
  trpDate: string;
  regNum: string;
  startKm: number;
  endKm: number;
  typeId: string;
  typeName?: string;
  startDate: string;
  endDate?: string;
  startTime?: string;
  endTime?: string;
  driver?: string;
  clientId: string;
  isBilled: boolean;
  billNumber?: number;
  billDate?: string;
  status?: string;
  hiring?: number;
  extra?: number;
  halt?: number;
  minimum?: number;
  time?: number;
  days?: number;
  permit?: number;
  misc?: number;
  remarks?: string;
  tagged?: boolean;
  totalKm?: number;
  totalAmount?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface TripSheetSummaryResponse {
  id: number;
  trpNum: number;
  trpDate: string;
  clientName?: string;
  clientId: string;
  regNum: string;
  typeName?: string;
  typeId: string;
  totalKm?: number;
  totalAmount?: number;
  isBilled: boolean;
  billNum?: number;
  status?: string;
}

export type TripStatus = 'F' | 'S' | 'O';

export const TRIP_STATUS_LABELS: Record<TripStatus, string> = {
  'F': 'Flat Rate',
  'S': 'Split Rate',
  'O': 'Outstation'
};

// Made with Bob
