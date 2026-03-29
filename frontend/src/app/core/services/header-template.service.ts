import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { HeaderTemplateResponse, HeaderTemplateRequest, ApiResponse } from '../../models';

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
export class HeaderTemplateService {
  private apiUrl = `${environment.apiUrl}/headertemplates`;

  constructor(private http: HttpClient) {}

  /**
   * Get paginated list of header templates
   */
  getHeaderTemplates(page: number = 0, size: number = 10, sort: string = 'templateName,asc'): Observable<HeaderTemplateResponse[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<PageResponse<HeaderTemplateResponse>>(this.apiUrl, { params }).pipe(
      map(response => response.content)
    );
  }

  /**
   * Get header template by ID
   */
  getHeaderTemplateById(id: number): Observable<HeaderTemplateResponse> {
    return this.http.get<HeaderTemplateResponse>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get default header template
   */
  getDefaultTemplate(): Observable<HeaderTemplateResponse> {
    return this.http.get<HeaderTemplateResponse>(`${this.apiUrl}/default`);
  }

  /**
   * Get active header templates
   */
  getActiveTemplates(): Observable<HeaderTemplateResponse[]> {
    return this.http.get<HeaderTemplateResponse[]>(`${this.apiUrl}/active`);
  }

  /**
   * Create new header template
   */
  createHeaderTemplate(request: HeaderTemplateRequest): Observable<HeaderTemplateResponse> {
    return this.http.post<ApiResponse<HeaderTemplateResponse>>(this.apiUrl, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Update existing header template
   */
  updateHeaderTemplate(id: number, request: HeaderTemplateRequest): Observable<HeaderTemplateResponse> {
    return this.http.put<ApiResponse<HeaderTemplateResponse>>(`${this.apiUrl}/${id}`, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Delete header template
   */
  deleteHeaderTemplate(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Set template as default
   */
  setAsDefault(id: number): Observable<HeaderTemplateResponse> {
    return this.http.patch<ApiResponse<HeaderTemplateResponse>>(`${this.apiUrl}/${id}/set-default`, {}).pipe(
      map(response => response.data)
    );
  }
}

// Made with Bob