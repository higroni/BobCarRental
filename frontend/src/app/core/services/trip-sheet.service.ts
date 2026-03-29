import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TripSheetResponse, TripSheetRequest, TripSheetSummaryResponse, ApiResponse } from '../../models';

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
export class TripSheetService {
  private apiUrl = `${environment.apiUrl}/trip-sheets`;

  constructor(private http: HttpClient) {}

  /**
   * Get paginated list of trip sheets
   */
  getTripSheets(page: number = 0, size: number = 20, sort: string = 'trpDate,desc'): Observable<TripSheetSummaryResponse[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<ApiResponse<PageResponse<TripSheetSummaryResponse>>>(this.apiUrl, { params }).pipe(
      map(response => response.data.content)
    );
  }

  /**
   * Get trip sheet by ID
   */
  getTripSheetById(id: number): Observable<TripSheetResponse> {
    return this.http.get<ApiResponse<TripSheetResponse>>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get trip sheet by trip number
   */
  getTripSheetByNumber(trpNum: number): Observable<TripSheetResponse> {
    return this.http.get<ApiResponse<TripSheetResponse>>(`${this.apiUrl}/by-number/${trpNum}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get trip sheets by client ID
   */
  getTripSheetsByClient(clientId: string): Observable<TripSheetSummaryResponse[]> {
    return this.http.get<ApiResponse<TripSheetSummaryResponse[]>>(`${this.apiUrl}/by-client/${clientId}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get trip sheets by date range
   */
  getTripSheetsByDateRange(startDate: string, endDate: string): Observable<TripSheetSummaryResponse[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<ApiResponse<TripSheetSummaryResponse[]>>(`${this.apiUrl}/by-date-range`, { params }).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get unbilled trip sheets
   */
  getUnbilledTripSheets(): Observable<TripSheetSummaryResponse[]> {
    return this.http.get<ApiResponse<TripSheetSummaryResponse[]>>(`${this.apiUrl}/unbilled`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get trip sheets by bill number
   */
  getTripSheetsByBillNumber(billNum: number): Observable<TripSheetSummaryResponse[]> {
    return this.http.get<ApiResponse<TripSheetSummaryResponse[]>>(`${this.apiUrl}/by-bill/${billNum}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Create new trip sheet
   */
  createTripSheet(request: TripSheetRequest): Observable<TripSheetResponse> {
    return this.http.post<ApiResponse<TripSheetResponse>>(this.apiUrl, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Update existing trip sheet
   */
  updateTripSheet(id: number, request: TripSheetRequest): Observable<TripSheetResponse> {
    return this.http.put<ApiResponse<TripSheetResponse>>(`${this.apiUrl}/${id}`, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Delete trip sheet (ADMIN only)
   */
  deleteTripSheet(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Mark trip sheet as billed
   */
  markAsBilled(id: number, billNum: number, billDate: string): Observable<TripSheetResponse> {
    const params = new HttpParams()
      .set('billNum', billNum.toString())
      .set('billDate', billDate);

    return this.http.patch<ApiResponse<TripSheetResponse>>(`${this.apiUrl}/${id}/mark-billed`, {}, { params }).pipe(
      map(response => response.data)
    );
  }

  /**
   * Calculate fare for trip sheet
   */
  calculateFare(request: TripSheetRequest): Observable<TripSheetResponse> {
    return this.http.post<ApiResponse<TripSheetResponse>>(`${this.apiUrl}/calculate-fare`, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Validate trip number uniqueness
   */
  validateTripNumber(trpNum: number): Observable<boolean> {
    const params = new HttpParams().set('trpNum', trpNum.toString());
    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/validate/trip-number`, { params }).pipe(
      map(response => response.data)
    );
  }
}

// Made with Bob