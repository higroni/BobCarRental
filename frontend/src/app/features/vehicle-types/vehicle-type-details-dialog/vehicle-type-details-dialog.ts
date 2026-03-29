import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { VehicleTypeResponse } from '../../../models/vehicle.model';

@Component({
  selector: 'app-vehicle-type-details-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule
  ],
  templateUrl: './vehicle-type-details-dialog.html',
  styleUrls: ['./vehicle-type-details-dialog.scss']
})
export class VehicleTypeDetailsDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<VehicleTypeDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public type: VehicleTypeResponse
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  edit(): void {
    this.dialogRef.close({ action: 'edit', type: this.type });
  }
}

// Made with Bob