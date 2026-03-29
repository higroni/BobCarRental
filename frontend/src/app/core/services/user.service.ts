import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { UserRequest, UserResponse } from '../../models/user.model';

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp: string;
}

interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = '/api/v1/users';

  constructor(private http: HttpClient) {}

  getAllUsers(page: number = 0, size: number = 10, sortBy: string = 'username', sortDir: string = 'ASC'): Observable<PageResponse<UserResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<ApiResponse<PageResponse<UserResponse>>>(this.apiUrl, { params })
      .pipe(map(response => response.data));
  }

  getAllUsersNoPagination(): Observable<UserResponse[]> {
    return this.http.get<ApiResponse<UserResponse[]>>(`${this.apiUrl}/all`)
      .pipe(map(response => response.data));
  }

  getUserById(id: number): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  createUser(request: UserRequest): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(this.apiUrl, request)
      .pipe(map(response => response.data));
  }

  updateUser(id: number, request: UserRequest): Observable<UserResponse> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.apiUrl}/${id}`, request)
      .pipe(map(response => response.data));
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(map(() => undefined));
  }

  checkUsername(username: string): Observable<boolean> {
    const params = new HttpParams().set('username', username);
    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/check-username`, { params })
      .pipe(map(response => response.data));
  }

  checkEmail(email: string): Observable<boolean> {
    const params = new HttpParams().set('email', email);
    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/check-email`, { params })
      .pipe(map(response => response.data));
  }
}

// Made with Bob