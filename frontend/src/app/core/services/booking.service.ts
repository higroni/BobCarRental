import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BookingResponse, BookingRequest, BookingSummaryResponse, ApiResponse } from '../../models';

interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasPrevious: boolean;
  hasNext: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private apiUrl = `${environment.apiUrl}/bookings`;

  constructor(private http: HttpClient) {}

  /**
   * Get paginated list of bookings
   */
  getBookings(page: number = 0, size: number = 20, sort: string = 'bookDate,desc'): Observable<BookingSummaryResponse[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<ApiResponse<PageResponse<BookingSummaryResponse>>>(this.apiUrl, { params }).pipe(
      map(response => response.data.content)
    );
  }

  /**
   * Get booking by ID
   */
  getBookingById(id: number): Observable<BookingResponse> {
    return this.http.get<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get booking by booking number
   */
  getBookingByNumber(bookNum: string): Observable<BookingResponse> {
    return this.http.get<ApiResponse<BookingResponse>>(`${this.apiUrl}/by-number/${bookNum}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get bookings by client ID
   */
  getBookingsByClient(clientId: number): Observable<BookingSummaryResponse[]> {
    return this.http.get<ApiResponse<BookingSummaryResponse[]>>(`${this.apiUrl}/by-client/${clientId}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get bookings by status
   */
  getBookingsByStatus(status: string): Observable<BookingSummaryResponse[]> {
    return this.http.get<ApiResponse<BookingSummaryResponse[]>>(`${this.apiUrl}/by-status/${status}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get bookings by date range
   */
  getBookingsByDateRange(startDate: string, endDate: string): Observable<BookingSummaryResponse[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<ApiResponse<BookingSummaryResponse[]>>(`${this.apiUrl}/by-date-range`, { params }).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get upcoming bookings
   */
  getUpcomingBookings(): Observable<BookingSummaryResponse[]> {
    return this.http.get<ApiResponse<BookingSummaryResponse[]>>(`${this.apiUrl}/upcoming`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Create new booking
   */
  createBooking(request: BookingRequest): Observable<BookingResponse> {
    return this.http.post<ApiResponse<BookingResponse>>(this.apiUrl, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Update existing booking
   */
  updateBooking(id: number, request: BookingRequest): Observable<BookingResponse> {
    return this.http.put<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}`, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Cancel booking
   */
  cancelBooking(id: number): Observable<BookingResponse> {
    return this.http.patch<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}/cancel`, {}).pipe(
      map(response => response.data)
    );
  }

  /**
   * Confirm booking (ADMIN only)
   */
  confirmBooking(id: number): Observable<BookingResponse> {
    return this.http.patch<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}/confirm`, {}).pipe(
      map(response => response.data)
    );
  }

  /**
   * Complete booking (ADMIN only)
   */
  completeBooking(id: number): Observable<BookingResponse> {
    return this.http.patch<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}/complete`, {}).pipe(
      map(response => response.data)
    );
  }

  /**
   * Delete booking (ADMIN only)
   */
  deleteBooking(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Validate booking number
   */
  validateBookingNumber(bookNum: string): Observable<boolean> {
    const params = new HttpParams().set('bookingNo', bookNum);
    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/validate/booking-no`, { params }).pipe(
      map(response => response.data)
    );
  }

  /**
   * Check vehicle availability
   */
  checkVehicleAvailability(vehicleTypeId: string, fromDate: string, toDate: string): Observable<boolean> {
    const params = new HttpParams()
      .set('vehicleTypeId', vehicleTypeId)
      .set('fromDate', fromDate)
      .set('toDate', toDate);

    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/check-availability`, { params }).pipe(
      map(response => response.data)
    );
  }
}

// Made with Bob