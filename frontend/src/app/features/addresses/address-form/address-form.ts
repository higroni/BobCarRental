import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AddressService } from '../../../core/services/address.service';
import { AddressRequest, AddressResponse } from '../../../models';

@Component({
  selector: 'app-address-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatSlideToggleModule,
    MatSnackBarModule
  ],
  templateUrl: './address-form.html',
  styleUrls: ['./address-form.scss']
})
export class AddressFormComponent implements OnInit {
  addressForm!: FormGroup;
  isEditMode = false;
  addressId?: number;
  loading = false;
  submitting = false;

  constructor(
    private fb: FormBuilder,
    private addressService: AddressService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.addressId = +id;
      this.loadAddress(this.addressId);
    }
  }

  private initForm(): void {
    this.addressForm = this.fb.group({
      clientId: ['', [Validators.required, Validators.maxLength(10)]],
      dept: ['', [Validators.maxLength(15)]],
      desc: ['', [Validators.maxLength(10)]],
      name: ['', [Validators.required, Validators.maxLength(40)]],
      address1: ['', [Validators.maxLength(35)]],
      address2: ['', [Validators.maxLength(30)]],
      address3: ['', [Validators.maxLength(25)]],
      place: ['', [Validators.maxLength(20)]],
      city: ['', [Validators.maxLength(15)]],
      pinCode: [null],
      phone: ['', [Validators.maxLength(25)]],
      fax: ['', [Validators.maxLength(25)]],
      category: ['', [Validators.maxLength(20)]],
      companyName: ['', [Validators.maxLength(100)]],
      isActive: [true]
    });
  }

  private loadAddress(id: number): void {
    this.loading = true;
    this.addressService.getAddressById(id).subscribe({
      next: (address: AddressResponse) => {
        this.addressForm.patchValue({
          clientId: address.clientId,
          dept: address.dept || '',
          desc: address.desc || '',
          name: address.name,
          address1: address.address1 || '',
          address2: address.address2 || '',
          address3: address.address3 || '',
          place: address.place || '',
          city: address.city || '',
          pinCode: address.pinCode,
          phone: address.phone || '',
          fax: address.fax || '',
          category: address.category || '',
          companyName: address.companyName || '',
          isActive: address.isActive !== undefined ? address.isActive : true
        });
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading address:', error);
        this.snackBar.open('Failed to load address data', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.addressForm.valid) {
      this.submitting = true;
      const request: AddressRequest = this.addressForm.value;

      const operation = this.isEditMode && this.addressId
        ? this.addressService.updateAddress(this.addressId, request)
        : this.addressService.createAddress(request);

      operation.subscribe({
        next: () => {
          this.snackBar.open(
            `Address ${this.isEditMode ? 'updated' : 'created'} successfully`,
            'Close',
            { duration: 3000 }
          );
          this.router.navigate(['/addresses']);
        },
        error: (error: any) => {
          console.error('Error saving address:', error);
          this.snackBar.open('Failed to save address', 'Close', { duration: 3000 });
          this.submitting = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/addresses']);
  }
}

// Made with Bob