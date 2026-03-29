import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { TripSheetService } from '../../../core/services/trip-sheet.service';
import { ClientService } from '../../../core/services/client.service';
import { VehicleTypeService } from '../../../core/services/vehicle-type.service';
import { TripSheetRequest, ClientResponse, VehicleTypeResponse } from '../../../models';

@Component({
  selector: 'app-trip-sheet-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatCheckboxModule,
    MatDividerModule
  ],
  templateUrl: './trip-sheet-form.html',
  styleUrls: ['./trip-sheet-form.css']
})
export class TripSheetFormComponent implements OnInit {
  tripSheetForm: FormGroup;
  isEditMode = false;
  isViewMode = false;
  tripSheetId: number | null = null;
  loading = false;
  error: string | null = null;
  
  clients: ClientResponse[] = [];
  vehicleTypes: VehicleTypeResponse[] = [];
  
  statusOptions = [
    { value: 'F', label: 'Flat Rate' },
    { value: 'S', label: 'Split Rate' },
    { value: 'O', label: 'Outstation' }
  ];

  constructor(
    private fb: FormBuilder,
    private tripSheetService: TripSheetService,
    private clientService: ClientService,
    private vehicleTypeService: VehicleTypeService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.tripSheetForm = this.fb.group({
      // Basic Info
      trpNum: ['', [Validators.required, Validators.min(1)]],
      trpDate: [new Date(), Validators.required],
      regNum: ['', [Validators.required, Validators.maxLength(14)]],
      driver: ['', Validators.maxLength(25)],
      bookingId: [''],
      
      // Client & Vehicle
      clientId: ['', Validators.required],
      typeId: ['', Validators.required],
      
      // Kilometer Readings
      startKm: [0, [Validators.required, Validators.min(0)]],
      endKm: [0, [Validators.required, Validators.min(0)]],
      
      // Date/Time Range
      startDate: [new Date(), Validators.required],
      endDate: [''],
      startTime: ['', Validators.pattern(/^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$/)],
      endTime: ['', Validators.pattern(/^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$/)],
      
      // Fare Calculations
      hiring: [0, [Validators.min(0)]],
      extra: [0, [Validators.min(0)]],
      halt: [0, [Validators.min(0)]],
      minimum: [0, [Validators.min(0)]],
      time: [0, [Validators.min(0)]],
      days: [0, [Validators.min(0)]],
      permit: [0, [Validators.min(0)]],
      misc: [0, [Validators.min(0)]],
      
      // Billing Info
      isBilled: [false],
      billNumber: [''],
      billDate: [''],
      
      // Additional
      status: ['F'],
      remarks: ['', Validators.maxLength(255)],
      tagged: [false]
    });
  }

  ngOnInit(): void {
    this.loadClients();
    this.loadVehicleTypes();
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.tripSheetId = +id;
      const url = this.router.url;
      this.isViewMode = !url.includes('/edit');
      this.isEditMode = url.includes('/edit');
      
      this.loadTripSheet(this.tripSheetId);
      
      if (this.isViewMode) {
        this.tripSheetForm.disable();
      }
    }
  }

  loadClients(): void {
    this.clientService.getAllClients().subscribe({
      next: (clients) => {
        this.clients = clients;
      },
      error: (error) => {
        console.error('Error loading clients:', error);
      }
    });
  }

  loadVehicleTypes(): void {
    this.vehicleTypeService.getAllVehicleTypes().subscribe({
      next: (types) => {
        this.vehicleTypes = types;
      },
      error: (error) => {
        console.error('Error loading vehicle types:', error);
      }
    });
  }

  loadTripSheet(id: number): void {
    this.loading = true;
    this.tripSheetService.getTripSheetById(id).subscribe({
      next: (tripSheet) => {
        this.tripSheetForm.patchValue({
          trpNum: tripSheet.trpNum,
          trpDate: new Date(tripSheet.trpDate),
          regNum: tripSheet.regNum,
          driver: tripSheet.driver,
          clientId: tripSheet.clientId,
          typeId: tripSheet.typeId,
          startKm: tripSheet.startKm,
          endKm: tripSheet.endKm,
          startDate: new Date(tripSheet.startDate),
          endDate: tripSheet.endDate ? new Date(tripSheet.endDate) : null,
          startTime: tripSheet.startTime,
          endTime: tripSheet.endTime,
          hiring: tripSheet.hiring,
          extra: tripSheet.extra,
          halt: tripSheet.halt,
          minimum: tripSheet.minimum,
          time: tripSheet.time,
          days: tripSheet.days,
          permit: tripSheet.permit,
          misc: tripSheet.misc,
          isBilled: tripSheet.isBilled,
          billNumber: tripSheet.billNumber,
          billDate: tripSheet.billDate ? new Date(tripSheet.billDate) : null,
          status: tripSheet.status,
          remarks: tripSheet.remarks,
          tagged: tripSheet.tagged
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading trip sheet:', error);
        this.error = 'Failed to load trip sheet. Please try again.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.tripSheetForm.invalid) {
      this.tripSheetForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    const formValue = this.tripSheetForm.value;
    const request: TripSheetRequest = {
      trpNum: formValue.trpNum,
      trpDate: this.formatDate(formValue.trpDate),
      regNum: formValue.regNum,
      driver: formValue.driver || undefined,
      bookingId: formValue.bookingId || undefined,
      clientId: formValue.clientId,
      startKm: formValue.startKm,
      endKm: formValue.endKm,
      typeId: formValue.typeId,
      startDate: this.formatDate(formValue.startDate),
      endDate: formValue.endDate ? this.formatDate(formValue.endDate) : undefined,
      startTime: formValue.startTime || undefined,
      endTime: formValue.endTime || undefined,
      hiring: formValue.hiring || 0,
      extra: formValue.extra || 0,
      halt: formValue.halt || 0,
      minimum: formValue.minimum || 0,
      time: formValue.time || 0,
      days: formValue.days || 0,
      permit: formValue.permit || 0,
      misc: formValue.misc || 0,
      isBilled: formValue.isBilled || false,
      billNumber: formValue.billNumber || undefined,
      billDate: formValue.billDate ? this.formatDate(formValue.billDate) : undefined,
      status: formValue.status || undefined,
      remarks: formValue.remarks || undefined,
      tagged: formValue.tagged || false
    };

    const operation = this.isEditMode
      ? this.tripSheetService.updateTripSheet(this.tripSheetId!, request)
      : this.tripSheetService.createTripSheet(request);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/trip-sheets']);
      },
      error: (error) => {
        console.error('Error saving trip sheet:', error);
        this.error = error.error?.message || 'Failed to save trip sheet. Please try again.';
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/trip-sheets']);
  }

  formatDate(date: Date): string {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  getTotalKm(): number {
    const startKm = this.tripSheetForm.get('startKm')?.value || 0;
    const endKm = this.tripSheetForm.get('endKm')?.value || 0;
    return Math.max(0, endKm - startKm);
  }

  getTotalAmount(): number {
    const hiring = this.tripSheetForm.get('hiring')?.value || 0;
    const extra = this.tripSheetForm.get('extra')?.value || 0;
    const halt = this.tripSheetForm.get('halt')?.value || 0;
    const permit = this.tripSheetForm.get('permit')?.value || 0;
    const misc = this.tripSheetForm.get('misc')?.value || 0;
    return hiring + extra + halt + permit + misc;
  }
}

// Made with Bob