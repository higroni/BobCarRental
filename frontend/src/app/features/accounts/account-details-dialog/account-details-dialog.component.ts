import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { Router } from '@angular/router';
import { AccountResponse } from '../../../models';

@Component({
  selector: 'app-account-details-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule
  ],
  templateUrl: './account-details-dialog.component.html',
  styleUrl: './account-details-dialog.component.scss'
})
export class AccountDetailsDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<AccountDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public account: AccountResponse,
    private router: Router
  ) {}

  onClose(): void {
    this.dialogRef.close();
  }

  onEdit(): void {
    this.dialogRef.close();
    this.router.navigate(['/accounts', this.account.id, 'edit']);
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) return '0.00';
    return value.toFixed(2);
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}

// Made with Bob