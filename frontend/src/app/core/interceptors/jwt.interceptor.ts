import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

/**
 * JWT Interceptor - Adds JWT token to all HTTP requests
 * Also handles token refresh on 401 errors
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  console.log('JWT Interceptor - URL:', req.url);
  console.log('JWT Interceptor - Token exists:', !!token);
  console.log('JWT Interceptor - Token value:', token ? token.substring(0, 20) + '...' : 'null');

  // Skip adding token for auth endpoints
  if (req.url.includes('/auth/login') || req.url.includes('/auth/refresh')) {
    console.log('JWT Interceptor - Skipping auth endpoint');
    return next(req);
  }

  // Clone request and add Authorization header if token exists
  if (token) {
    console.log('JWT Interceptor - Adding Authorization header');
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  } else {
    console.log('JWT Interceptor - No token available!');
  }

  // Handle the request and catch 401 errors
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // If 401 Unauthorized, logout user
      if (error.status === 401 && !req.url.includes('/auth/')) {
        // For now, just logout on 401 (token refresh disabled temporarily)
        authService.logout().subscribe();
      }

      return throwError(() => error);
    })
  );
};

// Made with Bob
