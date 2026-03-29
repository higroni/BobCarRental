import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AddressRequest, AddressResponse, ApiResponse } from '../../models';

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
export class AddressService {
  private readonly API_URL = `${environment.apiUrl}/addresses`;

  constructor(private http: HttpClient) {}

  getAllAddresses(): Observable<AddressResponse[]> {
    return this.http.get<ApiResponse<PageResponse<AddressResponse>>>(this.API_URL).pipe(
      map(response => response.data.content)
    );
  }

  getAddressById(id: number): Observable<AddressResponse> {
    return this.http.get<ApiResponse<AddressResponse>>(`${this.API_URL}/${id}`).pipe(
      map(response => response.data)
    );
  }

  createAddress(request: AddressRequest): Observable<AddressResponse> {
    return this.http.post<ApiResponse<AddressResponse>>(this.API_URL, request).pipe(
      map(response => response.data)
    );
  }

  updateAddress(id: number, request: AddressRequest): Observable<AddressResponse> {
    return this.http.put<ApiResponse<AddressResponse>>(`${this.API_URL}/${id}`, request).pipe(
      map(response => response.data)
    );
  }

  deleteAddress(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map(() => undefined)
    );
  }
}

// Made with Bob