import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { VehicleTypeRequest, VehicleTypeResponse, ApiResponse } from '../../models';

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
export class VehicleTypeService {
  private readonly API_URL = `${environment.apiUrl}/vehicle-types`;

  constructor(private http: HttpClient) {}

  getAllVehicleTypes(): Observable<VehicleTypeResponse[]> {
    return this.http.get<ApiResponse<PageResponse<VehicleTypeResponse>>>(this.API_URL).pipe(
      map(response => response.data.content)
    );
  }

  getVehicleTypeById(id: number): Observable<VehicleTypeResponse> {
    return this.http.get<ApiResponse<VehicleTypeResponse>>(`${this.API_URL}/${id}`).pipe(
      map(response => response.data)
    );
  }

  createVehicleType(request: VehicleTypeRequest): Observable<VehicleTypeResponse> {
    return this.http.post<ApiResponse<VehicleTypeResponse>>(this.API_URL, request).pipe(
      map(response => response.data)
    );
  }

  updateVehicleType(id: number, request: VehicleTypeRequest): Observable<VehicleTypeResponse> {
    return this.http.put<ApiResponse<VehicleTypeResponse>>(`${this.API_URL}/${id}`, request).pipe(
      map(response => response.data)
    );
  }

  deleteVehicleType(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map(() => undefined)
    );
  }
}

// Made with Bob