import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { AddressResponse } from '../../../models';

@Component({
  selector: 'app-address-details-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule
  ],
  templateUrl: './address-details-dialog.html',
  styleUrls: ['./address-details-dialog.scss']
})
export class AddressDetailsDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<AddressDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public address: AddressResponse
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  edit(): void {
    this.dialogRef.close({ action: 'edit', address: this.address });
  }
}

// Made with Bob