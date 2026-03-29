import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { StandardFareRequest, StandardFareResponse } from '../../models/fare.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

@Injectable({
  providedIn: 'root'
})
export class StandardFareService {
  private apiUrl = `${environment.apiUrl}/standard-fares`;

  constructor(private http: HttpClient) {}

  getAllStandardFares(page: number = 0, size: number = 100): Observable<StandardFareResponse[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PageResponse<StandardFareResponse>>>(this.apiUrl, { params })
      .pipe(
        map(response => response.data.content)
      );
  }

  getStandardFareById(id: number): Observable<StandardFareResponse> {
    return this.http.get<ApiResponse<StandardFareResponse>>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => response.data)
      );
  }

  createStandardFare(request: StandardFareRequest): Observable<StandardFareResponse> {
    return this.http.post<ApiResponse<StandardFareResponse>>(this.apiUrl, request)
      .pipe(
        map(response => response.data)
      );
  }

  updateStandardFare(id: number, request: StandardFareRequest): Observable<StandardFareResponse> {
    return this.http.put<ApiResponse<StandardFareResponse>>(`${this.apiUrl}/${id}`, request)
      .pipe(
        map(response => response.data)
      );
  }

  deleteStandardFare(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(
        map(() => undefined)
      );
  }

  searchStandardFares(vehicleCode?: string, fareType?: string): Observable<StandardFareResponse[]> {
    let params = new HttpParams();
    if (vehicleCode) {
      params = params.set('vehicleCode', vehicleCode);
    }
    if (fareType) {
      params = params.set('fareType', fareType);
    }

    return this.http.get<ApiResponse<StandardFareResponse[]>>(`${this.apiUrl}/search`, { params })
      .pipe(
        map(response => response.data)
      );
  }
}

// Made with Bob