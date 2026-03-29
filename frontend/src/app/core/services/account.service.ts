import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AccountRequest, AccountResponse, ApiResponse } from '../../models';

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
export class AccountService {
  private readonly API_URL = `${environment.apiUrl}/accounts`;

  constructor(private http: HttpClient) {}

  getAllAccounts(): Observable<AccountResponse[]> {
    return this.http.get<ApiResponse<PageResponse<AccountResponse>>>(this.API_URL).pipe(
      map(response => response.data.content)
    );
  }

  getAccountById(id: number): Observable<AccountResponse> {
    return this.http.get<ApiResponse<AccountResponse>>(`${this.API_URL}/${id}`).pipe(
      map(response => response.data)
    );
  }

  createAccount(request: AccountRequest): Observable<AccountResponse> {
    return this.http.post<ApiResponse<AccountResponse>>(this.API_URL, request).pipe(
      map(response => response.data)
    );
  }

  updateAccount(id: number, request: AccountRequest): Observable<AccountResponse> {
    return this.http.put<ApiResponse<AccountResponse>>(`${this.API_URL}/${id}`, request).pipe(
      map(response => response.data)
    );
  }

  deleteAccount(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map(() => undefined)
    );
  }

  activateAccount(id: number): Observable<AccountResponse> {
    return this.http.patch<ApiResponse<AccountResponse>>(`${this.API_URL}/${id}/activate`, {}).pipe(
      map(response => response.data)
    );
  }

  deactivateAccount(id: number): Observable<AccountResponse> {
    return this.http.patch<ApiResponse<AccountResponse>>(`${this.API_URL}/${id}/deactivate`, {}).pipe(
      map(response => response.data)
    );
  }
}

// Made with Bob