import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { VehicleTypeService } from '../../../core/services/vehicle-type.service';
import { VehicleTypeRequest, VehicleTypeResponse } from '../../../models/vehicle.model';

@Component({
  selector: 'app-vehicle-type-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSlideToggleModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './vehicle-type-form.html',
  styleUrl: './vehicle-type-form.scss'
})
export class VehicleTypeFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private vehicleTypeService = inject(VehicleTypeService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  vehicleTypeForm!: FormGroup;
  isEditMode = false;
  vehicleTypeId?: number;
  loading = false;
  submitting = false;

  ngOnInit(): void {
    this.initForm();
    
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.vehicleTypeId = +params['id'];
        this.loadVehicleType(this.vehicleTypeId);
      }
    });
  }

  private initForm(): void {
    this.vehicleTypeForm = this.fb.group({
      typeId: ['', [Validators.required, Validators.maxLength(5)]],
      typeName: ['', [Validators.required, Validators.maxLength(30)]],
      description: ['', [Validators.maxLength(100)]],
      isActive: [true]
    });
  }

  private loadVehicleType(id: number): void {
    this.loading = true;
    this.vehicleTypeService.getVehicleTypeById(id).subscribe({
      next: (type: VehicleTypeResponse) => {
        this.vehicleTypeForm.patchValue({
          typeId: type.typeId,
          typeName: type.typeName,
          description: type.description || '',
          isActive: type.isActive !== undefined ? type.isActive : true
        });
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading vehicle type:', error);
        this.snackBar.open('Failed to load vehicle type data', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.vehicleTypeForm.valid) {
      this.submitting = true;
      const request: VehicleTypeRequest = this.vehicleTypeForm.value;

      const operation = this.isEditMode
        ? this.vehicleTypeService.updateVehicleType(this.vehicleTypeId!, request)
        : this.vehicleTypeService.createVehicleType(request);

      operation.subscribe({
        next: () => {
          this.snackBar.open(
            `Vehicle type ${this.isEditMode ? 'updated' : 'created'} successfully`,
            'Close',
            { duration: 3000 }
          );
          this.router.navigate(['/vehicle-types']);
        },
        error: (error: any) => {
          console.error('Error saving vehicle type:', error);
          this.snackBar.open(
            `Failed to ${this.isEditMode ? 'update' : 'create'} vehicle type`,
            'Close',
            { duration: 3000 }
          );
          this.submitting = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/vehicle-types']);
  }
}

// Made with Bob