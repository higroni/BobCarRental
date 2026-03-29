import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BookingService } from '../../../core/services/booking.service';
import { AuthService } from '../../../core/services/auth.service';
import { BookingSummaryResponse, BookingStatus, BOOKING_STATUS_LABELS } from '../../../models';

@Component({
  selector: 'app-booking-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatTooltipModule,
    MatMenuModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './booking-list.html',
  styleUrls: ['./booking-list.css']
})
export class BookingListComponent implements OnInit {
  bookings: BookingSummaryResponse[] = [];
  filteredBookings: BookingSummaryResponse[] = [];
  displayedColumns: string[] = ['ref', 'bookDate', 'time', 'clientName', 'typeName', 'info1', 'status', 'actions'];
  loading = false;
  error: string | null = null;
  searchTerm = '';

  constructor(
    private bookingService: BookingService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.loading = true;
    this.error = null;
    
    this.bookingService.getBookings().subscribe({
      next: (bookings) => {
        this.bookings = bookings;
        this.filteredBookings = bookings;
        this.applyFilter();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading bookings:', error);
        this.error = 'Failed to load bookings. Please try again.';
        this.loading = false;
      }
    });
  }

  getStatusLabel(status: string): string {
    const statusEnum = status as BookingStatus;
    return BOOKING_STATUS_LABELS[statusEnum] || status;
  }

  getStatusClass(status: string): string {
    const statusMap: Record<string, string> = {
      'PENDING': 'status-pending',
      'CONFIRMED': 'status-confirmed',
      'IN_PROGRESS': 'status-in-progress',
      'COMPLETED': 'status-completed',
      'CANCELLED': 'status-cancelled'
    };
    return statusMap[status] || '';
  }

  createBooking(): void {
    this.router.navigate(['/bookings/new']);
  }

  viewBooking(id: number): void {
    this.router.navigate(['/bookings', id]);
  }

  editBooking(id: number): void {
    this.router.navigate(['/bookings', id, 'edit']);
  }

  confirmBooking(booking: BookingSummaryResponse): void {
    if (!this.isAdmin()) {
      return;
    }

    this.bookingService.confirmBooking(booking.id).subscribe({
      next: () => {
        this.loadBookings();
      },
      error: (error) => {
        console.error('Error confirming booking:', error);
        this.error = 'Failed to confirm booking. Please try again.';
      }
    });
  }

  cancelBooking(booking: BookingSummaryResponse): void {
    const bookingRef = booking.ref || `#${booking.id}`;
    if (!confirm(`Are you sure you want to cancel booking "${bookingRef}"?`)) {
      return;
    }

    this.bookingService.cancelBooking(booking.id).subscribe({
      next: () => {
        this.loadBookings();
      },
      error: (error) => {
        console.error('Error cancelling booking:', error);
        this.error = 'Failed to cancel booking. Please try again.';
      }
    });
  }

  completeBooking(booking: BookingSummaryResponse): void {
    if (!this.isAdmin()) {
      return;
    }

    this.bookingService.completeBooking(booking.id).subscribe({
      next: () => {
        this.loadBookings();
      },
      error: (error) => {
        console.error('Error completing booking:', error);
        this.error = 'Failed to complete booking. Please try again.';
      }
    });
  }

  deleteBooking(booking: BookingSummaryResponse): void {
    if (!this.isAdmin()) {
      return;
    }

    const bookingRef = booking.ref || `#${booking.id}`;
    if (!confirm(`Are you sure you want to delete booking "${bookingRef}"?`)) {
      return;
    }

    this.bookingService.deleteBooking(booking.id).subscribe({
      next: () => {
        this.loadBookings();
      },
      error: (error) => {
        console.error('Error deleting booking:', error);
        this.error = 'Failed to delete booking. Please try again.';
      }
    });
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  canConfirm(booking: BookingSummaryResponse): boolean {
    return this.isAdmin() && booking.status === 'PENDING';
  }

  canComplete(booking: BookingSummaryResponse): boolean {
    // Allow completing from CONFIRMED or IN_PROGRESS status
    return this.isAdmin() && (booking.status === 'CONFIRMED' || booking.status === 'IN_PROGRESS');
  }

  canCancel(booking: BookingSummaryResponse): boolean {
    return booking.status !== 'CANCELLED' && booking.status !== 'COMPLETED';
  }

  canEdit(booking: BookingSummaryResponse): boolean {
    return booking.status === 'PENDING' || booking.status === 'CONFIRMED';
  }

  canDelete(booking: BookingSummaryResponse): boolean {
    return this.isAdmin() && booking.status === 'CANCELLED';
  }

  onSearchChange(searchValue: string): void {
    this.searchTerm = searchValue;
    this.applyFilter();
  }

  applyFilter(): void {
    if (!this.searchTerm || this.searchTerm.trim() === '') {
      this.filteredBookings = this.bookings;
      return;
    }

    const searchLower = this.searchTerm.toLowerCase().trim();
    this.filteredBookings = this.bookings.filter(booking => {
      return (
        (booking.ref && booking.ref.toLowerCase().includes(searchLower)) ||
        (booking.clientName && booking.clientName.toLowerCase().includes(searchLower)) ||
        (booking.clientId && booking.clientId.toLowerCase().includes(searchLower)) ||
        (booking.typeName && booking.typeName.toLowerCase().includes(searchLower)) ||
        (booking.typeId && booking.typeId.toLowerCase().includes(searchLower)) ||
        (booking.info1 && booking.info1.toLowerCase().includes(searchLower)) ||
        (booking.status && booking.status.toLowerCase().includes(searchLower)) ||
        (booking.bookDate && booking.bookDate.toString().includes(searchLower))
      );
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilter();
  }
}

// Made with Bob