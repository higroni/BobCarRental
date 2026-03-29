import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, map } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, RefreshTokenRequest, RefreshTokenResponse, User, UserRole, ApiResponse } from '../../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'current_user';

  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  /**
   * Login user with username and password
   */
  login(username: string, password: string): Observable<LoginResponse> {
    const request: LoginRequest = { username, password };
    
    return this.http.post<ApiResponse<LoginResponse>>(`${this.API_URL}/login`, request).pipe(
      map(apiResponse => apiResponse.data),
      tap(response => {
        this.setSession(response);
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  /**
   * Logout current user
   */
  logout(): Observable<any> {
    return this.http.post(`${this.API_URL}/logout`, {}).pipe(
      tap(() => {
        this.clearSession();
        this.isAuthenticatedSubject.next(false);
        this.router.navigate(['/login']);
      })
    );
  }

  /**
   * Refresh access token using refresh token
   */
  refreshToken(): Observable<RefreshTokenResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const request: RefreshTokenRequest = { refreshToken };
    
    return this.http.post<ApiResponse<RefreshTokenResponse>>(`${this.API_URL}/refresh`, request).pipe(
      map(apiResponse => apiResponse.data),
      tap(response => {
        this.setTokens(response.accessToken, response.refreshToken || '');
      })
    );
  }

  /**
   * Get current access token
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Get current refresh token
   */
  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  /**
   * Get current user
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return this.hasValidToken();
  }

  /**
   * Check if user has specific role
   */
  hasRole(role: UserRole): boolean {
    const user = this.getCurrentUser();
    return user?.roles?.includes(role) || false;
  }

  /**
   * Check if user is admin
   */
  isAdmin(): boolean {
    return this.hasRole(UserRole.ADMIN);
  }

  /**
   * Set session data after successful login
   */
  private setSession(response: LoginResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.accessToken);
    if (response.refreshToken) {
      localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
    }
    
    // Backend returns roles as 'ROLE_ADMIN' or 'ROLE_USER'
    const isAdmin = response.user.roles.some((role: string) =>
      role === 'ROLE_ADMIN' || role === 'ADMIN'
    );
    
    const user: User = {
      id: response.user.id,
      username: response.user.username,
      email: response.user.email,
      firstName: response.user.firstName,
      lastName: response.user.lastName,
      fullName: response.user.fullName || `${response.user.firstName} ${response.user.lastName}`.trim() || response.user.username,
      enabled: response.user.enabled,
      accountNonExpired: response.user.accountNonExpired,
      accountNonLocked: response.user.accountNonLocked,
      credentialsNonExpired: response.user.credentialsNonExpired,
      lastLogin: response.user.lastLogin,
      failedLoginAttempts: response.user.failedLoginAttempts,
      roles: response.user.roles,
      createdAt: response.user.createdAt,
      updatedAt: response.user.updatedAt
    };
    
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  /**
   * Set tokens only (for refresh)
   */
  private setTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(this.TOKEN_KEY, accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  /**
   * Clear session data
   */
  private clearSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
  }

  /**
   * Get user from local storage
   */
  private getUserFromStorage(): User | null {
    const userJson = localStorage.getItem(this.USER_KEY);
    if (userJson) {
      try {
        return JSON.parse(userJson);
      } catch (e) {
        return null;
      }
    }
    return null;
  }

  /**
   * Check if token exists and is valid
   */
  private hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    // TODO: Add token expiration check by decoding JWT
    // For now, just check if token exists
    return true;
  }

  /**
   * Decode JWT token (optional - for extracting user info)
   */
  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch (e) {
      return null;
    }
  }
}

// Made with Bob
