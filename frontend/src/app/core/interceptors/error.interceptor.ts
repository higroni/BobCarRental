import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Error Interceptor - Handles HTTP errors globally
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An error occurred';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Client Error: ${error.error.message}`;
        console.error('Client-side error:', error.error.message);
      } else {
        // Server-side error
        errorMessage = `Server Error: ${error.status} - ${error.message}`;
        console.error('Server-side error:', {
          status: error.status,
          message: error.message,
          error: error.error
        });

        // Handle specific error codes
        switch (error.status) {
          case 400:
            errorMessage = 'Bad Request: ' + (error.error?.message || 'Invalid data');
            break;
          case 401:
            errorMessage = 'Unauthorized: Please login again';
            // Don't redirect here - let JWT interceptor handle it
            break;
          case 403:
            errorMessage = 'Forbidden: You do not have permission';
            break;
          case 404:
            errorMessage = 'Not Found: ' + (error.error?.message || 'Resource not found');
            break;
          case 409:
            errorMessage = 'Conflict: ' + (error.error?.message || 'Resource already exists');
            break;
          case 500:
            errorMessage = 'Internal Server Error: Please try again later';
            break;
          case 503:
            errorMessage = 'Service Unavailable: Server is temporarily unavailable';
            break;
          default:
            errorMessage = error.error?.message || errorMessage;
        }
      }

      // You can show a toast/snackbar notification here
      // For now, just log to console
      console.error('HTTP Error:', errorMessage);

      // Return error with user-friendly message
      return throwError(() => ({
        status: error.status,
        message: errorMessage,
        originalError: error
      }));
    })
  );
};

// Made with Bob
