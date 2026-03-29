import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Client, ClientRequest, ClientResponse, ApiResponse } from '../../models';

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
export class ClientService {
  private readonly API_URL = `${environment.apiUrl}/clients`;

  constructor(private http: HttpClient) {}

  /**
   * Get all clients
   */
  getAllClients(): Observable<ClientResponse[]> {
    return this.http.get<ApiResponse<PageResponse<ClientResponse>>>(this.API_URL).pipe(
      map(response => response.data.content)
    );
  }

  /**
   * Get client by ID
   */
  getClientById(id: number): Observable<ClientResponse> {
    return this.http.get<ApiResponse<ClientResponse>>(`${this.API_URL}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Search clients by name
   */
  searchClients(name: string): Observable<ClientResponse[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<ApiResponse<ClientResponse[]>>(`${this.API_URL}/search`, { params }).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get active clients only
   */
  getActiveClients(): Observable<ClientResponse[]> {
    return this.http.get<ApiResponse<ClientResponse[]>>(`${this.API_URL}/active`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Create new client
   */
  createClient(request: ClientRequest): Observable<ClientResponse> {
    return this.http.post<ApiResponse<ClientResponse>>(this.API_URL, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Update existing client
   */
  updateClient(id: number, request: ClientRequest): Observable<ClientResponse> {
    return this.http.put<ApiResponse<ClientResponse>>(`${this.API_URL}/${id}`, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Delete client
   */
  deleteClient(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get client statistics
   */
  getClientStats(clientId: number): Observable<any> {
    return this.http.get<ApiResponse<any>>(`${this.API_URL}/${clientId}/stats`).pipe(
      map(response => response.data)
    );
  }
}

// Made with Bob
