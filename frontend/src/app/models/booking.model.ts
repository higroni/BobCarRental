/**
 * Booking model matching backend Booking entity and DTOs
 */

export interface Booking {
  id: number;
  bookNum: string;
  bookDate: string;
  clientId: number;
  clientName?: string;
  vehicleTypeId: number;
  vehicleTypeName?: string;
  startDate: string;
  endDate: string;
  startTime?: string;
  endTime?: string;
  pickupLocation?: string;
  dropLocation?: string;
  estimatedKm?: number;
  estimatedAmount?: number;
  advanceAmount?: number;
  remarks?: string;
  status: BookingStatus;
  createdAt?: Date;
  updatedAt?: Date;
}

export enum BookingStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export const BOOKING_STATUS_LABELS: Record<BookingStatus, string> = {
  [BookingStatus.PENDING]: 'Pending',
  [BookingStatus.CONFIRMED]: 'Confirmed',
  [BookingStatus.IN_PROGRESS]: 'In Progress',
  [BookingStatus.COMPLETED]: 'Completed',
  [BookingStatus.CANCELLED]: 'Cancelled'
};

export interface BookingSummaryResponse {
  id: number;
  bookDate: string;
  time?: string;
  ref?: string;
  typeId: string;
  typeName?: string;
  clientId: string;
  clientName?: string;
  info1?: string;
  tagged?: boolean;
  status?: string;
}

export interface BookingRequest {
  bookDate: string;
  todayDate: string;
  time: string;
  ref?: string;
  typeId: string;
  clientId: string;
  info1?: string;
  info2?: string;
  info3?: string;
  info4?: string;
  tagged?: boolean;
}

export interface BookingResponse {
  id: number;
  bookDate: string;
  todayDate: string;
  time?: string;
  ref?: string;
  typeId: string;
  clientId: string;
  info1?: string;
  info2?: string;
  info3?: string;
  info4?: string;
  tagged?: boolean;
  fullInfo?: string;
  createdAt: string;
  updatedAt: string;
}

// Made with Bob
