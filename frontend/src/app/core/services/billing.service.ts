import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { 
  BillingRequest, 
  BillingResponse, 
  BillingSummaryResponse,
  ApiResponse,
  PageResponse 
} from '../../models';

@Injectable({
  providedIn: 'root'
})
export class BillingService {
  private apiUrl = `${environment.apiUrl}/billings`;

  constructor(private http: HttpClient) {}

  getBillings(page = 0, size = 20, sort = 'billDate,desc'): Observable<BillingSummaryResponse[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<ApiResponse<PageResponse<BillingSummaryResponse>>>(this.apiUrl, { params })
      .pipe(map(response => response.data.content));
  }

  getAllBillings(): Observable<BillingSummaryResponse[]> {
    return this.http.get<ApiResponse<BillingSummaryResponse[]>>(`${this.apiUrl}/all`)
      .pipe(map(response => response.data));
  }

  getBillingById(id: number): Observable<BillingResponse> {
    return this.http.get<ApiResponse<BillingResponse>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  getBillingByBillNum(billNum: number): Observable<BillingResponse> {
    return this.http.get<ApiResponse<BillingResponse>>(`${this.apiUrl}/bill-num/${billNum}`)
      .pipe(map(response => response.data));
  }

  createBilling(request: BillingRequest): Observable<BillingResponse> {
    return this.http.post<ApiResponse<BillingResponse>>(this.apiUrl, request)
      .pipe(map(response => response.data));
  }

  updateBilling(id: number, request: BillingRequest): Observable<BillingResponse> {
    return this.http.put<ApiResponse<BillingResponse>>(`${this.apiUrl}/${id}`, request)
      .pipe(map(response => response.data));
  }

  deleteBilling(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  recordPayment(id: number, amount: number, paymentMode?: string): Observable<BillingResponse> {
    const request = { amount, paymentMode };
    return this.http.post<ApiResponse<BillingResponse>>(`${this.apiUrl}/${id}/payment`, request)
      .pipe(map(response => response.data));
  }

  getUnbilledTripSheets(): Observable<any[]> {
    return this.http.get<ApiResponse<any[]>>(`${environment.apiUrl}/trip-sheets/unbilled`)
      .pipe(map(response => response.data));
  }

  generateBillPreview(clientId: string): Observable<string> {
    return this.http.get<ApiResponse<string>>(`${this.apiUrl}/preview/${clientId}`)
      .pipe(map(response => response.data));
  }
}

// Made with Bob