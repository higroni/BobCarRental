import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { StandardFareService } from '../../../core/services/standard-fare.service';
import { StandardFareResponse } from '../../../models/fare.model';
import { StandardFareDetailsDialogComponent } from '../standard-fare-details-dialog/standard-fare-details-dialog.component';

@Component({
  selector: 'app-standard-fare-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatChipsModule,
    MatDialogModule,
    FormsModule
  ],
  templateUrl: './standard-fare-list.component.html',
  styleUrl: './standard-fare-list.component.css'
})
export class StandardFareListComponent implements OnInit {
  standardFares: StandardFareResponse[] = [];
  filteredStandardFares: StandardFareResponse[] = [];
  displayedColumns: string[] = ['vehicleCode', 'fareType', 'hours', 'rate', 'freeKm', 'isActive', 'actions'];
  searchTerm: string = '';
  isLoading: boolean = false;
  error: string | null = null;

  constructor(
    private standardFareService: StandardFareService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadStandardFares();
  }

  loadStandardFares(): void {
    this.isLoading = true;
    this.error = null;
    
    this.standardFareService.getAllStandardFares().subscribe({
      next: (fares) => {
        this.standardFares = fares;
        this.filteredStandardFares = fares;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading standard fares:', error);
        this.error = 'Failed to load standard fares. Please try again.';
        this.isLoading = false;
      }
    });
  }

  applyFilter(): void {
    const term = this.searchTerm.toLowerCase().trim();
    
    if (!term) {
      this.filteredStandardFares = this.standardFares;
      return;
    }

    this.filteredStandardFares = this.standardFares.filter(fare =>
      fare.vehicleCode?.toLowerCase().includes(term) ||
      fare.fareType?.toLowerCase().includes(term) ||
      fare.description?.toLowerCase().includes(term)
    );
  }

  viewDetails(fare: StandardFareResponse): void {
    this.dialog.open(StandardFareDetailsDialogComponent, {
      width: '700px',
      data: fare
    });
  }

  editStandardFare(id: number): void {
    this.router.navigate(['/standard-fares', id, 'edit']);
  }

  deleteStandardFare(id: number, vehicleCode: string, fareType: string): void {
    if (confirm(`Are you sure you want to delete fare "${vehicleCode} - ${fareType}"?`)) {
      this.standardFareService.deleteStandardFare(id).subscribe({
        next: () => {
          this.loadStandardFares();
        },
        error: (error) => {
          console.error('Error deleting standard fare:', error);
          alert('Failed to delete standard fare. Please try again.');
        }
      });
    }
  }

  createStandardFare(): void {
    this.router.navigate(['/standard-fares/new']);
  }

  formatCurrency(value: number | undefined | null): string {
    if (value === undefined || value === null || value === 0) {
      return '-';
    }
    // Handle both number and potential string values from backend
    const numValue = typeof value === 'string' ? parseFloat(value) : value;
    if (isNaN(numValue)) {
      return '-';
    }
    return numValue.toFixed(2);
  }

  // Get the appropriate rate field based on fare type
  getDisplayRate(fare: StandardFareResponse): string {
    // Different fare types use different rate fields
    let rateValue: number | null | undefined;
    
    switch (fare.fareType) {
      case 'LOCAL':
      case 'EXTRA':
        rateValue = fare.rate;
        break;
      case 'OUTSTATION':
        rateValue = fare.ratePerKm;
        break;
      case 'GENERAL':
        rateValue = fare.fuelRatePerKm;
        break;
      default:
        rateValue = fare.rate;
    }
    
    return this.formatCurrency(rateValue);
  }
}

// Made with Bob