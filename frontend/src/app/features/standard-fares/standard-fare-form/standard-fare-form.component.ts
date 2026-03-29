import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { StandardFareService } from '../../../core/services/standard-fare.service';
import { VehicleTypeService } from '../../../core/services/vehicle-type.service';
import { StandardFareRequest, FARE_TYPES } from '../../../models/fare.model';
import { VehicleTypeResponse } from '../../../models';

@Component({
  selector: 'app-standard-fare-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './standard-fare-form.component.html',
  styleUrl: './standard-fare-form.component.css'
})
export class StandardFareFormComponent implements OnInit {
  fareForm!: FormGroup;
  isEditMode = false;
  fareId: number | null = null;
  isSubmitting = false;
  errorMessage: string = '';
  isLoadingVehicleTypes = false;

  fareTypes = Object.values(FARE_TYPES);
  vehicleTypes: VehicleTypeResponse[] = [];

  constructor(
    private fb: FormBuilder,
    private standardFareService: StandardFareService,
    private vehicleTypeService: VehicleTypeService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadVehicleTypes();
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.fareId = +id;
      this.loadStandardFare(this.fareId);
    }
  }

  loadVehicleTypes(): void {
    this.isLoadingVehicleTypes = true;
    this.vehicleTypeService.getAllVehicleTypes().subscribe({
      next: (vehicleTypes) => {
        this.vehicleTypes = vehicleTypes;
        this.isLoadingVehicleTypes = false;
      },
      error: (error) => {
        console.error('Error loading vehicle types:', error);
        this.errorMessage = 'Failed to load vehicle types';
        this.isLoadingVehicleTypes = false;
      }
    });
  }

  initForm(): void {
    this.fareForm = this.fb.group({
      vehicleCode: ['', [Validators.required]],
      fareType: ['', Validators.required],
      hours: [null, [Validators.min(0)]],
      rate: [null, [Validators.min(0)]],
      freeKm: [null, [Validators.min(0)]],
      splitFare: [null, [Validators.min(0)]],
      splitFuelKm: [null, [Validators.min(0)]],
      hireRate: [null, [Validators.min(0)]],
      hireKm: [null, [Validators.min(0)]],
      fuelRatePerKm: [null, [Validators.min(0)]],
      ratePerExcessKm: [null, [Validators.min(0)]],
      ratePerKm: [null, [Validators.min(0)]],
      minKmPerDay: [null, [Validators.min(0)]],
      nightHalt: [null, [Validators.min(0)]],
      active: [true],
      isActive: [true],
      effectiveFrom: [null],
      effectiveTo: [null],
      description: ['', Validators.maxLength(255)]
    });
  }

  loadStandardFare(id: number): void {
    this.standardFareService.getStandardFareById(id).subscribe({
      next: (fare) => {
        this.fareForm.patchValue({
          vehicleCode: fare.vehicleCode,
          fareType: fare.fareType,
          hours: fare.hours,
          rate: fare.rate,
          freeKm: fare.freeKm,
          splitFare: fare.splitFare,
          splitFuelKm: fare.splitFuelKm,
          hireRate: fare.hireRate,
          hireKm: fare.hireKm,
          fuelRatePerKm: fare.fuelRatePerKm,
          ratePerExcessKm: fare.ratePerExcessKm,
          ratePerKm: fare.ratePerKm,
          minKmPerDay: fare.minKmPerDay,
          nightHalt: fare.nightHalt,
          active: fare.active,
          isActive: fare.isActive,
          effectiveFrom: fare.effectiveFrom ? new Date(fare.effectiveFrom) : null,
          effectiveTo: fare.effectiveTo ? new Date(fare.effectiveTo) : null,
          description: fare.description
        });
      },
      error: (error) => {
        console.error('Error loading standard fare:', error);
        this.errorMessage = 'Failed to load standard fare';
      }
    });
  }

  onSubmit(): void {
    if (this.fareForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const formValue = this.fareForm.value;
    const request: StandardFareRequest = {
      vehicleCode: formValue.vehicleCode,
      fareType: formValue.fareType,
      hours: formValue.hours,
      rate: formValue.rate,
      freeKm: formValue.freeKm,
      splitFare: formValue.splitFare,
      splitFuelKm: formValue.splitFuelKm,
      hireRate: formValue.hireRate,
      hireKm: formValue.hireKm,
      fuelRatePerKm: formValue.fuelRatePerKm,
      ratePerExcessKm: formValue.ratePerExcessKm,
      ratePerKm: formValue.ratePerKm,
      minKmPerDay: formValue.minKmPerDay,
      nightHalt: formValue.nightHalt,
      active: formValue.active,
      isActive: formValue.isActive,
      effectiveFrom: formValue.effectiveFrom ? this.formatDate(formValue.effectiveFrom) : undefined,
      effectiveTo: formValue.effectiveTo ? this.formatDate(formValue.effectiveTo) : undefined,
      description: formValue.description
    };

    const operation = this.isEditMode && this.fareId
      ? this.standardFareService.updateStandardFare(this.fareId, request)
      : this.standardFareService.createStandardFare(request);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/standard-fares']);
      },
      error: (error) => {
        console.error('Error saving standard fare:', error);
        this.errorMessage = error.error?.message || 'Failed to save standard fare';
        this.isSubmitting = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/standard-fares']);
  }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  get selectedFareType(): string {
    return this.fareForm.get('fareType')?.value;
  }

  // Helper methods to show/hide fields based on fare type
  showLocalExtraFields(): boolean {
    const type = this.selectedFareType;
    return type === 'LOCAL' || type === 'EXTRA';
  }

  showGeneralFields(): boolean {
    return this.selectedFareType === 'GENERAL';
  }

  showOutstationFields(): boolean {
    return this.selectedFareType === 'OUTSTATION';
  }
}

// Made with Bob