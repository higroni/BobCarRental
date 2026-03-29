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
import { TripSheetService } from '../../../core/services/trip-sheet.service';
import { AuthService } from '../../../core/services/auth.service';
import { TripSheetSummaryResponse, TRIP_STATUS_LABELS } from '../../../models';

@Component({
  selector: 'app-trip-sheet-list',
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
  templateUrl: './trip-sheet-list.html',
  styleUrls: ['./trip-sheet-list.css']
})
export class TripSheetListComponent implements OnInit {
  tripSheets: TripSheetSummaryResponse[] = [];
  filteredTripSheets: TripSheetSummaryResponse[] = [];
  displayedColumns: string[] = ['trpNum', 'trpDate', 'clientName', 'regNum', 'typeName', 'totalKm', 'totalAmount', 'isBilled', 'actions'];
  loading = false;
  error: string | null = null;
  searchTerm = '';

  constructor(
    private tripSheetService: TripSheetService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTripSheets();
  }

  loadTripSheets(): void {
    this.loading = true;
    this.error = null;
    
    this.tripSheetService.getTripSheets().subscribe({
      next: (tripSheets) => {
        this.tripSheets = tripSheets;
        this.filteredTripSheets = tripSheets;
        this.applyFilter();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading trip sheets:', error);
        this.error = 'Failed to load trip sheets. Please try again.';
        this.loading = false;
      }
    });
  }

  getStatusLabel(status: string): string {
    return TRIP_STATUS_LABELS[status as keyof typeof TRIP_STATUS_LABELS] || status;
  }

  createTripSheet(): void {
    this.router.navigate(['/trip-sheets/new']);
  }

  viewTripSheet(id: number): void {
    this.router.navigate(['/trip-sheets', id]);
  }

  editTripSheet(id: number): void {
    this.router.navigate(['/trip-sheets', id, 'edit']);
  }

  deleteTripSheet(tripSheet: TripSheetSummaryResponse): void {
    if (!this.isAdmin()) {
      return;
    }

    const tripNum = tripSheet.trpNum;
    if (!confirm(`Are you sure you want to delete trip sheet #${tripNum}?`)) {
      return;
    }

    this.tripSheetService.deleteTripSheet(tripSheet.id).subscribe({
      next: () => {
        this.loadTripSheets();
      },
      error: (error) => {
        console.error('Error deleting trip sheet:', error);
        this.error = 'Failed to delete trip sheet. Please try again.';
      }
    });
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  canEdit(tripSheet: TripSheetSummaryResponse): boolean {
    return !tripSheet.isBilled;
  }

  canDelete(tripSheet: TripSheetSummaryResponse): boolean {
    return this.isAdmin() && !tripSheet.isBilled;
  }

  onSearchChange(searchValue: string): void {
    this.searchTerm = searchValue;
    this.applyFilter();
  }

  applyFilter(): void {
    if (!this.searchTerm || this.searchTerm.trim() === '') {
      this.filteredTripSheets = this.tripSheets;
      return;
    }

    const searchLower = this.searchTerm.toLowerCase().trim();
    this.filteredTripSheets = this.tripSheets.filter(tripSheet => {
      return (
        (tripSheet.trpNum && tripSheet.trpNum.toString().includes(searchLower)) ||
        (tripSheet.clientName && tripSheet.clientName.toLowerCase().includes(searchLower)) ||
        (tripSheet.clientId && tripSheet.clientId.toLowerCase().includes(searchLower)) ||
        (tripSheet.regNum && tripSheet.regNum.toLowerCase().includes(searchLower)) ||
        (tripSheet.typeName && tripSheet.typeName.toLowerCase().includes(searchLower)) ||
        (tripSheet.typeId && tripSheet.typeId.toLowerCase().includes(searchLower)) ||
        (tripSheet.trpDate && tripSheet.trpDate.toString().includes(searchLower)) ||
        (tripSheet.billNum && tripSheet.billNum.toString().includes(searchLower))
      );
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilter();
  }

  formatCurrency(amount: number | undefined): string {
    if (!amount) return '-';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }
}

// Made with Bob