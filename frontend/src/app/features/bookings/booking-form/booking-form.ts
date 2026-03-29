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
import { BookingService } from '../../../core/services/booking.service';
import { ClientService } from '../../../core/services/client.service';
import { VehicleTypeService } from '../../../core/services/vehicle-type.service';
import { BookingRequest, BookingStatus, ClientResponse, VehicleTypeResponse } from '../../../models';

@Component({
  selector: 'app-booking-form',
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
    MatCheckboxModule
  ],
  templateUrl: './booking-form.html',
  styleUrls: ['./booking-form.css']
})
export class BookingFormComponent implements OnInit {
  bookingForm: FormGroup;
  isEditMode = false;
  isViewMode = false;
  bookingId: number | null = null;
  loading = false;
  error: string | null = null;
  
  clients: ClientResponse[] = [];
  vehicleTypes: VehicleTypeResponse[] = [];

  constructor(
    private fb: FormBuilder,
    private bookingService: BookingService,
    private clientService: ClientService,
    private vehicleTypeService: VehicleTypeService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.bookingForm = this.fb.group({
      ref: [''],
      bookDate: [new Date(), Validators.required],
      todayDate: [new Date(), Validators.required],
      time: ['', [Validators.required, Validators.pattern(/^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$/)]],
      clientId: ['', Validators.required],
      vehicleTypeId: ['', Validators.required],
      info1: [''],
      info2: [''],
      info3: [''],
      info4: [''],
      tagged: [false]
    });
  }

  ngOnInit(): void {
    this.loadClients();
    this.loadVehicleTypes();
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.bookingId = +id;
      
      // Check if this is view mode (no 'edit' in URL) or edit mode
      const url = this.router.url;
      this.isViewMode = !url.includes('/edit');
      this.isEditMode = url.includes('/edit');
      
      this.loadBooking(this.bookingId);
      
      // Disable form in view mode
      if (this.isViewMode) {
        this.bookingForm.disable();
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

  loadBooking(id: number): void {
    this.loading = true;
    this.bookingService.getBookingById(id).subscribe({
      next: (booking) => {
        this.bookingForm.patchValue({
          ref: booking.ref,
          bookDate: new Date(booking.bookDate),
          todayDate: new Date(booking.todayDate),
          time: booking.time,
          clientId: booking.clientId,
          vehicleTypeId: booking.typeId,  // Backend returns typeId, form uses vehicleTypeId
          info1: booking.info1,
          info2: booking.info2,
          info3: booking.info3,
          info4: booking.info4,
          tagged: booking.tagged
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading booking:', error);
        this.error = 'Failed to load booking. Please try again.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.bookingForm.invalid) {
      this.bookingForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    const formValue = this.bookingForm.value;
    
    // IDs are already strings from dropdowns (clientId and typeId)
    const request: BookingRequest = {
      ref: formValue.ref || undefined,
      bookDate: this.formatDate(formValue.bookDate),
      todayDate: this.formatDate(formValue.todayDate),
      time: formValue.time,
      typeId: formValue.vehicleTypeId,
      clientId: formValue.clientId,
      info1: formValue.info1 || undefined,
      info2: formValue.info2 || undefined,
      info3: formValue.info3 || undefined,
      info4: formValue.info4 || undefined,
      tagged: formValue.tagged || false
    };

    const operation = this.isEditMode && this.bookingId
      ? this.bookingService.updateBooking(this.bookingId, request)
      : this.bookingService.createBooking(request);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/bookings']);
      },
      error: (error) => {
        console.error('Error saving booking:', error);
        this.error = error.error?.message || 'Failed to save booking. Please try again.';
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/bookings']);
  }

  private formatDate(date: Date): string {
    if (!date) return '';
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}

// Made with Bob