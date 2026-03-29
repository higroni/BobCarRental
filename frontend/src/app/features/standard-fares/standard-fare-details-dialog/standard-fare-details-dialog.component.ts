import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { Router } from '@angular/router';
import { StandardFareResponse } from '../../../models/fare.model';

@Component({
  selector: 'app-standard-fare-details-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule
  ],
  templateUrl: './standard-fare-details-dialog.component.html',
  styleUrl: './standard-fare-details-dialog.component.scss'
})
export class StandardFareDetailsDialogComponent {
  fare: StandardFareResponse;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: StandardFareResponse,
    private dialogRef: MatDialogRef<StandardFareDetailsDialogComponent>,
    private router: Router
  ) {
    this.fare = data;
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onEdit(): void {
    this.dialogRef.close();
    this.router.navigate(['/standard-fares', this.fare.id, 'edit']);
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) return 'N/A';
    return value.toFixed(2);
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}

// Made with Bob