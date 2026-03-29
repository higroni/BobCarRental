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
import { ClientService } from '../../../core/services/client.service';
import { ClientRequest, ClientResponse } from '../../../models/client.model';

@Component({
  selector: 'app-client-form',
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
  templateUrl: './client-form.html',
  styleUrl: './client-form.scss'
})
export class ClientFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private clientService = inject(ClientService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  clientForm!: FormGroup;
  isEditMode = false;
  clientId?: number;
  loading = false;
  submitting = false;

  ngOnInit(): void {
    this.initForm();
    
    // Check if we're in edit mode
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.clientId = +params['id'];
        this.loadClient(this.clientId);
      }
    });
  }

  private initForm(): void {
    this.clientForm = this.fb.group({
      clientId: ['', [Validators.required, Validators.maxLength(10)]],
      clientName: ['', [Validators.required, Validators.maxLength(100)]],
      address: ['', [Validators.maxLength(255)]],
      city: ['', [Validators.maxLength(50)]],
      phone: ['', [Validators.maxLength(20)]],
      mobile: ['', [Validators.maxLength(20)]],
      email: ['', [Validators.email, Validators.maxLength(100)]],
      gstNumber: ['', [Validators.maxLength(15)]],
      panNumber: ['', [Validators.maxLength(10)]],
      isActive: [true]
    });
  }

  private loadClient(id: number): void {
    this.loading = true;
    this.clientService.getClientById(id).subscribe({
      next: (client: ClientResponse) => {
        this.clientForm.patchValue({
          clientId: client.clientId,
          clientName: client.clientName,
          address: client.address1 || '',
          city: client.city,
          phone: client.phone,
          mobile: '',  // Backend doesn't have mobile field
          email: '',   // Backend doesn't have email field
          panNumber: '', // Backend doesn't have panNumber field
          gstNumber: '', // Backend doesn't have gstNumber field
          isActive: !client.deleted
        });
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading client:', error);
        this.snackBar.open('Failed to load client data', 'Close', { duration: 3000 });
        this.loading = false;
        this.router.navigate(['/clients']);
      }
    });
  }

  onSubmit(): void {
    if (this.clientForm.invalid) {
      this.clientForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const clientData: ClientRequest = this.clientForm.value;

    const operation = this.isEditMode && this.clientId
      ? this.clientService.updateClient(this.clientId, clientData)
      : this.clientService.createClient(clientData);

    operation.subscribe({
      next: () => {
        const message = this.isEditMode ? 'Client updated successfully' : 'Client created successfully';
        this.snackBar.open(message, 'Close', { duration: 3000 });
        this.router.navigate(['/clients']);
      },
      error: (error: any) => {
        console.error('Error saving client:', error);
        const message = error.error?.message || 'Failed to save client';
        this.snackBar.open(message, 'Close', { duration: 5000 });
        this.submitting = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/clients']);
  }

  getErrorMessage(fieldName: string): string {
    const control = this.clientForm.get(fieldName);
    if (!control || !control.errors || !control.touched) {
      return '';
    }

    if (control.errors['required']) {
      return `${this.getFieldLabel(fieldName)} is required`;
    }
    if (control.errors['pattern']) {
      if (fieldName === 'clientId') {
        return 'Client ID must be uppercase letters and numbers only';
      }
      if (fieldName === 'pinCode') {
        return 'PIN Code must contain only digits';
      }
      return 'Invalid format';
    }
    if (control.errors['maxlength']) {
      return `${this.getFieldLabel(fieldName)} must not exceed ${control.errors['maxlength'].requiredLength} characters`;
    }

    return '';
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      clientId: 'Client ID',
      clientName: 'Client Name',
      address1: 'Address Line 1',
      address2: 'Address Line 2',
      address3: 'Address Line 3',
      place: 'Place',
      city: 'City',
      pinCode: 'PIN Code',
      phone: 'Phone',
      fax: 'Fax',
      fare: 'Fare Information',
      isSplit: 'Split Rate',
      tagged: 'Tagged'
    };
    return labels[fieldName] || fieldName;
  }
}

// Made with Bob
